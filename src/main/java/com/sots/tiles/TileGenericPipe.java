package com.sots.tiles;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.sots.util.TileEntityHelper;
import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.item.ItemWrench;
import com.sots.particle.ParticleUtil;
import com.sots.routing.LPRoutedItem;
import com.sots.routing.Network;
import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.util.ConnectionHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

import javax.annotation.Nonnull;

public class TileGenericPipe extends TileEntity implements IRoutable, IPipe, ITickable, ITileEntityBase {

    private volatile Set<LPRoutedItem> contents = new HashSet<LPRoutedItem>();

    public static enum ConnectionTypes {
        NONE, PIPE, BLOCK, FORCENONE
    }

    public ConnectionTypes up = ConnectionTypes.NONE, down = ConnectionTypes.NONE, west = ConnectionTypes.NONE, east = ConnectionTypes.NONE, north = ConnectionTypes.NONE, south = ConnectionTypes.NONE;

    protected boolean hasNetwork = false;

    protected Network network = null;
    public UUID nodeID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static ConnectionTypes typeFromInt(int value) {
        switch (value) {
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
    @Nonnull
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
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        return compound;
    }

    protected IBlockState getState() {
        return world.getBlockState(pos);
    }


    @Override
    public boolean isRouted() {
        return false;
    }

    @Override
    public boolean isRoutable() {
        return true;
    }

    @Override
    public boolean hasNetwork() {
        return hasNetwork;
    }

    public Network getNetwork() {
        return network;
    }

    @Override
    public void subscribe(Network parent) {
        LogisticsPipes2.logger.log(Level.DEBUG, "Subscribed TileGenericPipe" + toString() + " to Network:" + parent.getName());
        network = parent;
        hasNetwork = true;
    }

    @Override
    public void disconnect() {
        LogisticsPipes2.logger.log(Level.DEBUG, "Removed TileGenericPipe" + toString() + " from Network:" + network.getName());
        hasNetwork = false;
        network = null;
    }

    @Override
    public boolean hasPower() {
        return false;
    }

    @Override
    public boolean consumesPower() {
        return false;
    }

    @Override
    public int powerConsumed() {
        return 0;
    }

    @Override
    public void getAdjacentPipes(IBlockAccess world) {
        up = getConnection(world, getPos().up(), EnumFacing.UP);
        down = getConnection(world, getPos().down(), EnumFacing.DOWN);
        north = getConnection(world, getPos().north(), EnumFacing.NORTH);
        south = getConnection(world, getPos().south(), EnumFacing.SOUTH);
        west = getConnection(world, getPos().west(), EnumFacing.WEST);
        east = getConnection(world, getPos().east(), EnumFacing.EAST);
    }


    public ConnectionTypes getConnection(EnumFacing side) {
        switch (side.getIndex()) {
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
        switch (side.getIndex()) {
            case 0:
                down = con;
            case 1:
                up = con;
            case 2:
                north = con;
            case 3:
                south = con;
            case 4:
                west = con;
            case 5:
                east = con;
        }
    }

    public ConnectionTypes getConnection(IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (getConnection(side) == ConnectionTypes.FORCENONE) {
            return ConnectionTypes.FORCENONE;
        }
        if (tile instanceof IPipe) {
            return ConnectionTypes.PIPE;
        } else if (tile != null) {
            if (TileEntityHelper.getInventoryFromTile(tile, side.getOpposite()) != null)
                return ConnectionTypes.BLOCK;
        }
        return ConnectionTypes.NONE;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (network != null)
            network.purgeNetwork();
    }


    @Override
    public void update() {
        getAdjacentPipes(world);
        if (!world.isRemote) {
            if (!hasNetwork) {
                network();
            }
        }
        if (!contents.isEmpty()) {
            //for(LPRoutedItem item : contents) {
            for (Iterator<LPRoutedItem> i = contents.iterator(); i.hasNext(); ) {
                LPRoutedItem item = i.next();
                item.ticks++;
                if (item.ticks == item.TICK_MAX / 2) {
                    item.setHeading(item.getHeadingForNode());
                }
                if (item.ticks == item.TICK_MAX) {
                    if (getConnection(item.getHeading()) == ConnectionTypes.PIPE) {
                        IPipe pipe = (IPipe) world.getTileEntity(getPos().offset(item.getHeading()));
                        if (pipe != null) {
                            pipe.catchItem(item);
                            i.remove();
                        }
                    } else if (getConnection(item.getHeading()) == ConnectionTypes.BLOCK) {
                        TileEntity te = world.getTileEntity(getPos().offset(item.getHeading()));
                        IItemHandler itemHandler = TileEntityHelper.getInventoryFromTile(te, item.getHeading().getOpposite());
                        if (itemHandler != null) {

                            ItemStack itemStack = item.getContent();
                            for (int j = 0; j < itemHandler.getSlots(); j++) {
                                itemStack = itemHandler.insertItem(j, itemStack, false);
                            }
                            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, itemStack));
                        }
                    } else {
                        LogisticsPipes2.logger.info(item.getHeading()); //DEBUG
                        world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, item.getContent()));
                    }
                }
            }
        }
    }

    protected void network() {
        for (int i = 0; i < 6; i++) {
            EnumFacing direction = EnumFacing.getFront(i);

            if (getConnection(direction) == ConnectionTypes.PIPE) {
                TileGenericPipe target = ConnectionHelper.getAdjacentPipe(world, pos, direction);
                //First network contact
                if (target.hasNetwork() && !hasNetwork) {
                    //LogisticsPipes2.logger.log(Level.INFO, String.format("Attempting to connect Generic Pipe %1$s %2$s to %3$s %4$s", nodeID.toString(), (hasNetwork ? " with network" : " without network"), target.getBlockType().toString(), (target.hasNetwork ? " with network." : " without network.")));
                    nodeID = target.network.subscribeNode(this);//Subscribe to network
                    LogisticsPipes2.logger.log(Level.INFO, "Added TileGenericPipe " + nodeID.toString() + " to Network: " + network.getName());
                    hasNetwork = true;

                    network.getNodeByID(target.nodeID).addNeighbor(network.getNodeByID(nodeID), direction.getOpposite().getIndex());//Tell target node he has a new neighbor
                    network.getNodeByID(nodeID).addNeighbor(network.getNodeByID(target.nodeID), direction.getIndex());//Add the Target as my neighbor
                    continue;
                }
                //Notify other Neighbors of our presence
                if (target.hasNetwork && hasNetwork) {
                    LogisticsPipes2.logger.log(Level.INFO, "Notified GenericPipe " + target.nodeID.toString() + " of presence of: " + nodeID.toString());
                    network.getNodeByID(target.nodeID).addNeighbor(network.getNodeByID(nodeID), direction.getOpposite().getIndex());//Tell target node he has a new neighbor
                    network.getNodeByID(nodeID).addNeighbor(network.getNodeByID(target.nodeID), direction.getIndex());//Add the Target as my neighbor
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
        switch (face) {
            case 0:
                if (down == ConnectionTypes.BLOCK)
                    return true;
                break;
            case 1:
                if (up == ConnectionTypes.BLOCK)
                    return true;
                break;
            case 2:
                if (north == ConnectionTypes.BLOCK)
                    return true;
                break;
            case 3:
                if (south == ConnectionTypes.BLOCK)
                    return true;
                break;
            case 4:
                if (west == ConnectionTypes.BLOCK)
                    return true;
                break;
            case 5:
                if (east == ConnectionTypes.BLOCK)
                    return true;
                break;
            default:
                return false;
        }
        return false;
    }

    @Nonnull
    public ItemStack takeFromInventoryOnSide(EnumFacing face, ItemStack item) {
        if (!hasInventoryOnSide(face.getIndex())) {
            return ItemStack.EMPTY;
        }
        TileEntity te = world.getTileEntity(getPos().offset(face));
        IItemHandler itemHandler = TileEntityHelper.getInventoryFromTile(te, face.getOpposite());

        if (itemHandler == null) return ItemStack.EMPTY;

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

    public boolean hasItemInInventoryOnSide(EnumFacing face, ItemStack item) {
        if (!hasInventoryOnSide(face.getIndex())) {
            return false;
        }
        TileEntity te = world.getTileEntity(getPos().offset(face));
        IItemHandler itemHandler = TileEntityHelper.getInventoryFromTile(te, face.getOpposite());

        if (itemHandler == null) return false;

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


    @Override
    public int posX() {
        return pos.getX();
    }

    @Override
    public int posY() {
        return pos.getY();
    }

    @Override
    public int posZ() {
        return pos.getZ();
    }

    @Override
    public void spawnParticle(float r, float g, float b) {
        ParticleUtil.spawnGlint(world, posX() + 0.5f, posY() + 0.5f, posZ() + 0.5f, 0, 0, 0, r, g, b, 2.5f, 200);
    }

    protected ConnectionTypes forceConnection(ConnectionTypes con) {
        if (con == ConnectionTypes.FORCENONE) {
            return ConnectionTypes.NONE;
        }
        if (con != ConnectionTypes.FORCENONE) {
            return ConnectionTypes.FORCENONE;
        }
        return ConnectionTypes.NONE;
    }

    @Override
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {

        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem.isEmpty()) {
            if (heldItem.getItem() instanceof ItemWrench) {
                if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
                    if (Math.abs(hitX - 0.75) > Math.abs(hitZ - 0.75)) {
                        if (hitX < 0.75) {
                            this.west = forceConnection(west);
                        } else {
                            this.east = forceConnection(east);
                        }
                    } else {
                        if (hitZ < 0.75) {
                            this.north = forceConnection(north);
                        } else {
                            this.south = forceConnection(south);
                        }
                    }
                }
                if (side == EnumFacing.EAST || side == EnumFacing.WEST) {
                    if (Math.abs(hitY - 0.75) > Math.abs(hitZ - 0.75)) {
                        if (hitY < 0.75) {
                            this.down = forceConnection(down);
                        } else {
                            this.up = forceConnection(up);
                        }
                    } else {
                        if (hitZ < 0.75) {
                            this.north = forceConnection(north);
                        } else {
                            this.south = forceConnection(south);
                        }
                    }
                }
                if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH) {
                    if (Math.abs(hitX - 0.75) > Math.abs(hitY - 0.75)) {
                        if (hitX < 0.75) {
                            this.west = forceConnection(west);
                        } else {
                            this.east = forceConnection(east);
                        }
                    } else {
                        if (hitY < 0.75) {
                            this.down = forceConnection(down);
                        } else {
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
            spawnParticle(1f, 1f, 1f);
            //LogisticsPipes2.logger.info("Caugth an item");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean passItem(TileGenericPipe pipe, LPRoutedItem item) {
        if (pipe != null && item != null) {
            return pipe.catchItem(item);
        }
        return false;
    }

    public Set<LPRoutedItem> getContents() {
        return contents;
    }

}

