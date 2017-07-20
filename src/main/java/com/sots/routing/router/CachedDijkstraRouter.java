package com.sots.routing.router;

import java.util.Stack;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import com.sots.LogisticsPipes2;
import com.sots.routing.NetworkNode;
import com.sots.routing.WeightedNetworkNode;
import com.sots.util.data.Tuple;
import com.sots.util.data.Triple;

import net.minecraft.util.EnumFacing;

public class CachedDijkstraRouter extends DijkstraRouter {
	//private Map<Triple<Map<UUID, WeightedNetworkNode>, NetworkNode, NetworkNode>, Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>>> cache = new HashMap<Triple<Map<UUID, WeightedNetworkNode>, NetworkNode, NetworkNode>, Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>>>();
	private Map<Tuple<NetworkNode, NetworkNode>, Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>>> cache = new HashMap<Tuple<NetworkNode, NetworkNode>, Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>>>();

	public CachedDijkstraRouter(Map<UUID, WeightedNetworkNode> junctions) {
		super(junctions);
	}

	@Override
	public Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>> route(NetworkNode s, NetworkNode t) {
		//Triple<Map<UUID, WeightedNetworkNode>, NetworkNode, NetworkNode> input = new Triple<Map<UUID, WeightedNetworkNode>, NetworkNode, NetworkNode>(super.junctions, s, t);
		Tuple<NetworkNode, NetworkNode> input = new Tuple<NetworkNode, NetworkNode>(s, t);
		try { //DEBUG
		if (cache.containsKey(input)) {
			LogisticsPipes2.logger.info("Got a route from cache"); //DEBUG
			return cache.get(input);
		}
		} catch (Exception e) {
			LogisticsPipes2.logger.info(e); //DEBUG
		}


		Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>> result = super.route(s, t);
		LogisticsPipes2.logger.info("Caching a route to cache"); //DEBUG
		cache.put(input, result);
		return result;
	}

	@Override
	public void shutdown() {
		super.shutdown();
		LogisticsPipes2.logger.info("Clearing cache"); //DEBUG
		cache.clear();
	}


}

