package com.sots.routing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sots.LogisticsPipes2;
import com.sots.tiles.TileGenericPipe;
import com.sots.util.data.Triple;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class LPRoutedObject<T> {
	
	public final int TICK_MAX = 10;
	public int ticks;
	private EnumFacing heading;
	private TileGenericPipe holding;
	private Deque<EnumFacing> route;
	private T contents;
	private Class<T> contentType;
	//private Triple<Double, Double, Double> position;
	private final UUID ID;
	private TileGenericPipe destination;
	private static Map<Class<? extends Object>, Class<? extends LPRoutedObject<? extends Object>>> types = new HashMap<Class<? extends Object>, Class<? extends LPRoutedObject<? extends Object>>>();

	public LPRoutedObject(T content, EnumFacing initVector, TileGenericPipe holder, Deque<EnumFacing> routingInfo, TileGenericPipe destination, Class<T> contentType) {
		this.heading = initVector;
		this.holding = holder;
		this.route = routingInfo;
		this.ticks = 0;
		this.contents = copyContent(content);
		//this.position = new Triple<Double, Double, Double>(x, y, z);
		this.ID = UUID.randomUUID();
		this.destination = destination;
		this.contentType = contentType;
	}

	public static LPRoutedObject makeLPRoutedObjectFromContent(Object content, EnumFacing initVector, TileGenericPipe holder, Deque<EnumFacing> routingInfo, TileGenericPipe destination) {
		Class<? extends LPRoutedObject<?>> lpRoutedClass = types.get(content.getClass());
		try {
			return lpRoutedClass.getConstructor(Object.class, EnumFacing.class, TileGenericPipe.class, Deque.class, TileGenericPipe.class).newInstance(content, initVector, holder, routingInfo, destination, content.getClass());
		} catch (Exception e) {
			LogisticsPipes2.logger.info("Something went wrong", e);
			return null;
		}
	}

	protected T copyContent(T content) {
		return null;
	}

	public LPRoutedObject(int ticks, UUID ID, Class<T> contentType) {
		this.ticks=ticks;
		//this.position=new Triple<Double, Double, Double>(x, y, z);
		this.ID = ID;
		this.contentType = contentType;
	}

	public static LPRoutedObject makeLPRoutedObjectFromContent(int ticks, UUID ID, Class content) {
		Class<? extends LPRoutedObject<? extends Object>> lpRoutedClass = types.get(content);
		if (lpRoutedClass == null) {
			LogisticsPipes2.logger.info("TESTTESTTEST");
		}

		try {
			return lpRoutedClass.getConstructor(int.class, UUID.class).newInstance(ticks, ID);
		} catch (Exception e) {
			LogisticsPipes2.logger.info("Something went wrong", e);
			return null;
		}
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

	public void setRoute(Deque<EnumFacing> route) {
		this.route = route;
	}

	public T getContent() {
		return contents;
	}

	public void setContent(T content) {
		this.contents = content;
	}

	public TileGenericPipe getDestination() {
		return destination;
	}
	
	public UUID getID() {
		return ID;
	}

	public Triple<Double, Double, Double> calculateTranslation(float partialTicks) {
		float tmpTicks = ticks + partialTicks;

		double newX = (getHeading().getDirectionVec().getX() * (tmpTicks / TICK_MAX - 0.5));
		double newY = (getHeading().getDirectionVec().getY() * (tmpTicks / TICK_MAX - 0.5));
		double newZ = (getHeading().getDirectionVec().getZ() * (tmpTicks / TICK_MAX - 0.5));
		return new Triple<>(newX, newY, newZ);
	}

	//public void setPosition(double x, double y, double z) {
		//position = new Triple<Double, Double, Double>(x, y, z);
	//}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		//Triple<Double, Double, Double> pos = getPosition();
		//tag.setDouble("posX", pos.getFirst());
		//tag.setDouble("posY", pos.getSecnd());
		//tag.setDouble("posZ", pos.getThird());
		tag.setInteger("heading", heading.ordinal());
		tag.setUniqueId("UID", this.ID);
		//tag.setTag("inventory", stack.serializeNBT());
		writeContentToNBT(tag);
		tag.setInteger("ticks", this.ticks);
		tag.setString("type", contentType.getName());
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

	public static LPRoutedObject readFromNBT(NBTTagCompound compound, TileGenericPipe holder) {
		//double x = compound.getDouble("posX");
		//double y = compound.getDouble("posY");
		//double z = compound.getDouble("posZ");
		double x = 0;
		double y = 0;
		double z = 0;
		UUID id = compound.getUniqueId("UID");
		//ItemStack content = new ItemStack(compound.getCompoundTag("inventory"));
		int ticks = compound.getInteger("ticks");
		Deque<EnumFacing> routingInfo = new ArrayDeque<>();
		NBTTagList routeList = (NBTTagList) compound.getTag("route");
		for (NBTBase aRouteList : routeList) {
			NBTTagCompound node = (NBTTagCompound) aRouteList;
			EnumFacing nodeTuple = EnumFacing.values()[node.getInteger("heading")];
			routingInfo.add(nodeTuple);
		}
		//LPRoutedObject item = new LPRoutedObject(x, y, z, ticks, id);
		//TODO: CONTINUE FROM HERE
		try {
			LPRoutedObject item = LPRoutedObject.makeLPRoutedObjectFromContent(ticks, id, Class.forName(compound.getString("type")));
			item.readContentFromNBT(compound);
			item.setHeading(EnumFacing.VALUES[compound.getInteger("heading")]);
			item.setHolding(holder);
			item.route = routingInfo;
			return item;
		} catch (Exception e) {
			LogisticsPipes2.logger.info("Something went wrong", e);
			return null;
		}

	}

	public void writeContentToNBT(NBTTagCompound compound) {}
	public void readContentFromNBT(NBTTagCompound compound) {}

	@SideOnly(Side.CLIENT)
	public void render(TileGenericPipe te, float partialTicks) {}

	public void spawnInWorld(World world, double x, double y, double z) {}

	public void putInBlock(TileEntity te) {}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static LPRoutedObject takeFromBlock(TileEntity te, EnumFacing face, Object stack, Deque<EnumFacing> route, TileGenericPipe destination, TileGenericPipe holder, Class type, int targetAmount) {
		LPRoutedObject lpRoutedObject = makeLPRoutedObjectFromContent(0, UUID.randomUUID(), type);
		return lpRoutedObject.takeFromBlock(te, face, stack, route, destination, holder, targetAmount);
	}

	@SuppressWarnings("rawtypes")
	protected LPRoutedObject takeFromBlock(TileEntity te, EnumFacing face, T item, Deque<EnumFacing> route, TileGenericPipe destination, TileGenericPipe holder, int targetAmount) {
		return null;
	}

	public static void registerTypeOfLPRoutedObject(Class<? extends LPRoutedObject<? extends Object>> lpRoutedType, Class<? extends Object> contents) {
		types.put(contents, lpRoutedType);
	}

}
