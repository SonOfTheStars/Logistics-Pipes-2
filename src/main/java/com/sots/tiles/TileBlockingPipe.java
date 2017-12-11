package com.sots.tiles;

import java.util.*;

import com.sots.util.Connections;
import com.sots.routing.NetworkNode;
import com.sots.LogisticsPipes2;

import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

public class TileBlockingPipe extends TileGenericPipe {

	private boolean redstone = false;

	public void setIncomingRedstone(boolean redstone) {
		if (this.hasNetwork) {
			LogisticsPipes2.logger.info("Redstone was set to " + redstone);
			if (redstone != this.redstone) {
				network.clearCache();
				LogisticsPipes2.logger.info("Network cache was cleared");
			}
			this.redstone = redstone;
		}
	}

	@Override
	public boolean isRouted() {return true;}

	@Override
	public boolean isRoutable() {return !redstone;}

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

	@Override
	public void network() {
		super.network();
		if (this.hasNetwork) {
			network.registerDestination(this.nodeID, EnumFacing.UP);
		}

	}


	@Override
	public void update() {
		super.update();
		if (this.hasNetwork) {
			for (int i = 0; i < 6; i++) {
				NetworkNode neighbor = network.getNodeByID(this.nodeID).getNeighborAt(i);
				if (neighbor != null && !(neighbor.isDestination())) {
					network.registerDestination(neighbor.getId(), EnumFacing.getFront(i).getOpposite());
				}
			}
		}
	}
}
