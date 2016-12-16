package com.sots.util;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.proxies.CommonProxy;

import net.minecraftforge.common.config.Configuration;

public class Config {
	private static final String CAT_GENERAL = "general";
	private static final String CAT_NETWORK = "network";
	private static final String CAT_WORLD = "world";
	
	public static boolean genTitanium = true;		//Wether or not to generate Rutile Ore in the World
	public static boolean allowTierTwo = true; 	//Wether or not to allow the making of Industrial grade Pipes
	public static float baseNetCost = 30.0f; 		//Cost in RF/t
	
	/**
	 * Read the config file ordered by Category
	 * @throws Exception to Log when sth goes wrong
	 */
	public static void readConfig(){
		Configuration cfg = CommonProxy.config;
		
		try{
			cfg.load();
			initGeneral(cfg);
			initNetwork(cfg);
			initWorld(cfg);
		}
		catch(Exception e){
			LogisticsPipes2.logger.log(Level.ERROR, "There was a Problem reading the Config File!");
		}
		finally{
			if(cfg.hasChanged()){
				cfg.save();
			}
		}
	}
	
	
	/**
	 * Provide Values of General Category
	 * @param cfg Config Provider
	 */
	private static void initGeneral(Configuration cfg){
		cfg.addCustomCategoryComment(CAT_GENERAL, "General settings:");
		
		allowTierTwo = cfg.getBoolean("allowT2", CAT_GENERAL, true, "Wether or not to allow the Crafting of Industrial Grade Pipes.");
	}
	
	/**
	 * Provides Values of Network Category
	 * @param cfg Config Provider
	 */
	private static void initNetwork(Configuration cfg){
		cfg.addCustomCategoryComment(CAT_NETWORK, "Network specific settings:");
		
		baseNetCost = cfg.getFloat("baseNetworkCost", CAT_NETWORK, 30.0f, 5.0f, 10000.0f, "The Base energy consumption of the Network Core");
	}
	
	/**
	 * Provides Values of World Category
	 * @param cfg Config Provider
	 */
	private static void initWorld(Configuration cfg){
		cfg.addCustomCategoryComment(CAT_WORLD, "World settings:");
		
		genTitanium = cfg.getBoolean("genTitanium", CAT_WORLD, true, "Wether or not to Worldgen Titanium. If off, its recommended to disable the Industrial Tier Pipes");
	}
}
