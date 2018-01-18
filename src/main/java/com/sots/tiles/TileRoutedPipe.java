package com.sots.tiles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.item.ItemWrench;
import com.sots.item.modules.IItemModule;
import com.sots.module.IModule;
import com.sots.routing.*;
import com.sots.routing.LogisticsRoute;
import com.sots.routing.interfaces.IDestination;
import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.util.Connections;
import com.sots.util.Misc;
import com.sots.util.References;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

public class TileRoutedPipe extends TileGenericPipe implements IRoutable, IPipe, IDestination{
	
	protected boolean hasInv = false;
	protected ItemStackHandler modules;
	protected Set<IModule> moduleLogics;
	protected List<Tuple<LogisticsRoute, Object>> waitingToRoute = new ArrayList<Tuple<LogisticsRoute, Object>>();
	//protected List<Tuple<LogisticsRoute, FluidStack>> waitingToRoute_fluid = new ArrayList<Tuple<LogisticsRoute, FluidStack>>();


	public TileRoutedPipe() {
		modules = new ItemStackHandler(References.MOD_COUNT_BASE);
		moduleLogics = new HashSet<IModule>();
	}	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		modules.deserializeNBT(compound.getCompoundTag("modules"));
		IModule mod = ((IItemModule) modules.getStackInSlot(0).getItem()).getModLogic();
		moduleLogics.add(mod);
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
			if(heldItem.isItemEqual(new ItemStack(Items.STICK))) {
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						for (int k = -1; k <= 1; k++) {
							if (!(world.getTileEntity(pos.add(i, j, k)) instanceof TileRoutedPipe)) {
								continue;
							}
							TileRoutedPipe current = (TileRoutedPipe) world.getTileEntity(pos.add(i, j, k));
							if (current.hasNetwork) {
								Set<UUID> nodes = current.network.getAllDestinations();
								int count = 0;
								int slot = 0;
								EnumFacing face = current.network.getDirectionForDestination(nodeID);
								ArrayList<ItemStack> stacks = current.getItemsInInventory(face);
								for (UUID nodeT : nodes) {
									if(nodeT.equals(current.nodeID))
										continue;
									count+=1;
									ItemStack stack = stacks.get(slot).copy();
									if(stack.getCount()>=count) {
										stack.setCount(1);//Only send one item per destination
										current.routeItemTo(nodeT, stack);
									}
									else {
										if (stacks.size() <= slot + 1) {
											break;
										}
										slot+=1;
										count = 1;
										stack = stacks.get(slot).copy();
										stack.setCount(1);//Only send one item per destination
										current.routeItemTo(nodeT, stack);
									}
								}
							}
						}
					}
				}
			}
			if(heldItem.getItem() instanceof ItemSign) {
				if (hasNetwork) {
					Set<UUID> nodes = network.getAllDestinations();
					int count = 0;
					int slot = 0;
					EnumFacing face = network.getDirectionForDestination(nodeID);
					ArrayList<ItemStack> stacks = getItemsInInventory(face);
					Iterator<ItemStack> iter = stacks.iterator();
					for (UUID nodeT : nodes) {
						if (stacks.isEmpty())
							break;
						if(nodeT.equals(nodeID))
							continue;
						count+=1;
						ItemStack stack = stacks.get(slot).copy();
						if(stack.getCount()>=count) {
							stack.setCount(1);//Only send one item per destination
							routeItemTo(nodeT, stack);
						}
						else {
							slot+=1;
							count = 1;
							stack = stacks.get(slot).copy();
							stack.setCount(1);//Only send one item per destination
							routeItemTo(nodeT, stack);
						}
					}
				}
				if (hasNetwork) {
					LogisticsPipes2.logger.info("STARTING ROUTING OF FLUID");
					Set<UUID> nodes = network.getAllDestinations();
					int count = 0;
					int slot = 0;
					EnumFacing face = network.getDirectionForDestination(nodeID);
					ArrayList<FluidStack> stacks = getFluidStacksInInventory(face);
					Iterator<FluidStack> iter = stacks.iterator();
					for (UUID nodeT : nodes) {
						if (stacks.isEmpty())
							break;
						if(nodeT.equals(nodeID))
							continue;
						count+=1;
						FluidStack stack = stacks.get(slot).copy();
						if(stack.amount >= count*1000) {
							stack.amount = 1000;//Only send one bucket per destination
							routeItemTo(nodeT, stack);
							//routeFluidTo(nodeT, stack);
						}
						else {
							slot+=1;
							count = 1;
							stack = stacks.get(slot).copy();
							stack.amount = 1000;//Only send one bucket per destination
							routeItemTo(nodeT, stack);
							//routeFluidTo(nodeT, stack);
						}
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
			if(count < References.MOD_COUNT_BASE){
				IModule mod = ((IItemModule) heldItem.getItem()).getModLogic();
				if(mod.canInsert()){
					modules.insertItem(0, heldItem.splitStack(1), false);
					moduleLogics.add(mod);
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
			for (int i = 0; i < 6; i++) {
				if (hasInventoryOnSide(i))
					hasInv = true;
			}
			if (!hasInv) {
				network.unregisterDestination(this.nodeID);
				moduleLogics.forEach(p -> p.disconnect());
			}
			
		}
		moduleLogics.forEach(p -> {
			if(p.canExecute()) {
				p.execute(this);
			}
		});
		checkIfRoutesAreReady();
		//checkIfFluidRoutesAreReady();
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		super.breakBlock(world, pos, state, player);
		if(modules.getStackInSlot(0)!=null) {
			Misc.spawnInventoryInWorld(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, modules);
		}
	}
	
	@Override
	public void disconnect() {
		LogisticsPipes2.logger.log(Level.DEBUG, "Removed TileGenericPipe" + toString() + " from Network:" + network.getName());
		hasNetwork=false;
		network=null;
		moduleLogics.forEach(p -> p.disconnect());
	}

	public void routeItemTo(UUID nodeT, Object item) {
		LogisticsRoute route = network.getRouteFromTo(nodeID, nodeT);
		if (route == null) {
			return;
		}
		waitingToRoute.add(new Tuple<LogisticsRoute, Object>(route, item));
	}

	//public void routeFluidTo(UUID nodeT, FluidStack fluid) {
		//LogisticsRoute route = network.getRouteFromTo(nodeID, nodeT);
		//if (route == null) {
			//return;
		//}
		//waitingToRoute_fluid.add(new Tuple<LogisticsRoute, FluidStack>(route, fluid));
	//}

	protected void checkIfRoutesAreReady() {
		if (!waitingToRoute.isEmpty()) {
			
			try {
				Tuple<LogisticsRoute, Object> route = waitingToRoute.stream()
						.filter(entry -> entry.getKey().isComplete())
						.findFirst().get();
				Object item = route.getVal();
				//ItemStack item = route.getVal();
				Deque<EnumFacing> routeCopy = new ArrayDeque<EnumFacing>();
				routeCopy.addAll(route.getKey().getdirectionStack());
				EnumFacing side = network.getDirectionForDestination(nodeID);
				catchItem(LPRoutedObject.takeFromBlock(world.getTileEntity(getPos().offset(side)), side, item, routeCopy, (TileGenericPipe) route.getKey().getTarget().getMember(), this, item.getClass()));
				//if (hasItemInInventoryOnSide(side, item)) {
					//ItemStack stack = takeFromInventoryOnSide(side, item);
					//catchItem(new LPRoutedItem((double) posX(), (double) posY(), (double) posZ(), stack, side.getOpposite(), this, routeCopy, (TileGenericPipe) route.getKey().getTarget().getMember()));
				//}
				waitingToRoute.remove(route);
			} catch (Exception e) {
				//Discard Exception. If we get any here that means there simply was no route ready yet.
			}
			
		}
	}

	//private void checkIfFluidRoutesAreReady() {
		//if (!waitingToRoute_fluid.isEmpty()) {
			//for (Iterator<Tuple<LogisticsRoute, FluidStack>> i = waitingToRoute_fluid.iterator(); i.hasNext();) {
				//Tuple<LogisticsRoute, FluidStack> route = i.next();
				//if (!route.getKey().isComplete()) {
					//LogisticsPipes2.logger.info("A route is not done routing yet");
					//continue;
				//}
				//FluidStack fluid = route.getVal();
				//Deque<EnumFacing> routeCopy = new ArrayDeque<EnumFacing>();
				//routeCopy.addAll(route.getKey().getdirectionStack());
				//EnumFacing side = network.getDirectionForDestination(nodeID);
				//if (hasFluidInInventoryOnSide(side, fluid)) {
					//FluidStack stack = takeFluidFromInventoryOnSide(side, fluid);
					//catchItem(new LPRoutedFluid(stack, side.getOpposite(), this, routeCopy, (TileGenericPipe) route.getKey().getTarget().getMember()));
				//}
				//i.remove();

				//break; // This line makes it so, that only 1 item is routed pr. tick. Comment out this line to allow multiple items to be routed pr. tick.
			//}
		//}
	//}
	
	public boolean hasInventory() {return hasInv;}

}
