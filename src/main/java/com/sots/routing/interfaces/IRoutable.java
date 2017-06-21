package com.sots.routing.interfaces;

import com.sots.routing.Network;

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
	
	/**
	 * Override this to specify behavior of a Pipe when it gets added to a Network
	 */
	public void subscribe(Network parent);
	
	/**
	 * Override this to specify behavior of a Pipe when it gets removed from a Network
	 */
	public void disconnect();
	
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
	
	public int posX();
	
	public int posY();
	
	public int posZ();

	void spawnParticle(float r, float g, float b);
}
