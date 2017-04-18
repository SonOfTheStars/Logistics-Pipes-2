package com.sots.routing.interfaces;

import java.util.Map;

import net.minecraft.util.EnumFacing;

public interface IPipe {
	public Map<EnumFacing, Boolean> getAdjacentPipes();
	
	public boolean hasAdjacent();
}
