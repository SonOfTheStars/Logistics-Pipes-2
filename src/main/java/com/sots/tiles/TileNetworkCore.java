package com.sots.tiles;

import java.util.UUID;

import com.sots.routing.Network;

public class TileNetworkCore extends TileGenericPipe {
	
	private boolean ownsNetwork = false;
	private Network network;
	
	public void makeNetwork() {
		if(!ownsNetwork) {
			network = new Network(UUID.randomUUID());
			ownsNetwork=true;
			nodeID = network.setRoot(this);
		}
	}
	
	public void updateNetwork() {
		network.purgeNetwork();
	}
}
