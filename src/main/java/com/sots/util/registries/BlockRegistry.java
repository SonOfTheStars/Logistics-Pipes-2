package com.sots.util.registries;

import java.util.ArrayList;
import java.util.List;

import com.sots.block.BlockOreRutile;
import com.sots.block.LPBlockBase;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRegistry {
	
	//All blocks to be registered
	public static List<LPBlockBase> registry = new ArrayList<LPBlockBase>();
	
	/**
	 * Registers all registered Blocks on Startup.
	 * Call this in preInit on the Common Proxy
	 */
	public static void init(){
		//init Blocks
		registry.add(new BlockOreRutile());
		
		for(LPBlockBase block : registry) {
			//Register Block
			GameRegistry.register(block);
			//Register Item of Block
			GameRegistry.register(new ItemBlock(block), block.getRegistryName());
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels(){
		for(LPBlockBase block : registry) {
			block.initModel();
		}
	}
}
