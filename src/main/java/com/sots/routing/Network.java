package com.sots.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.routing.interfaces.IDestination;
import com.sots.routing.interfaces.IRoutable;

public class Network {
	private volatile List<IDestination> destinations = new ArrayList<IDestination>();
	private volatile List<WeightedEdge> wEdges = new ArrayList<WeightedEdge>();
	private volatile Map<UUID, NetworkNode> nodes = new HashMap<UUID, NetworkNode>();
	private NetworkNode root = null;
	
	private UUID name;
	private int ID_RANGE=1;
	
	public Network(UUID n) {
		name=n;
	}
	
	public void registerDestination(IDestination in) {
		if(!destinations.contains(in)) {
			destinations.add(in);
		}
		else {
			LogisticsPipes2.logger.log(Level.WARN, "Tried to register " + in.name + " twice in Network " + name);
		}
	}
	
	public UUID subscribeNode(IRoutable Pipe) {
		UUID id = UUID.randomUUID();
		NetworkNode node = new NetworkNode(id, Pipe);
		nodes.put(id, node);
		Pipe.subscribe(this);
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
		nodes.put(root.getId(), root);
	}
	
	
	
	public NetworkNode getNodeByID(UUID id) {
		return nodes.get(id);
	}
	
	@Deprecated
	public void discover(NetworkNode node) {
//		NetworkNode nodeHelper;
//		TileGenericPipe pipe = (TileGenericPipe)node.getMember();
//		
//		for(int i=0; i<6; i++) {
//			if(ConnectionHelper.isPipe(pipe.getWorld(), pipe.getPos(), EnumFacing.getFront(i))) {
//				TileGenericPipe adj = (TileGenericPipe)AccessHelper.getTileSafe(pipe.getWorld(), pipe.getPos(), EnumFacing.getFront(i));
//				if(!adj.hasNetwork()) {
//					nodeHelper = new NetworkNode(node,ID_Range,adj);
//					adj.network(this);
//					ID_Range++;
//					if(!nodes.contains(nodeHelper))
//						nodes.add(nodeHelper);
//					discover(nodeHelper);
//				}
//				
//			}
//		}
	}
	
	public NetworkNode getRoot() {
		return root;
	}

	public String getName() {
		return name.toString();
	}
	
}
