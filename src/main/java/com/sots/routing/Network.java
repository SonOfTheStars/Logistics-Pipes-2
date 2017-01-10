package com.sots.routing;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.routing.interfaces.IDestination;
import com.sots.routing.interfaces.IRoutable;
import com.sots.tiles.TileGenericPipe;
import com.sots.util.AccessHelper;
import com.sots.util.ConnectionHelper;

import net.minecraft.util.EnumFacing;

public class Network {
	private volatile List<IDestination> destinations = new ArrayList<IDestination>();
	private volatile List<WeightedEdge> wEdges = new ArrayList<WeightedEdge>();
	private volatile List<NetworkNode> nodes = new ArrayList<NetworkNode>();
	private NetworkNode root;
	
	private String name;
	private int ID_Range=1;
	
	public Network(String n, IRoutable master) {
		name=n;
		
		root = new NetworkNode(null, 0, master);
	}
	
	public void registerDestination(IDestination in) {
		if(!destinations.contains(in)) {
			destinations.add(in);
		}
		else {
			LogisticsPipes2.logger.log(Level.WARN, "Tried to register " + in.name + " twice in Network " + name);
		}
	}
	
	public void addNode() {
		
	}
	
	
	
	public void discover(NetworkNode node) {
		NetworkNode nodeHelper;
		TileGenericPipe pipe = (TileGenericPipe)node.getMember();
		
		for(int i=0; i<6; i++) {
			if(ConnectionHelper.isPipe(pipe.getWorld(), pipe.getPos(), EnumFacing.getFront(i))) {
				TileGenericPipe adj = (TileGenericPipe)AccessHelper.getTileSafe(pipe.getWorld(), pipe.getPos(), EnumFacing.getFront(i));
				if(!adj.hasNetwork()) {
					nodeHelper = new NetworkNode(node,ID_Range,adj);
					adj.network();
					ID_Range++;
					nodes.add(nodeHelper);
					discover(nodeHelper);
				}
				
			}
		}
	}
	
	public NetworkNode getRoot() {
		return root;
	}
	
}
