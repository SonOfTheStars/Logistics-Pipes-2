package com.sots.tiles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.sots.item.ItemWrench;
import com.sots.item.modules.IItemModule;
import com.sots.module.IModule;
import com.sots.routing.LPRoutedItem;
import com.sots.routing.NetworkNode;
import com.sots.routing.interfaces.IDestination;
import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.util.Connections;
import com.sots.util.Misc;
import com.sots.util.data.Triple;
import com.sots.util.data.Tuple;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class TileRoutedPipe extends TileGenericPipe implements IRoutable, IPipe, IDestination{
	
	private final int MAX_MODS = 1;
	private ItemStackHandler modules = new ItemStackHandler(MAX_MODS);
	private List<Tuple<Tuple<Boolean, Deque<EnumFacing>>, ItemStack>> waitingToRoute = new ArrayList<Tuple<Tuple<Boolean, Deque<EnumFacing>>, ItemStack>>();
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		modules.deserializeNBT(compound.getCompoundTag("modules"));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setTag("modules", modules.serializeNBT());
		
	    return compound;
	}
	
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
					network.registerDestination(this.nodeID, EnumFacing.getFront(i));
					break;
				}
					
			}
		}
	}
	
	@Override
	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if(heldItem.getItem()!=null) {
			if(heldItem.getItem() instanceof ItemSign) {
				if (hasNetwork) {
					for (UUID nodeT : network.getAllDestinations()) {
						routeItemTo(nodeT, new ItemStack(Items.APPLE));
					}

				}
			}
			if(heldItem.getItem() instanceof ItemWrench) {
				if (side == EnumFacing.UP || side == EnumFacing.DOWN){
					if (Math.abs(hitX-0.5) > Math.abs(hitZ-0.5)){
						if (hitX < 0.5){
							this.west = forceConnection(west);
						}
						else {
							this.east = forceConnection(east);
						}
					}
					else {
						if (hitZ < 0.5){
							this.north = forceConnection(north);
						}
						else {
							this.south = forceConnection(south);
						}
					}
				}
				if (side == EnumFacing.EAST || side == EnumFacing.WEST){
					if (Math.abs(hitY-0.5) > Math.abs(hitZ-0.5)){
						if (hitY < 0.5){
							this.down = forceConnection(down);
						}
						else {
							this.up = forceConnection(up);
						}
					}
					else {
						if (hitZ < 0.5){
							this.north = forceConnection(north);
						}
						else {
							this.south = forceConnection(south);
						}
					}
				}
				if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH){
					if (Math.abs(hitX-0.5) > Math.abs(hitY-0.5)){
						if (hitX < 0.5){
							this.west = forceConnection(west);
						}
						else {
							this.east = forceConnection(east);
						}
					}
					else {
						if (hitY < 0.5){
							this.down = forceConnection(down);
						}
						else {
							this.up = forceConnection(up);
						}
					}
				}
				getAdjacentPipes(world);
				markDirty();
				return true;
			}
		}
		
		if(heldItem.getItem() instanceof IItemModule){
			int count = 0;
			if(modules.getStackInSlot(0)!=null) {
				count = modules.getStackInSlot(0).getCount();
			}
			if(count < MAX_MODS){
				IModule mod = ((IItemModule) heldItem.getItem()).getModLogic();
				if(mod.canInsert()){
					modules.insertItem(0, heldItem.splitStack(1), false);
					if(heldItem.isEmpty()) {
						heldItem = null;
					}
					markDirty();
				}
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void update() {
		super.update();
		if (this.hasNetwork && !(network.getNodeByID(this.nodeID).isDestination())) {
			for (int i = 0; i < 6; i++) {
				if (hasInventoryOnSide(i)) {
					network.registerDestination(this.nodeID, EnumFacing.getFront(i));
					break;
				}
			}
		} else if (this.hasNetwork && (network.getNodeByID(this.nodeID).isDestination())) {
			boolean hasInv = false;
			for (int i = 0; i < 6; i++) {
				if (hasInventoryOnSide(i))
					hasInv = true;
			}
			if (!hasInv)
				network.unregisterDestination(this.nodeID);
		}
		checkIfRoutesAreReady();
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		super.breakBlock(world, pos, state, player);
		if(modules.getStackInSlot(0)!=null) {
			Misc.spawnInventoryInWorld(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, modules);
		}
	}

	public void routeItemTo(UUID nodeT, ItemStack item) {
		Tuple<Boolean, Deque<EnumFacing>> route = network.getRouteFromTo(nodeID, nodeT);
		if (route == null) {
			return;
		}
		waitingToRoute.add(new Tuple<Tuple<Boolean, Deque<EnumFacing>>, ItemStack>(route, item));
	}

	private void checkIfRoutesAreReady() {
		if (!waitingToRoute.isEmpty()) {
			for (Iterator<Tuple<Tuple<Boolean, Deque<EnumFacing>>, ItemStack>> i = waitingToRoute.iterator(); i.hasNext();) {
				Tuple<Tuple<Boolean, Deque<EnumFacing>>, ItemStack> route = i.next();
				if (route.getKey().getKey() == false) {
					continue;
				}
				ItemStack item = route.getVal();
				Deque<EnumFacing> routeCopy = new ArrayDeque<EnumFacing>();
				routeCopy.addAll(route.getKey().getVal());
				EnumFacing side = network.getDirectionForDestination(nodeID);
				if (hasItemInInventoryOnSide(side, item)) {
					ItemStack stack = takeFromInventoryOnSide(side, item);
					catchItem(new LPRoutedItem((double) posX(), (double) posY(), (double) posZ(), stack, side.getOpposite(), this, routeCopy));
				}
				i.remove();
			}
		}
	}

}
