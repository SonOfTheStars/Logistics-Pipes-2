package com.sots.routing.interfaces;

import com.sots.routing.LPRoutedFluid;
import com.sots.routing.LPRoutedItem;

import net.minecraft.world.IBlockAccess;

public interface IPipe {
	public void getAdjacentPipes(IBlockAccess world);
	
	public boolean hasAdjacent();
	
	public boolean catchItem(LPRoutedItem item);
	public boolean catchFluid(LPRoutedFluid fluid);
}
