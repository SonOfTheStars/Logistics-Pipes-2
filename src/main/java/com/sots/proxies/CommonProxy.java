package com.sots.proxies;

import java.io.File;

import com.sots.particle.ParticleRenderer;
import com.sots.util.Config;
import com.sots.util.registries.BlockRegistry;
import com.sots.util.registries.ItemRegistry;
import com.sots.util.registries.PipeRegistry;
import com.sots.util.registries.TileRegistry;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	
	public static Configuration config;
	
	public void preInit(FMLPreInitializationEvent event){
		//Read/make the Config file on Startup
		File directory = event.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "LogisticsPipes2.cfg"));
		Config.readConfig();
		
		//Load Blocks
		BlockRegistry.init();
		
		//Load Pipes
		PipeRegistry.init();
		
		//Load Items
		ItemRegistry.init();
		
		TileRegistry.init();
	}
	
	public void init(FMLInitializationEvent event){
		
	}
	
	public void postInit(FMLPostInitializationEvent event){
		if(config != null && config.hasChanged()){
			config.save();
		}
	}

	public ParticleRenderer getParticleRenderer(){
		return null;
	}
}
