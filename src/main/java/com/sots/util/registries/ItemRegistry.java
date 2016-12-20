package com.sots.util.registries;

import java.util.ArrayList;
import java.util.List;

import com.sots.item.IngotTitanium;
import com.sots.item.LPItemBase;
import com.sots.item.ShardRutile;
import com.sots.util.StringUtil;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class ItemRegistry {
	
	//All Items to be Registered
	public static List<LPItemBase> registry = new ArrayList<LPItemBase>();
	//public static ShardRutile shard_rutile;
	//public static IngotTitanium ingot_titanium;
	
	/**
	 * Initialize all Items for preInit
	 * Call this in preInit on the Common Proxy
	 */
	public static void init(){
		//Init Items
		registry.add(new ShardRutile());
		registry.add(new IngotTitanium());
		
		//Register Items
		for(LPItemBase item : registry) {
			//Register Item
			GameRegistry.register(item);
			//Register Ordedict Names
			if(!StringUtil.isNullOrWhitespace(item.getOreName()) && !StringUtil.isNullOrEmpty(item.getOreName()))
				OreDictionary.registerOre(item.getOreName(), item);
		}
		
	}
	
	/**
	 * Initializes Models and Textures for registered Items.
	 * Call this in Init on the Client Proxy
	 */
	public static void initModels(){
		for(LPItemBase item : registry) {
			item.initModel();
		}
	}
}
