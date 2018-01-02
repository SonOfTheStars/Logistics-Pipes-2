package com.sots.routing;

import java.util.*;

import com.sots.tiles.TileGenericPipe;
import com.sots.util.data.Triple;
import com.sots.util.data.Tuple;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;

public class LPRoutedItem{

	public final int TICK_MAX = 10;
	public int ticks;
	private EnumFacing heading;
	private TileGenericPipe holding;
	private Deque<EnumFacing> route;
	private ItemStack stack;
	private Triple<Double, Double, Double> position;
	private final UUID ID;
	private TileGenericPipe destination;
	public LPRoutedItem(double x, double y, double z, ItemStack content, EnumFacing initVector, TileGenericPipe holder, Deque<EnumFacing> routingInfo, TileGenericPipe destination) {
		setHeading(initVector);
		setHolding(holder);
		route = routingInfo;
		ticks = 0;
		this.stack = content.copy();
		this.position=new Triple<Double, Double, Double>(x, y, z);
		ID=UUID.randomUUID();
		this.destination = destination;
	}

	public LPRoutedItem(double x, double y, double z, ItemStack content, int ticks, UUID ID) {
		this.ticks=ticks;
		this.position=new Triple<Double, Double, Double>(x, y, z);
		this.stack = content.copy();
		this.ID = ID;
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
		if (route.peek() == null) {
			return EnumFacing.UP;
		}
		return route.pop();
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
		position = new Triple<Double, Double, Double>(x, y, z);
		return position;
	}

	public void setPosition(double x, double y, double z) {
		position = new Triple<Double, Double, Double>(x, y, z);
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		Triple<Double, Double, Double> pos = getPosition();
		tag.setDouble("posX", pos.getFirst());
		tag.setDouble("posY", pos.getSecnd());
		tag.setDouble("posZ", pos.getThird());
		tag.setInteger("heading", heading.ordinal());
		tag.setUniqueId("UID", this.ID);
		tag.setTag("inventory", stack.serializeNBT());
		tag.setInteger("ticks", this.ticks);
		NBTTagList routeList = new NBTTagList();
		for(EnumFacing node : route) {
			NBTTagCompound nodeTag = new NBTTagCompound();
			//nodeTag.setUniqueId("UID", node.getKey());
			nodeTag.setInteger("heading", node.ordinal());
			routeList.appendTag(nodeTag);
		}
		tag.setTag("route", routeList);
		return tag;
	}

	public static LPRoutedItem readFromNBT(NBTTagCompound compound, TileGenericPipe holder) {
		double x = compound.getDouble("posX");
		double y = compound.getDouble("posY");
		double z = compound.getDouble("posZ");
		UUID id = compound.getUniqueId("UID");
		ItemStack content = new ItemStack(compound.getCompoundTag("inventory"));
		int ticks = compound.getInteger("ticks");
		Deque<EnumFacing> routingInfo = new ArrayDeque<>();
		NBTTagList routeList = (NBTTagList) compound.getTag("route");
		for(Iterator<NBTBase> i = routeList.iterator(); i.hasNext();) {
			NBTTagCompound node = (NBTTagCompound) i.next();
			EnumFacing nodeTuple = EnumFacing.values()[node.getInteger("heading")];
			routingInfo.add(nodeTuple);
		}
		LPRoutedItem item = new LPRoutedItem(x, y, z, content, ticks, id);
		item.setHeading(EnumFacing.VALUES[compound.getInteger("heading")]);
		item.setHolding(holder);
		item.route = routingInfo;
		return item;
	}

	public TileGenericPipe getDestination() {
		return destination;
	}

}
