package com.sots;

import org.apache.logging.log4j.Logger;

import com.sots.proxies.CommonProxy;
import com.sots.util.References;
import com.sots.world.LPWorldGen;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = References.MODID, name = References.MODNAME, version = References.VERSION, dependencies = "required-after:Forge@[11.16.0.1865,)", useMetadata = true)
public class LogisticsPipes2 {
	
	@SidedProxy(clientSide = "com.sots.proxies.ClientProxy", serverSide = "com.sots.proxies.ServerProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance
	public static LogisticsPipes2 instance = new LogisticsPipes2();
	
	public static Logger logger;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		logger = event.getModLog();
		proxy.preInit(event);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
		LPWorldGen worldGen = new LPWorldGen();
		GameRegistry.registerWorldGenerator(worldGen, 0);
		MinecraftForge.EVENT_BUS.register(worldGen);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);
	}
}
