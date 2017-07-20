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
	private boolean isDestination;
	
	public NetworkNode(UUID id, IRoutable pipe) {
		this.id=id;
		this.pipe=pipe;
		this.isDestination=false;
	}

	public void addNeighbor(NetworkNode node, int index) {
		neighbors[index]=node;
	}
	
	/**
	 * Returns a specific neighbor of this node, specified by the index.
	 * @param index Integer parameter representing the EnumFacing of the neighbor. Order is D-U-N-S-W-E (0-5)
	 * @return Specific Node neighbor for the given side.
	 */
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
	
	/**
	 * Returns the Integer count of connections this pipe holds
	 * @return Integer between 0 and 6
	 */
	public int connections() {
		int out = 0;
		for(NetworkNode item : neighbors) {
			if(item!=null) {
				out++;
			}
		}
		return out;
	}
	/**
	 * Wether or not this Node is a Destination
	 */
	public boolean isDestination() {return isDestination;}
	
	public void setAsDestination(boolean isDestination) {this.isDestination = isDestination;}
	
	/**
	 * Get the Manhattan distance to the Target
	 * @param target The Target Node
	 */
	public void h_Cost(NetworkNode target) {
		h_cost = ((Math.abs(target.pipe.posX() - pipe.posX())+(Math.abs(target.pipe.posY() - pipe.posY())+(Math.abs(target.pipe.posZ() - pipe.posZ())))));
	}
}
