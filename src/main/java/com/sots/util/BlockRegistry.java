package com.sots.util;

import com.sots.block.BlockOreRutile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRegistry {
	
	//All blocks to be registered
	public static BlockOreRutile oreRutile;
	
	/**
	 * Registers all Blocks on Startup
	 */
	public static void init(){
		//init Blocks
		oreRutile = new BlockOreRutile();
		
		//Register Blocks
		
		
		//Register Block Items
		
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels(){
		oreRutile.initModel();
	}
}
