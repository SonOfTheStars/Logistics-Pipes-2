package com.sots.routing.router;

import java.util.Comparator;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.routing.NetworkNode;

public class WeightComparator implements Comparator<NetworkNode>{

	@Override
	public int compare(NetworkNode o1, NetworkNode o2) {
		try {
		if(o1.h_cost<o2.h_cost)
			return -1;
		if(o1.h_cost>o2.h_cost)
			return 1;
		}
		catch(NullPointerException npe) {
			LogisticsPipes2.logger.log(Level.ALL, "Router ran into a Null node!");
		}
		return 0;
	}

	

}
