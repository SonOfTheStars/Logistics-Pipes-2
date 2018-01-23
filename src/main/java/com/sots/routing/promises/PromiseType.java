package com.sots.routing.promises;

import com.sots.util.data.Triple;

public enum PromiseType {
	PROMISE_SINK,
	PROMISE_CRAFT,
	PROMISE_SUPPLY;
	
	public static Triple<Float, Float, Float> getRGBFromType(PromiseType type){
		switch(type) {
			case PROMISE_SINK:
				return new Triple<>(0f, 0f, 1f);
			case PROMISE_CRAFT:
				return new Triple<>(.5f, 0f, .5f);
			case PROMISE_SUPPLY:
				return new Triple<>(1f, 1f, 0f);
			default: 
				return new Triple<>(1f, 1f, 1f);
		}
	}
}
