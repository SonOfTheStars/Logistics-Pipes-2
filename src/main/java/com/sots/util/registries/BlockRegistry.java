package com.sots.util.registries;

import com.sots.block.BlockOreRutile;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRegistry {
	
	//All blocks to be registered
	public static BlockOreRutile oreRutile;
	
	/**
	 * Registers all registered Blocks on Startup.
	 * Call this in preInit on the Common Proxy
	 */
	public static void init(){
		//init Blocks
		oreRutile = new BlockOreRutile();
		
		//Register Blocks
		GameRegistry.register(oreRutile);
		
		
		//Register Block Items
		GameRegistry.register(new ItemBlock(oreRutile), oreRutile.getRegistryName());
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels(){
		oreRutile.initModel();
	}
}
