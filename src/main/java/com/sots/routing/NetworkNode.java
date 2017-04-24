package com.sots.routing;

import java.util.UUID;

import com.sots.routing.interfaces.IRoutable;
import com.sots.util.data.Tuple;

import net.minecraft.util.EnumFacing;

public class NetworkNode {
	private NetworkNode[] neighbors = new NetworkNode[6];
	private Tuple<NetworkNode, EnumFacing> predecessor = null;
	public int gWeight = 0;
	public int fWeight = 0;
	private UUID id;
	private IRoutable pipe;
	
	public NetworkNode(UUID id, IRoutable pipe) {
		this.id=id;
		this.pipe=pipe;
	}

	public void addNeighbor(NetworkNode node, int index) {
		neighbors[index]=node;
	}
	
	public NetworkNode getNeighborAt(int index) {
		return neighbors[index];
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

	public Tuple<NetworkNode, EnumFacing> getPredecessor() {return predecessor;}

	public void setPredecessor(Tuple<NetworkNode, EnumFacing> tuple) {
		this.predecessor = tuple;
	}
}
