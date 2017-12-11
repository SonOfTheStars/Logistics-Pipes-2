package com.sots.routing.router;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import com.sots.LogisticsPipes2;
import com.sots.routing.NetworkNode;
import com.sots.routing.WeightedNetworkNode;
import com.sots.util.data.Triple;
import com.sots.util.data.Tuple;

import net.minecraft.util.EnumFacing;

public class MultiCachedDijkstraRouter{
	private static final int NUM_THREADS = 4;
	//WeightedNetworkNode start, target;
	//private ExecutorService executor = Executors.newSingleThreadExecutor();
	private ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
	private Queue<WeightedNetworkNode> unvisited = new LinkedBlockingQueue<WeightedNetworkNode>();
	private Queue<WeightedNetworkNode> visited = new LinkedBlockingQueue<WeightedNetworkNode>();
	private Triple<NetworkNode, NetworkNode, Deque<Tuple<UUID, EnumFacing>>> routingInfo;

	protected volatile Map<UUID, WeightedNetworkNode> junctions;
	protected volatile Map<UUID, Tuple<NetworkNode, EnumFacing>> destinations;
	protected volatile Map<UUID, NetworkNode> nodes;

	//private volatile Map<Tuple<NetworkNode, NetworkNode>, Triple<NetworkNode, NetworkNode, Deque<Tuple<UUID, EnumFacing>>>> cache = new HashMap<Tuple<NetworkNode, NetworkNode>, Triple<NetworkNode, NetworkNode, Deque<Tuple<UUID, EnumFacing>>>>();
	private volatile Map<Tuple<NetworkNode, NetworkNode>, Deque<Tuple<UUID, EnumFacing>>> cache = new HashMap<Tuple<NetworkNode, NetworkNode>, Deque<Tuple<UUID, EnumFacing>>>();

	protected volatile Set<UUID> sources = new HashSet<UUID>();

	public MultiCachedDijkstraRouter(Map<UUID, WeightedNetworkNode> junctions, Map<UUID, Tuple<NetworkNode, EnumFacing>> destinations, Map<UUID, NetworkNode> nodes) {
		this.junctions = junctions; //I am not sure if these two Maps will be kept updated with each other
		this.destinations = destinations;
		this.nodes = nodes;
	}

	/**
	 * The first part of the output is a boolean, which is false if the route has not yet been calculated, and is true when the route has been calculated
	 * The second part of the output is a triple consisting of the start node, the target node and the route from the start node to the target node
	 */
	public Tuple<Boolean, Deque<Tuple<UUID, EnumFacing>>> route(NetworkNode s, NetworkNode t) {
		Tuple<NetworkNode, NetworkNode> input = new Tuple<NetworkNode, NetworkNode>(s, t);

		while (sources.contains(s.getId())) {}

		if (cache.containsKey(input)) {
			return new Tuple<Boolean, Deque<Tuple<UUID, EnumFacing>>>(true, cache.get(input));
		}

		Tuple<Boolean, Deque<Tuple<UUID, EnumFacing>>> result = doActualRouting(s, t);
		return result;
	}

