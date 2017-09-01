package com.sots.routing;

import java.util.Deque;
import java.util.Stack;
import java.util.UUID;

import com.sots.tiles.TileGenericPipe;
import com.sots.util.data.Tuple;
import com.sots.util.data.Triple;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3i;

public class LPRoutedItem{
	public final int TICK_MAX = 10;
	public int ticks;
	private EnumFacing heading;
	private TileGenericPipe holding;
	private Deque<Tuple<UUID, EnumFacing>> route;
	private ItemStack stack;
	public LPRoutedItem(World worldIn, double x, double y, double z, ItemStack stack, EnumFacing initVector, TileGenericPipe holder, Deque<Tuple<UUID,EnumFacing>> deque, ItemStack content) {
		setHeading(initVector);
		setHolding(holder);
		route = deque;
		ticks = 0;
		this.stack = content;
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
	
	public EnumFacing getHeadingForNode(){
		if (route.isEmpty()) {
			return EnumFacing.UP;
		}
		return route.pop().getVal();
	}
	
	public ItemStack getContent() {
		return stack;
	}

	public Triple<Double, Double, Double> getPosition() {
		double x = holding.posX() + 0.5;
		double y = holding.posY() + 0.5;
		double z = holding.posZ() + 0.5;

		if (ticks < TICK_MAX/2) { // Approaching middle of pipe
			x -= (((TICK_MAX/2)-ticks)/(TICK_MAX/2)) * heading.getDirectionVec().getX();
			y -= (((TICK_MAX/2)-ticks)/(TICK_MAX/2)) * heading.getDirectionVec().getY();
			z -= (((TICK_MAX/2)-ticks)/(TICK_MAX/2)) * heading.getDirectionVec().getZ();
		} else { // Leaving middle of pipe
			x += ((ticks-(TICK_MAX/2))/(TICK_MAX/2)) * heading.getDirectionVec().getX();
			y += ((ticks-(TICK_MAX/2))/(TICK_MAX/2)) * heading.getDirectionVec().getY();
			z += ((ticks-(TICK_MAX/2))/(TICK_MAX/2)) * heading.getDirectionVec().getZ();
		}
		return new Triple<Double, Double, Double>(x, y, z);
	}

}
