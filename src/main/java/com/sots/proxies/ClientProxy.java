package com.sots.proxies;

import com.sots.util.BlockRegistry;
import com.sots.util.References;

import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy{
	@Override
	public void preInit(FMLPreInitializationEvent event){
		super.preInit(event);
		
		OBJLoader.INSTANCE.addDomain(References.MODID);
		BlockRegistry.initModels();
	}
	
	@Override
	public void init(FMLInitializationEvent event){
		super.init(event);
		
		
	}
}
