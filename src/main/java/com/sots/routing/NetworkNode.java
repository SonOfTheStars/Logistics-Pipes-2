package com.sots.routing;

import java.util.UUID;

import com.sots.routing.interfaces.IRoutable;

public class NetworkNode {
	private NetworkNode[] neighbors = new NetworkNode[6];
	private UUID id;
	private IRoutable pipe;
	
	public NetworkNode(UUID id, IRoutable pipe) {
		this.id=id;
		this.pipe=pipe;
	}

	public void addNeighbor(NetworkNode node, int index) {
		neighbors[index]=node;
	}
	
	public boolean isRoutedNode() {
		return pipe.isRouted();
	}
	
	public boolean isNodeRoutable() {
		return pipe.isRoutable();
	}
	
	public void dissolve() {
		pipe.disconnect();
	}
	
	public UUID getId() {return id;}
	
	public IRoutable getMember() {return pipe;}
}
