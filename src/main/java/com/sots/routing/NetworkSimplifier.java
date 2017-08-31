package com.sots.routing;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import com.sots.LogisticsPipes2;
import com.sots.util.data.Tuple;

import net.minecraft.util.EnumFacing;

public class NetworkSimplifier {

	public static void rescanNetwork(Map<UUID, NetworkNode> nodes, Map<UUID, Tuple<NetworkNode, EnumFacing>> destinations, Map<UUID, WeightedNetworkNode> junctions) {
		NetworkNode first = nodes.entrySet().iterator().next().getValue();

		junctions.clear();
		createWeightedNode(first, junctions, destinations);
	}

	private static WeightedNetworkNode createWeightedNode(NetworkNode node, Map<UUID, WeightedNetworkNode> results, Map<UUID, Tuple<NetworkNode, EnumFacing>> destinations) {
		if (results.containsKey(node.getId()))
			return results.get(node.getId());

		WeightedNetworkNode current = new WeightedNetworkNode(node.getId(), node.getMember());
		for (int i = 0; i < 6; i++)	{
			current.addNeighbor(node.getNeighborAt(i), i);
		}

		current.getMember().spawnParticle(1.0f, 1.0f, 0.0f);
		results.put(current.getId(), current);
		for (int i = 0; i < 6; i++) {
			Tuple<NetworkNode, Integer> neighbor = getNextNeighborAt(node, i, 0, destinations);
			if (neighbor == null) {
				continue;
			}
			current.weightedNeighbors[i] = new Tuple<WeightedNetworkNode, Integer>(createWeightedNode(neighbor.getKey(), results, destinations), neighbor.getVal());
		}

		return current;
	}

	private static Tuple<NetworkNode, Integer> getNextNeighborAt(NetworkNode node, int direction, int distance, Map<UUID, Tuple<NetworkNode, EnumFacing>> destinations) {
		NetworkNode neighbor = node.getNeighborAt(direction);

		if (neighbor == null)
			return null;

		int numNeighbors = 0;

		for (int i = 0; i < 6; i++)	{
			if (neighbor.getNeighborAt(i) != null && i != direction) 
				numNeighbors++;
		}

		if (numNeighbors >= 2 || destinations.containsKey(neighbor.getId()))
			return new Tuple<NetworkNode, Integer>(neighbor, distance+neighbor.t_cost);

		return getNextNeighborAt(neighbor, direction, distance+neighbor.t_cost, destinations);
	}

}