	public Tuple<Boolean, Deque<Tuple<UUID, EnumFacing>>> doActualRouting(NetworkNode s, NetworkNode t) {
		WeightedNetworkNode start, target;

		Map<UUID, WeightedNetworkNode> junctions = new HashMap<UUID, WeightedNetworkNode>(this.junctions);
		Map<UUID, Tuple<NetworkNode, EnumFacing>> destinations = new HashMap<UUID, Tuple<NetworkNode, EnumFacing>>(this.destinations);
		Map<UUID, NetworkNode> nodes = new HashMap<UUID, NetworkNode>(this.nodes);

		if (junctions.containsKey(s.getId())) {
			start = junctions.get(s.getId());
		} else {
			LogisticsPipes2.logger.info("You tried routing from a node, which was not a destination or junction.");
			return null;
		}

		if (junctions.containsKey(t.getId())) {
			target = junctions.get(t.getId());
		} else {
			LogisticsPipes2.logger.info("You tried routing to a node, which was not a destination or junction.");
			return null;
		}

		sources.add(start.getId());

		Tuple<Boolean, Deque<Tuple<UUID, EnumFacing>>> result = new Tuple<Boolean, Deque<Tuple<UUID, EnumFacing>>>(false, null);

		FutureTask<Void> routingTask =
			new FutureTask<Void>(
					new Callable<Void>() {
						@Override
						public Void call() 
								throws Exception {
							start.p_cost=0;
							start.parent=null;
							unvisited.add(start);
							while(!unvisited.isEmpty()) {
								WeightedNetworkNode current = unvisited.poll();
								current.getMember().spawnParticle(0f, 1.000f, 0f);
								//Thread.sleep(120);

								for (int i = 0; i < 6; i++) {
									Tuple<WeightedNetworkNode, Integer> neighborT = current.weightedNeighbors[i];
									if (neighborT == null) {
										continue;
									}

									WeightedNetworkNode neighbor = neighborT.getKey();
									int distance = neighborT.getVal();
									if (!(unvisited.contains(neighbor) || visited.contains(neighbor))) {
										neighbor.p_cost = Integer.MAX_VALUE;
										unvisited.add(neighbor);
										neighbor.getMember().spawnParticle(0.502f, 0.000f, 0.502f);
									}
									if (current.p_cost + distance < neighbor.p_cost) {
										neighbor.p_cost = current.p_cost + distance;
										neighbor.parent = new Tuple<NetworkNode, EnumFacing>(current, EnumFacing.getFront(i));
									}
								}

								visited.add(current);
								//Thread.sleep(120);
							}
							for (Tuple<NetworkNode, EnumFacing> n : destinations.values()) {
								//NetworkNode help = n;
								WeightedNetworkNode help = junctions.get(n.getKey().getId());

								Deque<Tuple<UUID, EnumFacing>> route = new ArrayDeque<Tuple<UUID, EnumFacing>>();
								route.push(new Tuple<UUID, EnumFacing>(null, n.getVal()));
								while(help.parent != null) {
									pushToRouteUntillParent(help, route);

									help.getMember().spawnParticle(1.0f, 0.549f, 0.0f);
									//help = help.parent.getKey();
									help = junctions.get(help.parent.getKey().getId());
								}
								Tuple<NetworkNode, NetworkNode> input = new Tuple<NetworkNode, NetworkNode>((NetworkNode) start, n.getKey());
								Deque<Tuple<UUID, EnumFacing>> tmp_result = route;
								cache.put(input, tmp_result);
								//LogisticsPipes2.logger.info("Route found of length " + route.size());
							}
							result.setVal(cache.get(new Tuple<NetworkNode, NetworkNode>(start, target)));
							result.setKey(true);
							LogisticsPipes2.logger.info("Done routing for now " + start + "-" + target);
							sources.remove(start.getId());
							return null;
							//return cache.get(new Tuple<NetworkNode, NetworkNode>(start, target));
						}


					});
		executor.execute(routingTask);
		//try {
			//routingInfo = routingTask.get();
			////executor.shutdownNow();
		//}
		//catch (Exception e) {
			//CrashReport.makeCrashReport(e, "A logistics Pipes router was interrupted!");
		//}


		return result;
	}

	private void pushToRouteUntillParent(NetworkNode current, Deque<Tuple<UUID, EnumFacing>> route) {
		NetworkNode parent = current.parent.getKey();
		EnumFacing direction = current.parent.getVal();
		int parentDirection = direction.getOpposite().getIndex();

		NetworkNode help = current;
		while(help.getId() != parent.getId()) {
			help = help.getNeighborAt(parentDirection);
			route.push(new Tuple<UUID, EnumFacing>(help.getId(), direction));
			help.getMember().spawnParticle(1.0f, 0.0f, 0.0f);
			//Thread.sleep(120);
			//LogisticsPipes2.logger.info(route.size());
		}
	}

	public void clean() {
		unvisited.clear();
		visited.clear();
		//executor = Executors.newSingleThreadExecutor();
		executor = Executors.newFixedThreadPool(NUM_THREADS);
	}

	public void shutdown() {
		executor.shutdownNow();
		//executor = Executors.newSingleThreadExecutor();
		executor = Executors.newFixedThreadPool(NUM_THREADS);
		cache.clear();
	}
}
