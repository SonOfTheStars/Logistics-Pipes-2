package com.sots.tiles;

import java.util.UUID;

import com.sots.routing.Network;

public class TileNetworkCore extends TileGenericPipe {
	
	private boolean ownsNetwork = false;
	
	public void makeNetwork() {
		if(!ownsNetwork) {
			network = new Network(UUID.randomUUID());
			nodeID = network.setRoot(this);
			hasNetwork=true;
			ownsNetwork=true;
		}
	}
	
	public void updateNetwork() {
		network.purgeNetwork();
	}
	
	@Override
	public void update() {
	}
}
