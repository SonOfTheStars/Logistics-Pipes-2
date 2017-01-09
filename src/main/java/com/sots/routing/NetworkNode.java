package com.sots.routing;

import java.util.ArrayList;
import java.util.List;

public class NetworkNode {
	private List<NetworkNode> neighbors = new ArrayList<NetworkNode>();
	private int nodeLevel;
	
	public NetworkNode(ArrayList<NetworkNode> next, int level, boolean root) {
		if(root) {
			setNodeLevel(0);
		}
		else {
			setNodeLevel(level);
		}
		
		if(next != null && !next.isEmpty()) {
			for(NetworkNode node : next){
				if(!neighbors.contains(node))
					neighbors.add(node);
				
			}
		}
		else if(!root){
			throw new IllegalArgumentException("Non Root nodes may not be created without Connections");
		}
	}

	/**
	 * @return the nodeLevel
	 */
	public int getNodeLevel() {
		return nodeLevel;
	}

	/**
	 * @param nodeLevel the nodeLevel to set
	 */
	public void setNodeLevel(int nodeLevel) {
		this.nodeLevel = nodeLevel;
	}
	
	public boolean addNeighbor(NetworkNode node) {
		
	}
}
