package com.sots.routing.interfaces;

public interface IRoutable {
	/**
	 * Wether or not the Pipe itself is a Routed Node
	 * @return true=Routed Node, False=passive Pipe
	 */
	public boolean isRouted();
	
	/**
	 * Wether or not the Pipe is Routeable, aka. if the Network can send stuff through it.
	 * @return true=Can be routed through, false=Can't be routed through
	 */
	public boolean isRoutable();
	
	/**
	 * Wether or not this Pipe is Part of a Network
	 */
	public boolean hasNetwork();
	
	public void network();
	
	/**
	 * Wether or not this Pipe is powered by a Network
	 */
	public boolean hasPower();
	
	/**
	 * Wether or not this Pipe actually actively consumes Power
	 */
	public boolean consumesPower();
	
	/**
	 * The amount of power that this pipe consumes by default
	 * @return Idle Power Cost
	 */
	public int powerConsumed();
	
}
