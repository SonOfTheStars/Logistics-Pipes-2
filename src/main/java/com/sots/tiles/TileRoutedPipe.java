package com.sots.tiles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.event.LPMakePromiseEvent;
import com.sots.item.ItemWrench;
import com.sots.item.modules.IItemModule;
import com.sots.module.logic.IModuleLogic;
import com.sots.routing.LPRoutedObject;
import com.sots.routing.LogisticsRoute;
import com.sots.routing.interfaces.IDestination;
import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.routing.promises.LogisticsPromise;
import com.sots.routing.promises.PromiseType;
import com.sots.util.Connections;
import com.sots.util.Misc;
import com.sots.util.ModuleInv;
import com.sots.util.References;
import com.sots.util.data.Quad;
import com.sots.util.data.Triple;
import com.sots.util.helpers.ItemHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
import net.minecraftforge.items.ItemStackHandler;

public class TileRoutedPipe extends TileGenericPipe implements IRoutable, IPipe, IDestination {

    protected boolean hasInv = false;
    protected int ticksTillSparkle=0;
    private final ModuleInv moduleInv;
    protected ItemStackHandler modules;
    protected List<IModuleLogic> executables = new ArrayList<>();
    protected List<Quad<LogisticsRoute, Item, PromiseType, Integer>> waitingToRoute = new ArrayList<>();
    protected List<LogisticsPromise> promises = new ArrayList<>();
    
	public TileRoutedPipe() {
        this.moduleInv = new ModuleInv(this, References.MOD_COUNT_BASE);
        MinecraftForge.EVENT_BUS.register(this);
        modules = new ItemStackHandler(References.MOD_COUNT_BASE);
    }
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		modules.deserializeNBT(compound.getCompoundTag("modules"));
		if(!world.isRemote) {
			for(int i = 0; i<modules.getSlots(); i++) {
				IItemModule mod = (IItemModule) modules.getStackInSlot(i).getItem();
				executables.add(mod.getModLogic());
			}
		}
		
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
									if(!ItemHelper.isEmptyOrZero(stack)) {
										current.routeItemTo(nodeT, stack.getItem(), PromiseType.PROMISE_SINK, 4);
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
						if(!ItemHelper.isEmptyOrZero(stack)) {
							routeItemTo(nodeT, stack.getItem(), PromiseType.PROMISE_SINK, 1);
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
				IItemModule mod = (IItemModule) heldItem.getItem();
				if(mod.canInsert()){
					modules.insertItem(0, heldItem.splitStack(1), false);
					if(heldItem.isEmpty()) {
						heldItem = null;
					}
					executables.add(mod.getModLogic());
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
		ticksTillSparkle+=1;
		if(!world.isRemote) {
			if (this.hasNetwork && !(network.getNodeByID(this.nodeID).isDestination())) {
				for (int i = 0; i < 6; i++) {
					if (hasInventoryOnSide(i)) {
						EnumFacing face = EnumFacing.getFront(i);
						network.registerDestination(this.nodeID, face);
						hasInv=true;
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
					executables.forEach(exe -> exe.onInvalidate(this));
				}
			}
		}
		if(ticksTillSparkle==10) {
			if(!promises.isEmpty()) {
				promises.forEach(promise -> {
					Triple<Float, Float, Float> rgb = PromiseType.getRGBFromType(promise.getType());
					this.spawnParticle(rgb);
				});
			}
			ticksTillSparkle=0;
		}
		
		if(!world.isRemote) {
			executables.forEach(exe -> exe.execute(this));
			checkIfRoutesAreReady();
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        executables.forEach(exe -> exe.onInvalidate(this));
        executables.clear();
        Misc.spawnInventoryInWorld(world, pos, modules);
		super.breakBlock(world, pos, state, player);
	}
	
	@Override
	public void disconnect() {
		LogisticsPipes2.logger.log(Level.DEBUG, "Removed TileGenericPipe" + toString() + " from Network:" + network.getName());
		executables.forEach(exe -> exe.onInvalidate(this));
		hasNetwork=false;
		network=null;
	}

	public void routeItemTo(UUID nodeT, Item item, PromiseType type, int amount) {
		LogisticsRoute route = network.getRouteFromTo(nodeID, nodeT);
		if (route == null) {
			return;
		}
		waitingToRoute.add(new Quad<>(route, item, type, amount));
		
	}

	@SuppressWarnings("rawtypes")
	protected void checkIfRoutesAreReady() {
		if (!waitingToRoute.isEmpty()) {
			
			try {
				Quad<LogisticsRoute, Item, PromiseType, Integer> route = waitingToRoute.stream()
						.filter(entry -> entry.getA().isComplete())
						.findFirst().get();
				ItemStack item = new ItemStack(route.getB());
                Deque<EnumFacing> routeCopy = new ArrayDeque<>(route.getA().getdirectionStack());
				EnumFacing side = network.getDirectionForDestination(nodeID);
				LPRoutedObject toRoute = LPRoutedObject.takeFromBlock(world.getTileEntity(getPos().offset(side)), side, item, routeCopy, (TileGenericPipe) route.getA().getTarget().getMember(), this, item.getClass(), route.getD());
				MinecraftForge.EVENT_BUS.post(new LPMakePromiseEvent(new LogisticsPromise(toRoute.getID(), route.getC()), route.getA().getTarget().getId()));
				catchItem(toRoute);
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
