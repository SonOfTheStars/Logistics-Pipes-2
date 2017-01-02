package com.sots.routing;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.routing.interfaces.IDestination;

public class Network {
	private List<IDestination> destinations = new ArrayList<IDestination>();
	private NetworkNode root;
	private String name;
	
	public Network(String n) {
		name=n;
		
		root = new NetworkNode(null, 0, true);
	}
	
	public void registerDestination(IDestination in) {
		if(!destinations.contains(in)) {
			destinations.add(in);
		}
		else {
			LogisticsPipes2.logger.log(Level.WARN, "Tried to register " + in.name + " twice in Network " + name);
		}
	}
	
	public void addNode() {
		
	}
}
