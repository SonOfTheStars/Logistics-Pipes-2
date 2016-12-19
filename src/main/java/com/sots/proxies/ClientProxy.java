package com.sots.proxies;

import com.sots.util.References;
import com.sots.util.registries.BlockRegistry;
import com.sots.util.registries.ItemRegistry;

import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy{
	@Override
	public void preInit(FMLPreInitializationEvent event){
		super.preInit(event);
		
		OBJLoader.INSTANCE.addDomain(References.MODID);
		BlockRegistry.initModels();
		ItemRegistry.initModels();
	}
	
	@Override
	public void init(FMLInitializationEvent event){
		super.init(event);
		
		
	}
}
