package com.sots.tiles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.event.LPMakePromiseEvent;
import com.sots.item.ItemWrench;
import com.sots.module.CapabilityModule;
import com.sots.routing.LPRoutedObject;
import com.sots.routing.LogisticsRoute;
import com.sots.routing.interfaces.IDestination;
import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.routing.promises.LogisticsPromise;
import com.sots.routing.promises.PromiseType;
import com.sots.util.Connections;
import com.sots.util.ModuleInv;
import com.sots.util.References;
import com.sots.util.data.Triple;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TileRoutedPipe extends TileGenericPipe implements IRoutable, IPipe, IDestination {

    protected boolean hasInv = false;
    protected int ticksTillSparkle=0;
    private final ModuleInv moduleInv;
    protected List<Triple<LogisticsRoute, Object, PromiseType>> waitingToRoute = new ArrayList<>();
    //protected List<Tuple<LogisticsRoute, FluidStack>> waitingToRoute_fluid = new ArrayList<Tuple<LogisticsRoute, FluidStack>>();
    protected List<LogisticsPromise> promises = new ArrayList<>();


	public TileRoutedPipe() {
        this.moduleInv = new ModuleInv(this, References.MOD_COUNT_BASE);
        MinecraftForge.EVENT_BUS.register(this);
    }
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		moduleInv.deserializeNBT(compound.getCompoundTag("modules"));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setTag("modules", moduleInv.serializeNBT());
		
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
    public boolean isRoutable() {
        return true;
    }

    @Override
    public boolean hasPower() {
        return false;
    }

	@Override
	public boolean consumesPower() {return true;}
	
	@Override
	public int powerConsumed() {return 1;}
	
	@SubscribeEvent
	public void receivePromise(LPMakePromiseEvent event) {
		if(event.getTargetNode().equals(nodeID)) {
			promises.add(event.getPromise());
		}
	}
	
	public void onItemCatch(LPRoutedObject item) {
		if(!promises.isEmpty()) {
			LogisticsPromise toRemove = promises.stream()
					.filter(promise -> item.getID().equals(promise.getPromiseID()))
					.findFirst().get();
				promises.remove(toRemove);
		}
	}
	
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
	public boolean catchItem(LPRoutedObject item) {
		onItemCatch(item);
		return super.catchItem(item);
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
										current.routeItemTo(nodeT, stack, PromiseType.PROMISE_SINK);
									}
									else {
										if (stacks.size() <= slot + 1) {
											break;
										}
										slot+=1;
										count = 1;
										stack = stacks.get(slot).copy();
										stack.setCount(1);//Only send one item per destination
										current.routeItemTo(nodeT, stack, PromiseType.PROMISE_SINK);
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
					for (UUID nodeT : nodes) {
						if (stacks.isEmpty())
							break;
						if(nodeT.equals(nodeID))
							continue;
						count+=1;
						ItemStack stack = stacks.get(slot).copy();
						if(stack.getCount()>=count) {
							stack.setCount(1);//Only send one item per destination
							routeItemTo(nodeT, stack, PromiseType.PROMISE_SINK);
						}
						else {
							slot+=1;
							count = 1;
							stack = stacks.get(slot).copy();
							stack.setCount(1);//Only send one item per destination
							routeItemTo(nodeT, stack, PromiseType.PROMISE_SINK);
						}
					}
				}
				/*if (hasNetwork) {
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
							routeItemTo(nodeT, stack, PromiseType.PROMISE_SINK);
							//routeFluidTo(nodeT, stack);
						}
						else {
							slot+=1;
							count = 1;
							stack = stacks.get(slot).copy();
							stack.amount = 1000;//Only send one bucket per destination
							routeItemTo(nodeT, stack, PromiseType.PROMISE_SINK);
							//routeFluidTo(nodeT, stack);
						}
					}
				}*/
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
		
		if (heldItem.hasCapability(CapabilityModule.CAPABILITY_MODULE, null)){
		    if (moduleInv.putstack(heldItem)) {
                player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                return true;
            }

        }
		
		return false;
	}
	
	@Override
	public void update() {
		super.update();
		ticksTillSparkle+=1;
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
				moduleInv.disConnect();
			}
			
		}
		if(ticksTillSparkle==10) {
			if(!promises.isEmpty()) {
				promises.forEach(promise -> {
					Triple<Float, Float, Float> rgb = PromiseType.getRGBFromType(promise.getType());
					this.spawnParticle(rgb.getFirst(), rgb.getSecnd(), rgb.getThird());
				});
			}
			ticksTillSparkle=0;
		}
		
		moduleInv.execute();
		checkIfRoutesAreReady();
		//checkIfFluidRoutesAreReady();
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        moduleInv.dropInv(world, pos);
		super.breakBlock(world, pos, state, player);
	}
	
	@Override
	public void disconnect() {
		LogisticsPipes2.logger.log(Level.DEBUG, "Removed TileGenericPipe" + toString() + " from Network:" + network.getName());
		hasNetwork=false;
		network=null;
		moduleInv.disConnect();
	}

	public void routeItemTo(UUID nodeT, Object item, PromiseType type) {
		LogisticsRoute route = network.getRouteFromTo(nodeID, nodeT);
		if (route == null) {
			return;
		}
		waitingToRoute.add(new Triple<>(route, item, type));
		
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
				Triple<LogisticsRoute, Object, PromiseType> route = waitingToRoute.stream()
						.filter(entry -> entry.getFirst().isComplete())
						.findFirst().get();
				Object item = route.getSecnd();
				//ItemStack item = route.getVal();
                Deque<EnumFacing> routeCopy = new ArrayDeque<>(route.getFirst().getdirectionStack());
				EnumFacing side = network.getDirectionForDestination(nodeID);
				LPRoutedObject toRoute = LPRoutedObject.takeFromBlock(world.getTileEntity(getPos().offset(side)), side, item, routeCopy, (TileGenericPipe) route.getFirst().getTarget().getMember(), this, item.getClass());
				MinecraftForge.EVENT_BUS.post(new LPMakePromiseEvent(new LogisticsPromise(toRoute.getID(), route.getThird()), route.getFirst().getTarget().getId()));
				catchItem(toRoute);
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

    public boolean hasInventory() {
        return hasInv;
    }

}
