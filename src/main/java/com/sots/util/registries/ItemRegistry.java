package com.sots.util.registries;

import com.sots.item.IngotTitanium;
import com.sots.item.ShardRutile;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemRegistry {
	
	//All Items to be Registered
	public static ShardRutile shard_rutile;
	public static IngotTitanium ingot_titanium;
	
	/**
	 * Initialize all Items for preInit
	 * Call this in preInit on the Common Proxy
	 */
	public static void init(){
		//Init Items
		shard_rutile= new ShardRutile();
		ingot_titanium = new IngotTitanium();
		
		//Register Items
		GameRegistry.register(shard_rutile);
		GameRegistry.register(ingot_titanium);
	}
	
	/**
	 * Initializes Models and Textures for registered Items.
	 * Call this in Init on the Client Proxy
	 */
	public static void initModels(){
		shard_rutile.initModel();
		ingot_titanium.initModel();
	}
}
