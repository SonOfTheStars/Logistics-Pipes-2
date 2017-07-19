package com.sots.routing;

import java.util.UUID;

import com.sots.routing.interfaces.IRoutable;
import com.sots.util.data.Tuple;

public class WeightedNetworkNode extends NetworkNode {

	public WeightedNetworkNode(UUID id, IRoutable pipe) {
		super(id, pipe);
		p_cost = Integer.MAX_VALUE;
	}


	public Tuple<WeightedNetworkNode, Integer>[] weightedNeighbors = (Tuple<WeightedNetworkNode, Integer>[]) new Object[6]; //Apparently one cannot create arrays of generic types directly
}

