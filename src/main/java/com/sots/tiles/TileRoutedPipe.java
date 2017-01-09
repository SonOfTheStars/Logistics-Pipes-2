package com.sots.tiles;

import java.util.ArrayList;

import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.util.ConnectionHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TileRoutedPipe extends TileEntity implements IRoutable, IPipe{
	private boolean hasChanged = false;
	
	
	
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
		ArrayList<String> check = ConnectionHelper.checkInventoriesAndPipes(world, pos);
		return check;
	}
	
	private IBlockState getState() {
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
	public boolean isRouted() {return true;}

	@Override
	public boolean isRoutable() {return true;}

	@Override
	public boolean hasNetwork() {return false;}

	@Override
	public boolean hasPower() {return false;}

	@Override
	public boolean consumesPower() {return true;}
	
	@Override
	public int powerConsumed() {return 1;}

	public void setHasChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}
}
