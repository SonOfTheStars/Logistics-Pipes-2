package com.sots.routing.router;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import com.sots.routing.NetworkNode;
import com.sots.util.data.Triple;
import com.sots.util.data.Tuple;

import net.minecraft.util.EnumFacing;
//A* Implementation for routing inside the 6deg Network map
public class LPRouter {
	private NetworkNode target;
	private static Comparator<NetworkNode> comp = new WeightComparator();
	public static PriorityQueue<NetworkNode> openList = new PriorityQueue<NetworkNode>(10, comp);
	public static Set<NetworkNode> closedList = new HashSet<NetworkNode>();
	
	public Triple<NetworkNode, NetworkNode, ArrayList<Tuple<NetworkNode, EnumFacing>>> route(NetworkNode start, NetworkNode target) {
		this.target = target;
		openList.add(start);
		while(!openList.isEmpty()) {
			NetworkNode currentNode = openList.poll();
			
			if(currentNode == target) {
				NetworkNode helper = currentNode;
				ArrayList<Tuple<NetworkNode, EnumFacing>> route = new ArrayList<Tuple<NetworkNode, EnumFacing>>();
				while(helper.getPredecessor()!=null) {
					route.add(currentNode.getPredecessor());
					helper = currentNode.getPredecessor().getKey();
				}
				Triple<NetworkNode, NetworkNode, ArrayList<Tuple<NetworkNode, EnumFacing>>> routingInfo = new Triple<NetworkNode, NetworkNode, ArrayList<Tuple<NetworkNode, EnumFacing>>>(start, target, route);
				return routingInfo;
			}
			
			closedList.add(currentNode);
			
			expandSearch(currentNode);
		}
		return null;
	}
	
	
	private void expandSearch(NetworkNode current) {
		for(int i=0; i<6; i++) {
			if(current.getNeighborAt(i)!=null) {
				NetworkNode successor = current.getNeighborAt(i);
				
				//Ignore node if it has already been visited.
				if(closedList.contains(successor))
					continue;
				
				//New paths gValue. As the cost of moving to an additional pipe is always 1, we can take 2x the current paths weight + 1
				int tenative_g = current.gWeight*2 + 1;
				
				//If the successor is already part of the openList but the new path is not better than the old one, skip.
				if(openList.contains(successor) && tenative_g >= successor.gWeight)
					continue;
				
				successor.setPredecessor(new Tuple<NetworkNode, EnumFacing>(current, EnumFacing.getFront(i)));//We do NOT want the opposite facing here, since we backtrace the actual route later.
				successor.gWeight = tenative_g;
				
				successor.fWeight = tenative_g +h(successor);
				if(openList.contains(successor)) {
					openList.remove(successor);
					openList.add(successor);
				}
				else {
					openList.add(successor);
				}
			}
			
		}
	}
	
	/**
	 * @return Returns the Heuristic Distance h between @param node and the routers target, as sum of their distances on each axis.
	 */
	private int h(NetworkNode node) {
		return (Math.abs(node.getMember().posX() - target.getMember().posX())
				+ Math.abs(node.getMember().posY() - target.getMember().posY())
				+ Math.abs(node.getMember().posZ() - target.getMember().posZ()));
	}
}
