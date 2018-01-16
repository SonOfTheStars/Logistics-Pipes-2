package com.sots.tiles;

import java.util.*;

import org.apache.logging.log4j.Level;

import com.sots.EventManager;
import com.sots.LogisticsPipes2;
import com.sots.item.ItemWrench;
import com.sots.particle.ParticleUtil;
import com.sots.routing.LPRoutedItem;
import com.sots.routing.LogisticsRoute;
import com.sots.routing.LPRoutedFluid;
import com.sots.routing.Network;
import com.sots.routing.NetworkNode;
import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.util.ConnectionHelper;
import com.sots.util.data.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.fluids.capability.*;
import net.minecraftforge.fluids.*;

public class TileGenericPipe extends TileEntity implements IRoutable, IPipe, ITickable, ITileEntityBase{
	
	private volatile Set<LPRoutedItem> contents = new HashSet<LPRoutedItem>();
	private volatile Set<LPRoutedFluid> contents_fluid = new HashSet<LPRoutedFluid>();
	private List<Triple<LogisticsRoute, ItemStack, EnumFacing>> waitingToReroute = new ArrayList<Triple<LogisticsRoute, ItemStack, EnumFacing>>();
	private List<Triple<LogisticsRoute, FluidStack, EnumFacing>> waitingToReroute_fluid = new ArrayList<Triple<LogisticsRoute, FluidStack, EnumFacing>>();
	
	public static enum ConnectionTypes{
		NONE, PIPE, BLOCK, FORCENONE
	}
	
	public ConnectionTypes up = ConnectionTypes.NONE, down = ConnectionTypes.NONE, west = ConnectionTypes.NONE, east = ConnectionTypes.NONE, north = ConnectionTypes.NONE, south = ConnectionTypes.NONE;
	
	protected boolean hasNetwork = false;
	
