package com.sots.tiles;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.UnmodifiableIterator;
import com.sots.util.Connections;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;

public class TileBasicPipe extends TileEntity{
	
	private boolean hasChanged = false;
	
	private final List<String> hidden = new ArrayList<String>();
	public final IModelState state = new IModelState()
	{
		private final Optional<TRSRTransformation> value = Optional.of(TRSRTransformation.identity());
		
		@Override
		public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
        {
            if(part.isPresent())
            {
                // This whole thing is subject to change, but should do for now.
                UnmodifiableIterator<String> parts = Models.getParts(part.get());
                if(parts.hasNext())
                {
                    String name = parts.next();
                    // only interested in the root level
                    if(!parts.hasNext() && hidden.contains(name))
                    {
                        return value;
                    }
                }
            }
            return Optional.absent();
        }
		
	};
	
	
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
	
	public boolean canConnect(IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		return (te instanceof TileBasicPipe);
	}
	
	public void checkConnections(IBlockAccess world, BlockPos pos) {
		hidden.add("CUP");
		hidden.add("CDOWN");
		hidden.add("CNORTH");
		hidden.add("CSOUTH");
		hidden.add("CEAST");
		hidden.add("CWEST");
		hidden.add("GUP");
		hidden.add("GDOWN");
		hidden.add("GNORTH");
		hidden.add("GWEST");
		hidden.add("GSOUTH");
		hidden.add("GEAST");
		hidden.add("GCENTER");
		
		
		//North Connection
		if(canConnect(world, pos.north())) {
			//if(hidden.contains("UP"))
				hidden.remove(Connections.NORTH.toString());
				hasChanged=true;
		}
		else {hidden.add(Connections.NORTH.toString());hasChanged=true;}
		
		//South Connection
		if(canConnect(world, pos.south())) {
			//if(hidden.contains("DOWN"))
				hidden.remove(Connections.SOUTH.toString());
				hasChanged=true;
		}
		else{hidden.add(Connections.SOUTH.toString());hasChanged=true;}
		
		//East Connection
		if(canConnect(world, pos.east())) {
			//if(hidden.contains("EAST"))
				hidden.remove(Connections.EAST.toString());
				hasChanged=true;
		}
		else{hidden.add(Connections.EAST.toString());hasChanged=true;}
		
		//West Connection
		if(canConnect(world, pos.west())) {
			//if(hidden.contains("WEST"))
				hidden.remove(Connections.WEST.toString());
				hasChanged=true;
		}
		else{hidden.add(Connections.WEST.toString());hasChanged=true;}
		
		//Up Connection
		if(canConnect(world, pos.up())) {
			//if(hidden.contains("SOUTH"))
				hidden.remove(Connections.UP.toString());
				hasChanged=true;
		}
		else{hidden.add(Connections.UP.toString());hasChanged=true;}
		
		//Down Connection
		if(canConnect(world, pos.down())) {
			//if(hidden.contains("NORTH"))
				hidden.remove(Connections.DOWN.toString());
				hasChanged=true;
		}
		else{hidden.add(Connections.DOWN.toString());hasChanged=true;}
		
		if(worldObj.isRemote) {
			this.worldObj.markBlockRangeForRenderUpdate(this.pos, this.pos);
			this.worldObj.notifyBlockUpdate(pos, getState(), getState(), 3);
			this.worldObj.scheduleBlockUpdate(this.pos, this.getBlockType(), 0, 0);
			
			markDirty();
		}
		
	}
	
	private IBlockState getState() {
		return worldObj.getBlockState(pos);
	}
	
}
