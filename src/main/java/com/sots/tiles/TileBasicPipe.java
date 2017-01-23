package com.sots.tiles;

import java.util.ArrayList;

import com.sots.util.ConnectionHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TileBasicPipe extends TileGenericPipe{
	
	public ArrayList<String> checkConnections(IBlockAccess world, BlockPos pos) {
		ArrayList<String> check = ConnectionHelper.checkForPipes(world, pos);
		return check;
		
	}
	
	@Override
	public boolean isRouted() {return false;}

	@Override
	public boolean isRoutable() {return true;}

	@Override
	public boolean hasPower() {return false;}

	@Override
	public boolean consumesPower() {return false;}
	
	@Override
	public int powerConsumed() {return 0;}
	
}
