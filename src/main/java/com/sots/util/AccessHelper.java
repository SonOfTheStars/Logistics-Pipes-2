package com.sots.util;

import javax.annotation.Nullable;

import com.sots.tiles.TileGenericPipe;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.Chunk;

public class AccessHelper {
	
	@Nullable
	public static TileEntity getTileSafe(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
		TileEntity target = worldIn instanceof ChunkCache ? 
				((ChunkCache)worldIn).getTileEntity(pos.offset(facing), Chunk.EnumCreateEntityType.CHECK)
				: 
				worldIn.getTileEntity(pos.offset(facing));
		return target;
	}
	
	@Nullable
	public static TileEntity getTileSafe(IBlockAccess worldIn, BlockPos pos) {
		TileEntity target = worldIn instanceof ChunkCache ? 
				((ChunkCache)worldIn).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK)
				: 
				worldIn.getTileEntity(pos);
		return target;
	}
	
	public static TileGenericPipe getPipeSafe(IBlockAccess worldIn, BlockPos pos, EnumFacing facing ) {
		TileEntity target = worldIn instanceof ChunkCache ? 
				((ChunkCache)worldIn).getTileEntity(pos.offset(facing), Chunk.EnumCreateEntityType.CHECK)
				: 
				worldIn.getTileEntity(pos.offset(facing));
		if(target instanceof TileGenericPipe) 
			return (TileGenericPipe) target;
		return null;
	}
}
