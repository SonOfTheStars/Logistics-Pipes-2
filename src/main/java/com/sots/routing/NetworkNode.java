package com.sots.routing;

import java.util.UUID;

import com.sots.routing.interfaces.IRoutable;
import com.sots.util.data.Tuple;

import net.minecraft.util.EnumFacing;

public class NetworkNode {
	private NetworkNode[] neighbors = new NetworkNode[6];
	public Tuple<NetworkNode, EnumFacing> parent = null;
	public int h_cost = 0;
	public int p_cost = 0;
	public int t_cost = 1;
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
	
	public void h_Cost(NetworkNode target) {
		h_cost = ((Math.abs(target.pipe.posX() - pipe.posX())+(Math.abs(target.pipe.posY() - pipe.posY())+(Math.abs(target.pipe.posZ() - pipe.posZ())))));
	}
}
