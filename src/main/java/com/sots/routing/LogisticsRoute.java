package com.sots.routing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import net.minecraft.util.EnumFacing;

public class LogisticsRoute {
	private NetworkNode start;
	private NetworkNode target;
	private Deque<EnumFacing> directionStack;
	private int weight = 0;
	private boolean isComplete = false;
	
	public LogisticsRoute(NetworkNode start, NetworkNode target) {
		
	}
	
	public LogisticsRoute(NetworkNode start, NetworkNode target, Deque<EnumFacing> directionStack) {
		this.start = start;
		this.target = target;
		this.directionStack = directionStack;
		this.weight = 0;
	}
	
	public LogisticsRoute(NetworkNode start, NetworkNode target, Deque<EnumFacing> directionStack, int weight) {
		this.start = start;
		this.target = target;
		this.directionStack = directionStack;
		this.weight = weight;
	}
	
	public LogisticsRoute(NetworkNode start, NetworkNode target, Deque<EnumFacing> directionStack, int weight, boolean isComplete) {
		this.start = start;
		this.target = target;
		this.directionStack = directionStack;
		this.weight = weight;
		this.isComplete = isComplete;
	}

	public NetworkNode getStart() {
		return start;
	}

	public NetworkNode getTarget() {
		return target;
	}

	public Deque<EnumFacing> getdirectionStack() {
		return directionStack;
	}

	public int getWeight() {
		return weight;
	}
	
	public boolean isComplete() {
		return isComplete;
	}

	public void setStart(NetworkNode start) {
		this.start = start;
	}

	public void setTarget(NetworkNode target) {
		this.target = target;
	}

	public void setDirectionStack(Deque<EnumFacing> directionStack) {
		this.directionStack = directionStack;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public void setCompletion(boolean completion) {
		this.isComplete = completion;
	}
	
	public void weightFromStack() {
		this.weight = this.directionStack.size();
	}
	
	public LogisticsRoute reverse() {
		Deque<EnumFacing> helpStack = this.directionStack;
		Deque<EnumFacing> reversedStack = new ArrayDeque<EnumFacing>();
		
		while(!helpStack.isEmpty()) {
			EnumFacing facing = helpStack.pop();
			facing = facing.getOpposite();
			reversedStack.push(facing);
		}
		
		return new LogisticsRoute(target, start, reversedStack, reversedStack.size(), isComplete);
	}
	
	public boolean isRouteFor(UUID start, UUID target) {
		return (this.start.getId().equals(start) && this.target.getId().equals(target));
	}
}
