package com.sots.routing;

import java.util.List;
import java.util.UUID;

import com.sots.tiles.TileGenericPipe;
import com.sots.util.data.Tuple;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class LPRoutedItem{
	public final int TICK_MAX = 60;
	public int ticks;
	private EnumFacing heading;
	private TileGenericPipe holding;
	private List<Tuple<UUID, EnumFacing>> route;
	private ItemStack stack;
	public LPRoutedItem(World worldIn, double x, double y, double z, ItemStack stack, EnumFacing initVector, TileGenericPipe holder, List<Tuple<UUID, EnumFacing>> routingInfo, ItemStack content) {
		setHeading(initVector);
		setHolding(holder);
		route = routingInfo;
		ticks = 0;
		stack = content;
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
	
	public EnumFacing getHeadingForNode(UUID id){
		for(Tuple<UUID, EnumFacing> node : route) {
			if(node.getKey().equals(id)) {
				return node.getVal();
			}
		}
		return null;
	}
	
	public ItemStack getContent() {
		return stack;
	}
}
