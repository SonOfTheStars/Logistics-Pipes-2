package com.sots.tiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.sots.LogisticsPipes2;
import com.sots.item.ItemWrench;
import com.sots.item.modules.IItemModule;
import com.sots.module.IModule;
import com.sots.routing.Network;
import com.sots.routing.interfaces.IDestination;
import com.sots.routing.interfaces.IPipe;
import com.sots.routing.interfaces.IRoutable;
import com.sots.util.Connections;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TileRoutedPipe extends TileGenericPipe implements IRoutable, IPipe, IDestination{
	
	private static final int MAX_MODS = 1;
	private volatile Set<IModule> mods = new HashSet<IModule>();
	
	@Override
    public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
			for(int i = 0; i<MAX_MODS; i++ ){
				mods.add(IModule.getFromType(compound.getInteger("module_" + i)));
			}
		}
	
	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if(!mods.isEmpty()) {
			int modcount = 0;
	        for(IModule mod : mods) {
	        	compound.setInteger("module_" + modcount, mod.modType().ordinal());
	        	modcount+=1;
	        }
		}
		
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
					network.registerDestination(this.nodeID);
					break;
				}
					
			}
		}
	}
	
	@Override
	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if(heldItem.getItem()!=null) {
			if(heldItem.getItem() instanceof ItemSign) {
				if(hasNetwork) {
					network.getAllRoutesFrom(nodeID);
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
			boolean remote = world.isRemote;
			int count = mods.size(); //debug
			if(count < MAX_MODS){
				IModule mod = ((IItemModule) heldItem.getItem()).getModLogic();
				if(mod.canInsert()){
					mods.add(mod);
					heldItem.shrink(1);
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
					network.registerDestination(this.nodeID);
					break;
				}
			}
		} else if (this.hasNetwork && (network.getNodeByID(this.nodeID).isDestination())) {
			boolean hasInv = false;
			for (int i = 0; i < 6; i++) {
				if (hasInventoryOnSide(i))
					hasInv = true;
			}
			if (!hasInv)
				network.unregisterDestination(this.nodeID);
		}
	}

}
