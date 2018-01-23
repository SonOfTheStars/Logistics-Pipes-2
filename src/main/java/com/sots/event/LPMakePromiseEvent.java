package com.sots.event;

import java.util.UUID;

import com.sots.routing.promises.LogisticsPromise;

import net.minecraftforge.fml.common.eventhandler.Event;

public class LPMakePromiseEvent extends Event {
	private final LogisticsPromise promise;
	private final UUID targetNode;
	
	public LPMakePromiseEvent(LogisticsPromise promise, UUID targetNode) {
		this.promise = promise;
		this.targetNode = targetNode;
	}

	public LogisticsPromise getPromise() {
		return promise;
	}

	public UUID getTargetNode() {
		return targetNode;
	}
}
