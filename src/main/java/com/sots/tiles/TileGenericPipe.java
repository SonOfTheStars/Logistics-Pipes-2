package com.sots.tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.util.ConnectionHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TileGenericPipe extends TileEntity implements IRoutable, IPipe{
	
	protected boolean hasChanged = false;
	protected boolean hasNetwork = false;
	
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
	public boolean hasNetwork() {return false;}
	
	@Override
	public void network() {hasNetwork=true;}

	@Override
	public boolean hasPower() {return false;}

	@Override
	public boolean consumesPower() {return false;}
	
	@Override
	public int powerConsumed() {return 0;}
	
	@Override
	public Map<EnumFacing, IPipe> getAdjacentPipes() {
		Map<EnumFacing, IPipe> out = new HashMap<EnumFacing, IPipe>();
		
		for(int i=0; i<6; i++) {
			out.put(EnumFacing.getFront(i), ConnectionHelper.getAdjacentPipe(worldObj, pos, EnumFacing.getFront(i)));
		}
		
		return out;
	}
	
	@Override
	public boolean hasAdjacent() {
		boolean out=false;
		
		for(int i =0; i<6; i++) {
			if(!out) {
				out = (ConnectionHelper.isPipe(worldObj, pos, EnumFacing.getFront(i)));
			}
			else {break;}
		}
		
		return out;
	}
}

