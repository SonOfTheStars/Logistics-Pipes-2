package com.sots.routing.interfaces;

import java.util.Map;

import net.minecraft.util.EnumFacing;

public interface IPipe {
	public Map<EnumFacing, IPipe> getAdjacentPipes();
	
	public boolean hasAdjacent();
}
