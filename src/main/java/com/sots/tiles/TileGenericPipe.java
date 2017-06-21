package com.sots.tiles;

import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.particle.ParticleUtil;
import com.sots.routing.Network;
import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.util.ConnectionHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileGenericPipe extends TileEntity implements IRoutable, IPipe, ITickable{
	
	public ItemStackHandler inventory = new ItemStackHandler(16){
        @Override
        protected void onContentsChanged(int slot) {
            // We need to tell the tile entity that something has changed so
            // that the chest contents is persisted
        	TileGenericPipe.this.markDirty();
        }
	};
	
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
	public NBTTagCompound getUpdateTag() {return writeToNBT(new NBTTagCompound());}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}
	
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.readFromNBT(packet.getNbtCompound());
    }
	
	@Override
    public void readFromNBT(NBTTagCompound compound) {super.readFromNBT(compound);}
	
	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        
        return compound;
    }
	
	protected IBlockState getState() {
		return worldObj.getBlockState(pos);
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
			if(world.getTileEntity(pos).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()));
				return ConnectionTypes.BLOCK;
		}
		return ConnectionTypes.NONE;
	}
	
	@Override
	public void invalidate() {
		if(network!=null)
			network.purgeNetwork();
	}
	
	
	@Override
	public void update() {
		getAdjacentPipes(worldObj);
		if(!worldObj.isRemote) {
			if(!hasNetwork) {
				network();
			}
		}
	}
	
	protected void network() {
		for(int i=0; i<6; i++) {
			EnumFacing direction = EnumFacing.getFront(i);
			
			if(getConnection(direction) == ConnectionTypes.PIPE) {
				TileGenericPipe target = ConnectionHelper.getAdjacentPipe(worldObj, pos, direction);
				//First network contact
				if(target.hasNetwork() && !hasNetwork) {
					//LogisticsPipes2.logger.log(Level.INFO, String.format("Attempting to connect Generic Pipe %1$s %2$s to %3$s %4$s", nodeID.toString(), (hasNetwork ? " with network" : " without network"), target.getBlockType().toString(), (target.hasNetwork ? " with network." : " without network.")));
					nodeID = target.network.subscribeNode(this);//Subscribe to network
					LogisticsPipes2.logger.log(Level.INFO, "Added TileGenericPipe " + nodeID.toString() + " to Network: " + network.getName());
					hasNetwork=true;
					
					network.getNodeByID(target.nodeID).addNeighbor(network.getNodeByID(nodeID), direction.getOpposite().getIndex());//Tell target node he has a new neighbor
					network.getNodeByID(nodeID).addNeighbor(network.getNodeByID(target.nodeID), direction.getIndex());//Add the Target as my neighbor
					continue;
				}
				//Notify other Neighbors of our presence
				if(target.hasNetwork && hasNetwork) {
					LogisticsPipes2.logger.log(Level.INFO, "Notified GenericPipe " + target.nodeID.toString() + " of presence of: " + nodeID.toString());
					network.getNodeByID(target.nodeID).addNeighbor(network.getNodeByID(nodeID), direction.getOpposite().getIndex());//Tell target node he has a new neighbor
					network.getNodeByID(nodeID).addNeighbor(network.getNodeByID(target.nodeID), direction.getIndex());//Add the Target as my neighbor
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
		case 1:
			if(up == ConnectionTypes.BLOCK)
				return true;
		case 2:
			if(north == ConnectionTypes.BLOCK)
				return true;
		case 3:
			if(south == ConnectionTypes.BLOCK)
				return true;
		case 4:
			if(west == ConnectionTypes.BLOCK)
				return true;
		case 5:
			if(east == ConnectionTypes.BLOCK)
				return true;
		default:
				return false;
		}
	}

	@Override
	public int posX() {return pos.getX();}

	@Override
	public int posY() {return pos.getY();}

	@Override
	public int posZ() {return pos.getZ();}
	
	@Override
	public void spawnParticle(float r, float g, float b) {
		ParticleUtil.spawnGlint(worldObj, posX()+0.5f, posY()+0.5f, posZ()+0.5f, 0, 0, 0, r, g, b, 2.5f, 200);
	}
}

