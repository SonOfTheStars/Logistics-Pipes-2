package com.sots.tiles;

import java.util.ArrayList;
import java.util.UUID;

import com.sots.routing.Network;
import com.sots.routing.interfaces.IDestination;
import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.util.Connections;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TileRoutedPipe extends TileGenericPipe implements IRoutable, IPipe, IDestination{
	
	public ArrayList<String> checkConnections(IBlockAccess world, BlockPos pos) {
		ArrayList<String> hidden = new ArrayList<String>();
		if(down != ConnectionTypes.BLOCK) {
			hidden.add(Connections.C_DOWN.toString());
			if(down != ConnectionTypes.PIPE)
				hidden.add(Connections.DOWN.toString());
		}
		if(up != ConnectionTypes.BLOCK) {
			hidden.add(Connections.C_UP.toString());
			if(up != ConnectionTypes.PIPE)
				hidden.add(Connections.UP.toString());
		}
		if(north != ConnectionTypes.BLOCK) {
			hidden.add(Connections.C_NORTH.toString());
			if(north != ConnectionTypes.PIPE)
				hidden.add(Connections.NORTH.toString());
		}
		if(south != ConnectionTypes.BLOCK) {
			hidden.add(Connections.C_SOUTH.toString());
			if(south != ConnectionTypes.PIPE)
				hidden.add(Connections.SOUTH.toString());
		}
		if(west != ConnectionTypes.BLOCK) {
			hidden.add(Connections.C_WEST.toString());
			if(west != ConnectionTypes.PIPE)
				hidden.add(Connections.WEST.toString());
		}
		if(east != ConnectionTypes.BLOCK) {
			hidden.add(Connections.C_EAST.toString());
			if(east != ConnectionTypes.PIPE)
				hidden.add(Connections.EAST.toString());
		}
		
		return hidden;
	}
	
	@Override
	public boolean isRouted() {return true;}

	@Override
	public boolean isRoutable() {return true;}

	@Override
	public boolean hasPower() {return false;}

	@Override
	public boolean consumesPower() {return true;}
	
	@Override
	public int powerConsumed() {return 1;}
	
	@Override
	protected void network() {
		super.network();
		if(this.hasNetwork) {
			for(int i = 0; i<6; i++) {
				if(hasInventoryOnSide(i)) {
					network.registerDestination(this.nodeID);
					break;
				}
					
			}
		}
	}
	
}
