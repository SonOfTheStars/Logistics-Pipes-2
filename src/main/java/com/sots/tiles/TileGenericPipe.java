package com.sots.tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TileGenericPipe extends TileEntity implements IRoutable, IPipe, ITickable{
	
	protected boolean hasChanged = false;
	protected boolean hasNetwork = false;
	
	protected Network network = null;
	protected UUID nodeID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	protected Map<EnumFacing, Boolean> connections = new HashMap<EnumFacing, Boolean>();
	
	public void setHasChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}

	@Override
	public NBTTagCompound getUpdateTag() {return writeToNBT(new NBTTagCompound());}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
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
	
	public ArrayList<String> checkConnections(IBlockAccess world, BlockPos pos) {
		ArrayList<String> check = ConnectionHelper.checkForPipes(world, pos);
		return check;
		
	}
	
	protected IBlockState getState() {
		return worldObj.getBlockState(pos);
	}
	
	public void updateBlock(){
		if(hasChanged){
			this.worldObj.markBlockRangeForRenderUpdate(this.pos, this.pos);
			this.worldObj.notifyBlockUpdate(pos, getState(), getState(), 3);
			this.worldObj.scheduleBlockUpdate(this.pos, this.getBlockType(), 0, 0);
			
			markDirty();
			hasChanged=false;
		}
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
	public Map<EnumFacing, Boolean> getAdjacentPipes() {
		
		for(int i=0; i<6; i++) {
			EnumFacing direction = EnumFacing.getFront(i);
			if(ConnectionHelper.isPipe(worldObj, pos, direction)) {
				if(connections.containsKey(direction)) {
					connections.replace(direction, true);
				}
				else {
					connections.put(direction, true);
					}
				
			}
			else {
				if(connections.containsKey(direction)) {
					connections.replace(direction, false);
				}
				else {
					connections.put(direction, false);
				}
			}
		}
		
		return connections;
	}
	
	@Override
	public void invalidate() {
		if(network!=null)
			network.purgeNetwork();
	}
	
	
	@Override
	public void update() {
		getAdjacentPipes();
		if(!worldObj.isRemote) {
			if(!hasNetwork) {
				network();
			}
		}
	}
	
	protected void network() {
		for(int i=0; i<6; i++) {
			EnumFacing direction = EnumFacing.getFront(i);
			if(connections.containsKey(direction)) {
				if(connections.get(direction)) {
					TileGenericPipe target = ConnectionHelper.getAdjacentPipe(worldObj, pos, direction);
					
					//First network contact
					if(target.hasNetwork() && !hasNetwork) {
						LogisticsPipes2.logger.log(Level.INFO, "Attempting to connect GenericPipe " + nodeID.toString() + (hasNetwork ? " with network" : " without network") + " to " + target.getBlockType() + (target.hasNetwork ? " with network." : " without network."));
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
	}

	@Override
	public boolean hasAdjacent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int posX() {return pos.getX();}

	@Override
	public int posY() {return pos.getY();}

	@Override
	public int posZ() {return pos.getZ();}
}

