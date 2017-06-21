package com.sots.routing.interfaces;

import net.minecraft.world.IBlockAccess;

public interface IPipe {
	public void getAdjacentPipes(IBlockAccess world);
	
	public boolean hasAdjacent();
	
}
