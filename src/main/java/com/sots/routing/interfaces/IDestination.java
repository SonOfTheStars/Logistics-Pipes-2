package com.sots.routing.interfaces;

import com.sots.routing.Network;

public interface IDestination {
	public static String name = "";
	public void registerDestination(Network parent);
	public void setDestinationName(String name);
	
}
