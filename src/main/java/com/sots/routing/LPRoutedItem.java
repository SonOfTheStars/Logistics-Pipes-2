package com.sots.routing;

import java.util.List;
import java.util.UUID;

import com.sots.tiles.TileGenericPipe;
import com.sots.util.data.Tuple;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class LPRoutedItem extends EntityItem{

	private EnumFacing heading;
	private TileGenericPipe holding;
	private List<Tuple<UUID, EnumFacing>> route;
	public LPRoutedItem(World worldIn, double x, double y, double z, ItemStack stack, EnumFacing initVector, TileGenericPipe holder, List<Tuple<UUID, EnumFacing>> routingInfo) {
		super(worldIn, x, y, z, stack);
		setHeading(initVector);
		setHolding(holder);
		route = routingInfo;
	}
	public EnumFacing getHeading() {
		return heading;
	}
	public void setHeading(EnumFacing heading) {
		this.heading = heading;
	}
	public TileGenericPipe getHolding() {
		return holding;
	}
	public void setHolding(TileGenericPipe holding) {
		this.holding = holding;
	}
	public void setMotion() {
		switch(heading.getIndex()) {
		case 0: {
			motionX=0;
			motionZ=0;
			motionY=-0.3d;
			return;
		}
		case 1: {
			motionX=0;
			motionZ=0;
			motionY=0.3d;
			return;
		}
		case 2: {
			motionX=0;
			motionZ=-0.3d;
			motionY=0;
			return;
		}
		case 3: {
			motionX=0;
			motionZ=0.3d;
			motionY=0;
			return;
		}
		case 4: {
			motionX=-0.3d;
			motionZ=0;
			motionY=0;
			return;
		}
		case 5: {
			motionX=0.3d;
			motionZ=0;
			motionY=0;
			return;
		}
		default:
		{
			motionX=0;
			motionZ=0;
			motionY=0;
			return;
		}
		}
	}
}