	protected Network network = null;
	public UUID nodeID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	public static ConnectionTypes typeFromInt(int value) {
		switch(value) {
		case 0:
				return ConnectionTypes.NONE;
		case 1:
				return ConnectionTypes.PIPE;
		case 2:
				return ConnectionTypes.BLOCK;
		case 3:
				return ConnectionTypes.FORCENONE;
		}
		return ConnectionTypes.NONE;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		
		
		return writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}
	
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.readFromNBT(packet.getNbtCompound());
    }
	
	@Override
    public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(compound.hasKey("contents")) {
			NBTTagList list = (NBTTagList) compound.getTag("contents");
			contents.clear();
			for(Iterator<NBTBase> i = list.iterator(); i.hasNext();) {
				contents.add(LPRoutedItem.readFromNBT((NBTTagCompound) i.next(), this));
			}
		}
		if(compound.hasKey("contents_fluid")) {
			NBTTagList list = (NBTTagList) compound.getTag("contents_fluid");
			contents_fluid.clear();
			for(Iterator<NBTBase> i = list.iterator(); i.hasNext();) {
				contents_fluid.add(LPRoutedFluid.readFromNBT((NBTTagCompound) i.next(), this));
			}
		}
	}
	
	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        NBTTagList list = new NBTTagList();
        for(LPRoutedItem lpri : contents) {
        	list.appendTag(lpri.writeToNBT());
        }
        if(!list.hasNoTags()) {
        	compound.setTag("contents", list);
        }
        NBTTagList list_fluid = new NBTTagList();
        for(LPRoutedFluid lprf : contents_fluid) {
        	list_fluid.appendTag(lprf.writeToNBT());
        }
        if(!list_fluid.hasNoTags()) {
        	compound.setTag("contents_fluid", list_fluid);
        }
        
        return compound;
    }
	
	protected IBlockState getState() {
		return world.getBlockState(pos);
	}
	
	
	@Override
	public boolean isRouted() {return false;}

	@Override
	public boolean isRoutable() {return true;}

	@Override
	public boolean hasNetwork() {return hasNetwork;}
	
	public Network getNetwork() {return network;}
	
	@Override
	public void subscribe(Network parent) {
		LogisticsPipes2.logger.log(Level.DEBUG, "Subscribed TileGenericPipe" + toString() + " to Network:" + parent.getName());
		network = parent;
		hasNetwork=true;
		}
	
	@Override
	public void disconnect() {
		LogisticsPipes2.logger.log(Level.DEBUG, "Removed TileGenericPipe" + toString() + " from Network:" + network.getName());
		hasNetwork=false;
		network=null;
	}

	@Override
	public boolean hasPower() {return false;}

	@Override
	public boolean consumesPower() {return false;}
	
	@Override
	public int powerConsumed() {return 0;}
	
	@Override
	public void getAdjacentPipes(IBlockAccess world) {
		up = getConnection(world,getPos().up(),EnumFacing.UP);
		down = getConnection(world,getPos().down(),EnumFacing.DOWN);
		north = getConnection(world,getPos().north(),EnumFacing.NORTH);
		south = getConnection(world,getPos().south(),EnumFacing.SOUTH);
		west = getConnection(world,getPos().west(),EnumFacing.WEST);
		east = getConnection(world,getPos().east(),EnumFacing.EAST);
	}
	
	
	public ConnectionTypes getConnection(EnumFacing side) {
		switch(side.getIndex()) {
		case 0:
			return down;
		case 1:
			return up;
		case 2:
			return north;
		case 3:
			return south;
		case 4:
			return west;
		case 5:
			return east;
			
		}
		return ConnectionTypes.NONE;
	}
	
	public void setConnection(EnumFacing side, ConnectionTypes con) {
		switch(side.getIndex()) {
		case 0:
			down = con;
		case 1:
			up = con;
		case 2:
			north  = con;
		case 3:
			south = con;
		case 4:
			west  = con;
		case 5:
			east = con;
		}
	}
	
	public ConnectionTypes getConnection(IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity tile = world.getTileEntity(pos);
		if(getConnection(side) == ConnectionTypes.FORCENONE) {
			return ConnectionTypes.FORCENONE;
		}
		if(tile instanceof IPipe) {
			return ConnectionTypes.PIPE;
		}
		else if(tile!=null) {
			if(world.getTileEntity(pos).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()))
				return ConnectionTypes.BLOCK;
			if(world.getTileEntity(pos).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()))
				return ConnectionTypes.BLOCK;
		}
		return ConnectionTypes.NONE;
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		if(network!=null)
			network.purgeNetwork();
	}
	
	
	@Override
	public void update() {
		getAdjacentPipes(world);
		if(!world.isRemote) {
			if(!hasNetwork) {
				network();
			}
		}
		if(!contents.isEmpty()) {
			//for(LPRoutedItem item : contents) {
			for(Iterator<LPRoutedItem> i = contents.iterator(); i.hasNext();) {
				LPRoutedItem item = i.next();
				item.ticks++;
				if(item.ticks==item.TICK_MAX/2) {
					item.setHeading(item.getHeadingForNode());
				}
				if(item.ticks==item.TICK_MAX) {
					//boolean debug = world.isRemote;
					if(getConnection(item.getHeading())==ConnectionTypes.PIPE) {
						IPipe pipe = (IPipe) world.getTileEntity(getPos().offset(item.getHeading()));
						if(pipe!=null) {
							if (!pipe.catchItem(item)) {
								if (!world.isRemote) {
									world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, item.getContent()));
								}
							}

//							i.remove();
						}
					}
					else if (getConnection(item.getHeading())==ConnectionTypes.BLOCK) {
						TileEntity te = world.getTileEntity(getPos().offset(item.getHeading()));
						if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, item.getHeading().getOpposite())) {
							IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, item.getHeading().getOpposite());
							ItemStack itemStack = item.getContent();
							for (int j = 0; j < itemHandler.getSlots(); j++) {
								itemStack = itemHandler.insertItem(j, itemStack, false);
							}
							if(!itemStack.isEmpty())
								if (!world.isRemote) {
									world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, itemStack));
								}
						} else {
							if (!world.isRemote) {
								world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, item.getContent()));
							}
						}

					}
					else {
						if (!world.isRemote) {
								try {
									if (item.getDestination() != null && network.getAllDestinations().contains(item.getDestination().nodeID)) {
										rerouteItemTo(item.getDestination().nodeID, item.getContent(), item.getHeading());
									} else {
										LogisticsPipes2.logger.info(item.getHeading()); //DEBUG
										if (!world.isRemote) {
											world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, item.getContent()));
										}
									}
								} catch (Exception e) {
									contents.remove(item);
									world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, item.getContent()));
									continue;
								}
						}
					}
					i.remove();
				}
			}
			markForUpdate();
		}
		checkIfReroutesAreReady();
		if(!contents_fluid.isEmpty()) {
			//for(LPRoutedItem item : contents_fluid) {
			for(Iterator<LPRoutedFluid> i = contents_fluid.iterator(); i.hasNext();) {
				LPRoutedFluid fluid = i.next();
				fluid.ticks++;
				if(fluid.ticks==fluid.TICK_MAX/2) {
					fluid.setHeading(fluid.getHeadingForNode());
				}
				if(fluid.ticks==fluid.TICK_MAX) {
					//boolean debug = world.isRemote;
					if(getConnection(fluid.getHeading())==ConnectionTypes.PIPE) {
						IPipe pipe = (IPipe) world.getTileEntity(getPos().offset(fluid.getHeading()));
						if(pipe!=null) {
							if (!pipe.catchFluid(fluid)) {
								if (!world.isRemote) {
									world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, UniversalBucket.getFilledBucket(new UniversalBucket(), fluid.getContent().getFluid())));
								}
							}

//							i.remove();
						}
					}
					else if (getConnection(fluid.getHeading())==ConnectionTypes.BLOCK) {
						TileEntity te = world.getTileEntity(getPos().offset(fluid.getHeading()));
						if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, fluid.getHeading().getOpposite())) {
							IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, fluid.getHeading().getOpposite());
							FluidStack fluidStack = fluid.getContent();
							//for (int j = 0; j < fluidHandler.getSlots(); j++) {
								//fluidStack = fluidHandler.insertFluid(j, fluidStack, false);
							//}
							int amountLeft = fluidHandler.fill(fluidStack, true);
							//if(!fluidStack.isEmpty())
							if (amountLeft < fluidStack.amount)
								if (!world.isRemote) {
									world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, UniversalBucket.getFilledBucket(new UniversalBucket(), fluidStack.getFluid())));
								}
						} else {
							if (!world.isRemote) {
								world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, UniversalBucket.getFilledBucket(new UniversalBucket(), fluid.getContent().getFluid())));
							}
						}

					}
					else {
						if (!world.isRemote) {
								if (fluid.getDestination() != null && network.getAllDestinations().contains(fluid.getDestination().nodeID)) {
									rerouteFluidTo(fluid.getDestination().nodeID, fluid.getContent(), fluid.getHeading());
								} else {
									LogisticsPipes2.logger.info(fluid.getHeading()); //DEBUG
									if (!world.isRemote) {
										world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, UniversalBucket.getFilledBucket(new UniversalBucket(), fluid.getContent().getFluid())));
									}
								}
						}
					}
					i.remove();
				}
			}
			markForUpdate();
		}
		checkIfFluidReroutesAreReady();
	}
	
	protected void network() {
		for(int i=0; i<6; i++) {
			EnumFacing direction = EnumFacing.getFront(i);
			
			if(getConnection(direction) == ConnectionTypes.PIPE) {
				TileGenericPipe target = ConnectionHelper.getAdjacentPipe(world, pos, direction);
				//First network contact
				if(target.hasNetwork() && !hasNetwork) {
					//LogisticsPipes2.logger.log(Level.INFO, String.format("Attempting to connect Generic Pipe %1$s %2$s to %3$s %4$s", nodeID.toString(), (hasNetwork ? " with network" : " without network"), target.getBlockType().toString(), (target.hasNetwork ? " with network." : " without network.")));
					nodeID = target.network.subscribeNode(this);//Subscribe to network
					LogisticsPipes2.logger.log(Level.INFO, "Added TileGenericPipe " + nodeID.toString() + " to Network: " + network.getName());
					hasNetwork=true;
					
					network.getNodeByID(target.nodeID).addNeighbor(network.getNodeByID(nodeID), direction.getOpposite().getIndex());//Tell target node he has a new neighbor
					network.getNodeByID(nodeID).addNeighbor(network.getNodeByID(target.nodeID), direction.getIndex());//Add the Target as my neighbor
					network.recalculateNetwork();
					continue;
				}
				//Notify other Neighbors of our presence
				if(target.hasNetwork && hasNetwork) {
					LogisticsPipes2.logger.log(Level.INFO, "Notified GenericPipe " + target.nodeID.toString() + " of presence of: " + nodeID.toString());
					network.getNodeByID(target.nodeID).addNeighbor(network.getNodeByID(nodeID), direction.getOpposite().getIndex());//Tell target node he has a new neighbor
					network.getNodeByID(nodeID).addNeighbor(network.getNodeByID(target.nodeID), direction.getIndex());//Add the Target as my neighbor
					network.recalculateNetwork();
					continue;
				}
			}
		}
	}

	@Override
	public boolean hasAdjacent() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean hasInventoryOnSide(int face) {
		switch(face){
		case 0:
			if(down == ConnectionTypes.BLOCK)
				return true;
			break;
		case 1:
			if(up == ConnectionTypes.BLOCK)
				return true;
			break;
		case 2:
			if(north == ConnectionTypes.BLOCK)
				return true;
			break;
		case 3:
			if(south == ConnectionTypes.BLOCK)
				return true;
			break;
		case 4:
			if(west == ConnectionTypes.BLOCK)
				return true;
			break;
		case 5:
			if(east == ConnectionTypes.BLOCK)
				return true;
			break;
		default:
				return false;
		}
		return false;
	}

	public ItemStack takeFromInventoryOnSide(EnumFacing face, ItemStack item) {
		if (!hasInventoryOnSide(face.getIndex())) {
			return null;
		}
		TileEntity te = world.getTileEntity(getPos().offset(face));
		if (!te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite())) {
			return null;
		}
		IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
		ItemStack result = ItemStack.EMPTY;
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			if (itemHandler.getStackInSlot(i).isItemEqual(item)) {
				ItemStack tmp = itemHandler.extractItem(i, item.getCount() - result.getCount(), false);
				result = new ItemStack(item.getItem(), tmp.getCount() + result.getCount());
			}
			if (result.getCount() >= item.getCount()) {
				break;
			}
		}
		return result;
	}

	public FluidStack takeFluidFromInventoryOnSide(EnumFacing face, FluidStack fluid) {
		if (!hasInventoryOnSide(face.getIndex())) {
			return null;
		}
		TileEntity te = world.getTileEntity(getPos().offset(face));
		if (!te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite())) {
			return null;
		}
		IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite());
		FluidStack result = new FluidStack(fluid.getFluid(), 0);
		for (int i = 0; i < fluidHandler.getTankProperties().length; i++) {
			if (fluidHandler.getTankProperties()[i].getContents().isFluidEqual(fluid)) {
				FluidStack tmp = fluidHandler.drain(fluid, true);
				result = new FluidStack(fluid.getFluid(), tmp.amount + result.amount);
				fluid = new FluidStack(fluid.getFluid(), fluid.amount - tmp.amount);
			}
			if (fluid.amount <= 0) {
				break;
			}
		}
		if (result.amount == 0)
			return null;
		return result;
	}
	
	public boolean hasItemInInventoryOnSide(EnumFacing face, ItemStack item) {
		if (!hasInventoryOnSide(face.getIndex())) {
			return false;
		}
		TileEntity te = world.getTileEntity(getPos().offset(face));
		if (!te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite())) {
			return false;
		}
		IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
		ItemStack result = ItemStack.EMPTY;
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			if (itemHandler.getStackInSlot(i).isItemEqual(item)) {
				result = new ItemStack(item.getItem(), itemHandler.getStackInSlot(i).getCount() + result.getCount());
			}
			if (result.getCount() >= item.getCount()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasFluidInInventoryOnSide(EnumFacing face, FluidStack fluid) {
		if (!hasInventoryOnSide(face.getIndex())) {
			return false;
		}
		TileEntity te = world.getTileEntity(getPos().offset(face));
		if (!te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite())) {
			return false;
		}
		IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite());
		FluidStack result = new FluidStack(fluid.getFluid(), 0);
		for (int i = 0; i < fluidHandler.getTankProperties().length; i++) {
			if (fluidHandler.getTankProperties()[i].getContents().isFluidEqual(fluid)) {
				result = new FluidStack(fluid.getFluid(), fluidHandler.getTankProperties()[i].getContents().amount + result.amount);
			}
			if (result.amount >= fluid.amount) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<ItemStack> getItemsInInventory(EnumFacing face){
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();
		
		if (!hasInventoryOnSide(face.getIndex())) {
			return result;
		}
		TileEntity te = world.getTileEntity(getPos().offset(face));
		if (!te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite())) {
			return result;
		}
		IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
		for(int i=0; i < itemHandler.getSlots(); i++) {
			ItemStack slotStack = itemHandler.getStackInSlot(i);
			if(!slotStack.isEmpty()) {
				result.add(slotStack);
			}
		}
		return result;
	}

	public ArrayList<Item> getItemTypesInInventory(EnumFacing face){
		ArrayList<Item> result = new ArrayList<Item>();
		
		if (!hasInventoryOnSide(face.getIndex())) {
			return result;
		}
		TileEntity te = world.getTileEntity(getPos().offset(face));
		if (!te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite())) {
			return result;
		}
		IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
		for(int i=0; i < itemHandler.getSlots(); i++) {
			ItemStack slotStack = itemHandler.getStackInSlot(i);
			Item item = slotStack.getItem();
			if(!slotStack.isEmpty() && !result.contains(item)) {
				result.add(item);
			}
		}
		return result;
	}


	public ArrayList<FluidStack> getFluidStacksInInventory(EnumFacing face){
		ArrayList<FluidStack> result = new ArrayList<FluidStack>();
		
		if (!hasInventoryOnSide(face.getIndex())) {
			return result;
		}
		TileEntity te = world.getTileEntity(getPos().offset(face));
		if (!te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite())) {
			return result;
		}
		IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite());
		for (IFluidTankProperties tank : fluidHandler.getTankProperties()) {
			if (!(tank.getContents() == null)) {
				result.add(tank.getContents());
			}
		}
		return result;
	}


	@Override
	public int posX() {return pos.getX();}

	@Override
	public int posY() {return pos.getY();}

	@Override
	public int posZ() {return pos.getZ();}
	
	@Override
	public void spawnParticle(float r, float g, float b) {
		ParticleUtil.spawnGlint(world, posX()+0.5f, posY()+0.5f, posZ()+0.5f, 0, 0, 0, r, g, b, 2.5f, 200);
	}
	
	protected ConnectionTypes forceConnection(ConnectionTypes con) {
		if(con== ConnectionTypes.FORCENONE) {
			return ConnectionTypes.NONE;
		}
		if(con != ConnectionTypes.FORCENONE) {
			return ConnectionTypes.FORCENONE;
		}
		return ConnectionTypes.NONE;
	}

	@Override
	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		
		ItemStack heldItem = player.getHeldItem(hand);
		if(heldItem.getItem()!=null) {
			if(heldItem.getItem() instanceof ItemWrench) {
				if (side == EnumFacing.UP || side == EnumFacing.DOWN){
					if (Math.abs(hitX-0.75) > Math.abs(hitZ-0.75)){
						if (hitX < 0.75){
							this.west = forceConnection(west);
						}
						else {
							this.east = forceConnection(east);
						}
					}
					else {
						if (hitZ < 0.75){
							this.north = forceConnection(north);
						}
						else {
							this.south = forceConnection(south);
						}
					}
				}
				if (side == EnumFacing.EAST || side == EnumFacing.WEST){
					if (Math.abs(hitY-0.75) > Math.abs(hitZ-0.75)){
						if (hitY < 0.75){
							this.down = forceConnection(down);
						}
						else {
							this.up = forceConnection(up);
						}
					}
					else {
						if (hitZ < 0.75){
							this.north = forceConnection(north);
						}
						else {
							this.south = forceConnection(south);
						}
					}
				}
				if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH){
					if (Math.abs(hitX-0.75) > Math.abs(hitY-0.75)){
						if (hitX < 0.75){
							this.west = forceConnection(west);
						}
						else {
							this.east = forceConnection(east);
						}
					}
					else {
						if (hitY < 0.75){
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
		
		
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		
	}
	
	public boolean catchItem(LPRoutedItem item) {
		try {
			contents.add(item);
			item.ticks = 0;
			//spawnParticle(1f, 1f, 1f);
			//LogisticsPipes2.logger.info("Caugth an item");
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

	public boolean catchFluid(LPRoutedFluid fluid) {
		try {
			contents_fluid.add(fluid);
			fluid.ticks = 0;
			//spawnParticle(1f, 1f, 1f);
			//LogisticsPipes2.logger.info("Caugth an item");
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	private boolean passItem(TileGenericPipe pipe, LPRoutedItem item) {
		if(pipe!=null && item!=null) {
			return pipe.catchItem(item);
		}
		return false;
	}
	
	public Set<LPRoutedItem> getContents(){
		return contents;
	}

	public Set<LPRoutedFluid> getContents_fluid(){
		return contents_fluid;
	}

	@Override
	public void markForUpdate() {
		EventManager.markTEForUpdate(this.getPos(), this);
	}

	public void rerouteItemTo(UUID nodeT, ItemStack item, EnumFacing side) {
		LogisticsRoute route = network.getRouteFromTo(nodeID, nodeT);
		if (route == null) {
			LogisticsPipes2.logger.info("Route returned null");
			return;
		}
		waitingToReroute.add(new Triple<LogisticsRoute, ItemStack, EnumFacing>(route, item, side.getOpposite()));
	}

	public void rerouteFluidTo(UUID nodeT, FluidStack fluid, EnumFacing side) {
		LogisticsRoute route = network.getRouteFromTo(nodeID, nodeT);
		if (route == null) {
			LogisticsPipes2.logger.info("Route returned null");
			return;
		}
		waitingToReroute_fluid.add(new Triple<LogisticsRoute, FluidStack, EnumFacing>(route, fluid, side.getOpposite()));
	}

	private void checkIfReroutesAreReady() {
		if (!waitingToReroute.isEmpty()) {
			for (Iterator<Triple<LogisticsRoute, ItemStack, EnumFacing>> i = waitingToReroute.iterator(); i.hasNext();) {
				Triple<LogisticsRoute, ItemStack, EnumFacing> route = i.next();
				if (!route.getFirst().isComplete()) {
					continue;
				}
				ItemStack item = route.getSecnd();
				Deque<EnumFacing> routeCopy = new ArrayDeque<EnumFacing>();
				routeCopy.addAll(route.getFirst().getdirectionStack());
				EnumFacing side = route.getThird();
				catchItem(new LPRoutedItem((double) posX(), (double) posY(), (double) posZ(), item, side, this, routeCopy, (TileGenericPipe) route.getFirst().getTarget().getMember()));
				i.remove();

				break; // This line makes it so, that only 1 item is routed pr. tick. Comment out this line to allow multiple items to be routed pr. tick.
			}
		}
	}

	private void checkIfFluidReroutesAreReady() {
		if (!waitingToReroute_fluid.isEmpty()) {
			for (Iterator<Triple<LogisticsRoute, FluidStack, EnumFacing>> i = waitingToReroute_fluid.iterator(); i.hasNext();) {
				Triple<LogisticsRoute, FluidStack, EnumFacing> route = i.next();
				if (!route.getFirst().isComplete()) {
					continue;
				}
				FluidStack fluid = route.getSecnd();
				Deque<EnumFacing> routeCopy = new ArrayDeque<EnumFacing>();
				routeCopy.addAll(route.getFirst().getdirectionStack());
				EnumFacing side = route.getThird();
				catchFluid(new LPRoutedFluid((double) posX(), (double) posY(), (double) posZ(), fluid, side, this, routeCopy, (TileGenericPipe) route.getFirst().getTarget().getMember()));
				i.remove();

				break; // This line makes it so, that only 1 item is routed pr. tick. Comment out this line to allow multiple items to be routed pr. tick.
			}
		}
	}
}

