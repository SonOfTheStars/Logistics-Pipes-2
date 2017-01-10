package com.sots.util;

import java.util.ArrayList;

import com.sots.routing.interfaces.IPipe;
import com.sots.tiles.TileGenericPipe;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.items.CapabilityItemHandler;

public class ConnectionHelper {
	public static boolean canConnectTile(IBlockAccess world, BlockPos pos) {
		return world.getTileEntity(pos)!=null;
	}
	
	public static boolean isPipe(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
		TileEntity target = AccessHelper.getTileSafe(worldIn, pos, facing);
		return(target instanceof IPipe);
	}
	
	public static boolean isInventory(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
		TileEntity target = AccessHelper.getTileSafe(worldIn, pos, facing);
		if(target !=null) {
			return(target.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()));
		}
		else {
			return false;
		}
	}
	
	public static ArrayList<String> checkForPipes(IBlockAccess world, BlockPos pos) {
		ArrayList<String> hidden = new ArrayList<String>();
		//The Center Block of the Pipe allways has to be shown, thus its never added here
		
		//North Connection
		if(!isPipe(world, pos, EnumFacing.NORTH)) {
			hidden.add(Connections.NORTH.toString());
			hidden.add(Connections.G_NORTH.toString());
		}
		
		//South Connection
		if(!isPipe(world, pos, EnumFacing.SOUTH)) {
			hidden.add(Connections.SOUTH.toString());
			hidden.add(Connections.G_SOUTH.toString());
		}
		
		//East Connection
		if(!isPipe(world, pos, EnumFacing.EAST)) {
			hidden.add(Connections.EAST.toString());
			hidden.add(Connections.G_EAST.toString());
		}
		
		//West Connection
		if(!isPipe(world, pos, EnumFacing.WEST)) {
			hidden.add(Connections.WEST.toString());
			hidden.add(Connections.G_WEST.toString());
		}
		
		//Up Connection
		if(!isPipe(world, pos, EnumFacing.UP)) {
			hidden.add(Connections.UP.toString());
			hidden.add(Connections.G_UP.toString());
		}
		
		//Down Connection
		if(!isPipe(world, pos, EnumFacing.DOWN)) {
			hidden.add(Connections.DOWN.toString());
			hidden.add(Connections.G_DOWN.toString());
		}
		
		return hidden;
	}
	
	public static ArrayList<String> checkInventoriesAndPipes(IBlockAccess world, BlockPos pos){
		ArrayList<String> hidden = new ArrayList<String>();
		//The Center Block of the Pipe allways has to be shown, thus its never added here
		//North Side Check
		if(!isInventory(world, pos, EnumFacing.NORTH)) {
			hidden.add(Connections.C_NORTH.toString());
			if(!isPipe(world, pos, EnumFacing.NORTH)) 
				hidden.add(Connections.NORTH.toString());
		}
		
		//South Side Check
		if(!isInventory(world, pos, EnumFacing.SOUTH)) {
			hidden.add(Connections.C_SOUTH.toString());
			if(!isPipe(world, pos, EnumFacing.SOUTH)) 
				hidden.add(Connections.SOUTH.toString());
		}
		
		//East Side Check
		if(!isInventory(world, pos, EnumFacing.EAST)) {
			hidden.add(Connections.C_EAST.toString());
			if(!isPipe(world, pos, EnumFacing.EAST))
				hidden.add(Connections.EAST.toString());
		}
		
		//West Side Check
		if(!isInventory(world, pos, EnumFacing.WEST)) {
			hidden.add(Connections.C_WEST.toString());
			if(!isPipe(world, pos, EnumFacing.WEST)) 
				hidden.add(Connections.WEST.toString());
		}
		
		//Up Side Check
		if(!isInventory(world, pos, EnumFacing.UP)) {
			hidden.add(Connections.C_UP.toString());
			if(!isPipe(world, pos, EnumFacing.UP)) 
				hidden.add(Connections.UP.toString());
		}
		
		//Down Side Check
		if(!isInventory(world, pos, EnumFacing.DOWN)) {
			hidden.add(Connections.C_DOWN.toString());
			if(!isPipe(world, pos, EnumFacing.DOWN)) 
				hidden.add(Connections.DOWN.toString());
		}
		
		return hidden;
	}
	
	public static TileGenericPipe getAdjacentPipe(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
		if(isPipe(worldIn,pos,facing)) {
			TileGenericPipe target = (TileGenericPipe)AccessHelper.getTileSafe(worldIn, pos, facing);
			if(target!=null) {
				return target;
			}
		}
		return null;
	}
}
