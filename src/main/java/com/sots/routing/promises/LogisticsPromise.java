package com.sots.routing.promises;

import java.util.UUID;

public class LogisticsPromise {
	private final UUID promiseID;
	private final PromiseType type;
	
	public LogisticsPromise(UUID promiseID, PromiseType type) {
		this.promiseID = promiseID;
		this.type = type;
	}

	public UUID getPromiseID() {
		return promiseID;
	}

	public PromiseType getType() {
		return type;
	}
	
}
