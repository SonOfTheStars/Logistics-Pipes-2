package com.sots.routing;

import java.util.ArrayList;
import java.util.List;

import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;

public class NetworkNode {
	private List<NetworkNode> neighbors = new ArrayList<NetworkNode>();
	private int id;
	private IRoutable pipe;
	
	public NetworkNode(NetworkNode adj, int id, IRoutable pipe) {
		neighbors.add(adj);
		this.id=id;
		this.pipe=pipe;
	}

	public void addNeighbor(NetworkNode node) {
		neighbors.add(node);
	}
	
	public boolean isRoutedNode() {
		return pipe.isRouted();
	}
	
	public boolean isNodeRoutable() {
		return pipe.isRoutable();
	}
	
	public int getId() {
		return id;
	}
	
	public IRoutable getMember() {return pipe;}
}
