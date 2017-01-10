package com.sots.tiles;

import java.util.ArrayList;

import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.util.ConnectionHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TileRoutedPipe extends TileGenericPipe implements IRoutable, IPipe{
	
	public ArrayList<String> checkConnections(IBlockAccess world, BlockPos pos) {
		ArrayList<String> check = ConnectionHelper.checkInventoriesAndPipes(world, pos);
		return check;
	}
	
	@Override
	public boolean isRouted() {return true;}

	@Override
	public boolean isRoutable() {return true;}

	@Override
	public boolean hasNetwork() {return false;}

	@Override
	public boolean hasPower() {return false;}

	@Override
	public boolean consumesPower() {return true;}
	
	@Override
	public int powerConsumed() {return 1;}

	public void setHasChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}
	
}
