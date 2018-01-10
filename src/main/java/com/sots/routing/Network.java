package com.sots.routing;

import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.routing.interfaces.IRoutable;
import com.sots.routing.router.MultiCachedDijkstraRouter;
import com.sots.util.data.Triple;
import com.sots.util.data.Tuple;

import net.minecraft.util.EnumFacing;

public class Network {
	private volatile Map<UUID, Tuple<NetworkNode, EnumFacing>> destinations = new HashMap<UUID, Tuple<NetworkNode, EnumFacing>>();
	private volatile Map<UUID, NetworkNode> nodes = new HashMap<UUID, NetworkNode>();

	private volatile Map<UUID, WeightedNetworkNode> junctions = new ConcurrentHashMap<UUID, WeightedNetworkNode>(); // Contains only nodes which have 3 or more neighbors or are destinations. All nodes in this map have other junctions or destinations listed as neighbors

	private NetworkNode root = null;
	private NetworkSimplifier networkSimplifier = new NetworkSimplifier();
	
	private MultiCachedDijkstraRouter router;
	
	private UUID name;
	
	public Network(UUID n) {
		name=n; 
		router=new MultiCachedDijkstraRouter(junctions, destinations, nodes);
	}
	
	public void registerDestination(UUID in, EnumFacing dir) {
		if(!destinations.containsKey(in)) {
			destinations.put(in, new Tuple<NetworkNode, EnumFacing>(getNodeByID(in), dir));
			networkSimplifier.shutdown();
			networkSimplifier.rescanNetwork(nodes, destinations, junctions);
			getNodeByID(in).setAsDestination(true);
			LogisticsPipes2.logger.log(Level.INFO, "Registered destination [" + in + "] in network [" + name + "]");
		}
		else {
			LogisticsPipes2.logger.log(Level.WARN, "Tried to register destination [" + in + "] twice in network [" + name + "]");
		}
	}

	public void unregisterDestination(UUID out) {
		if (destinations.containsKey(out)) {
			destinations.remove(out);
			networkSimplifier.shutdown();
			networkSimplifier.rescanNetwork(nodes, destinations, junctions);
			getNodeByID(out).setAsDestination(false);
			LogisticsPipes2.logger.log(Level.INFO, "Unregistered destination [" + out + "] in network [" + name + "]");
		}
		else {
			LogisticsPipes2.logger.log(Level.WARN, "Tried to unregister destination [" + out + "] twice in network [" + name + "]");
		}


	}

	public EnumFacing getDirectionForDestination(UUID node) {
		if (destinations.containsKey(node)) {
			return destinations.get(node).getVal();
		}
		return null;
	}

	public Set<UUID> getAllDestinations() {
		return destinations.keySet();
	}

	public UUID subscribeNode(IRoutable Pipe) {
		UUID id = UUID.randomUUID();
		NetworkNode node = new NetworkNode(id, Pipe);
		nodes.put(id, node);
		Pipe.subscribe(this);

		recalculateNetwork();
		router.shutdown();

		return id;
	}

	public UUID setRoot(IRoutable pipe) {
		if(root==null) {
			UUID id = UUID.randomUUID();
			NetworkNode node = new NetworkNode(id, pipe);
			nodes.put(id, node);
			root = node;
			return id;
		}
		return UUID.fromString("00000000-0000-0000-0000-000000000000");
	}
	
	public void purgeNetwork() {
		Set<Entry<UUID, NetworkNode>> _nodes = nodes.entrySet();
		for(Entry<UUID, NetworkNode> e : _nodes) {
			if(e.getKey()!=root.getId())
				e.getValue().dissolve();
		}
		nodes.clear();
		destinations.clear();
		junctions.clear();
		nodes.put(root.getId(), root);
		router.shutdown();
	}
	
	
	
	public NetworkNode getNodeByID(UUID id) {
		return nodes.get(id);
	}
	
	public NetworkNode getRoot() {
		return root;
	}

	public String getName() {
		return name.toString();
	}
	
	public List<Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>>> getAllRoutesFrom(UUID nodeId){
		NetworkNode start = destinations.get(nodeId).getKey();
		List<Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>>> routes = new ArrayList<Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>>>();;
		Set<UUID> keys = destinations.keySet();
		for(UUID key : keys) {
			NetworkNode dest = destinations.get(key).getKey();
			if(dest.getId() != start.getId()) {
				routes.add(router.route(start, dest));
				router.clean();
				//LogisticsPipes2.logger.info(String.format("A route from Pipe [ %s ] to Pipe [ %s ] has %s",start.getId().toString(), dest.getId().toString(), (route!= null ? "" : "not") + " been found!"));
			}
		}
		return routes;
	}
	
	public Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>> getRouteFromTo(UUID nodeS, UUID nodeT) {
		Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>> route = null;
		if(nodeS != nodeT) {
			//NetworkNode start = destinations.get(nodeS).getKey();
			NetworkNode target = destinations.get(nodeT).getKey();
			
			route = router.route(nodes.get(nodeS), target);
			//route = router.route(start, target);
			router.clean();
			//LogisticsPipes2.logger.info(String.format("A route from Pipe [ %s ] to Pipe [ %s ] has %s",start.getId().toString(), target.getId().toString(), (route!= null ? "" : "not") + " been found!"));
		}
		return route;
	}

	public void recalculateNetwork() {
		networkSimplifier.shutdown();
		networkSimplifier.rescanNetwork(nodes, destinations, junctions);
	}

	/*
	 * Takes a UUID of a node in the network, and a list of destinations in the network, and returns the list of destinations sorted by the length of the route from nodeS to the node in the list.
	 * The list should not be accessed until the boolean in the Tuple is true.
	 */
	public Tuple<Boolean, List<UUID>> getListOfDestinationsOrderedByRoute(UUID nodeS, List<UUID> nodeTs) {
		ExecutorService executor = Executors.newSingleThreadExecutor();

		Tuple<Boolean, List<UUID>> result = new Tuple<Boolean, List<UUID>>(false, new ArrayList<UUID>());

		FutureTask<Void> listingTask = new FutureTask<Void>(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				List<Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>>> routes = new ArrayList<Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>>>();
				for (UUID nodeT : nodeTs) {
					routes.add(getRouteFromTo(nodeS, nodeT));
				}
				for (Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>> route : routes) {
					while (route.getKey() == false) {}
				}

				Collections.sort(routes, new Comparator<Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>>>() {
					@Override
					public int compare(Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>> o1, Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>> o2) {
						return o1.getVal().getThird().size() - o2.getVal().getThird().size();
					}
				});
				for (Tuple<Boolean, Triple<NetworkNode, NetworkNode, Deque<EnumFacing>>> route : routes) {
					result.getVal().add(route.getVal().getSecnd().getId());
				}
				result.setKey(true);
				return null;
			}
		});
		executor.execute(listingTask);

		return result;
	}


}
