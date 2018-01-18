package com.sots.routing.interfaces;

import com.sots.routing.*;

import net.minecraft.world.IBlockAccess;

public interface IPipe {
	public void getAdjacentPipes(IBlockAccess world);
	
	public boolean hasAdjacent();
	
	public boolean catchItem(LPRoutedObject item);
}
