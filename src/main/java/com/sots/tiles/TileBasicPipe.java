package com.sots.tiles;

import java.util.ArrayList;

import com.sots.util.Connections;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TileBasicPipe extends TileGenericPipe{
	
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
	
	public ArrayList<String> checkConnections(IBlockAccess world, BlockPos pos) {
		ArrayList<String> hidden = new ArrayList<String>();
		if(down != ConnectionTypes.PIPE) {
			hidden.add(Connections.DOWN.toString());
		}
		if(up != ConnectionTypes.PIPE) {
			hidden.add(Connections.UP.toString());
		}
		if(north != ConnectionTypes.PIPE) {
			hidden.add(Connections.NORTH.toString());
		}
		if(south != ConnectionTypes.PIPE) {
			hidden.add(Connections.SOUTH.toString());
		}
		if(west != ConnectionTypes.PIPE) {
			hidden.add(Connections.WEST.toString());
		}
		if(east != ConnectionTypes.PIPE) {
			hidden.add(Connections.EAST.toString());
		}
		return hidden;
	}
	
}
