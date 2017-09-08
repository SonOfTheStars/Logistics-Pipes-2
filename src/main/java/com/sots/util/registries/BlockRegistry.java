package com.sots.util.registries;

import java.util.ArrayList;
import java.util.List;

import com.sots.block.BlockOreRutile;
import com.sots.block.LPBlockBase;
import com.sots.util.Config;
import com.sots.util.References;
import com.sots.util.StringUtil;
import com.sots.world.LPWorldGen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class BlockRegistry {
	
	//All blocks to be registered
	public static List<LPBlockBase> registry = new ArrayList<LPBlockBase>();
	
	/**
	 * Registers all registered Blocks on Startup.
	 * Call this in preInit on the Common Proxy
	 */
	public static void init() {
		//init Blocks
		registry.add(new BlockOreRutile());
		
		for(LPBlockBase block : registry) {

			//Register Block
			ForgeRegistries.BLOCKS.register(block);
			//Register Item of Block
			ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
			//Register Ores
			if(!StringUtil.isNullOrWhitespace(block.getOreName()) && !StringUtil.isNullOrEmpty(block.getOreName()))
				OreDictionary.registerOre(block.getOreName(), block);
			
			if(block instanceof BlockOreRutile) {
				addConfiguredWorldgen(block.getDefaultState(), References.RN_ORE_RUTILE, Config.oreRutile);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		for(LPBlockBase block : registry) {
			block.initModel();
		}
	}
	
	public static void addConfiguredWorldgen(IBlockState state, String name, int[] config) {
		if(config!=null && config.length>=5 && config[0]>0) {
			LPWorldGen.addOreGen(name, state, config[0], config[1], config[2], config[3], config[4]);
		}
	}
}
