package com.sots.util;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.proxies.CommonProxy;

import net.minecraftforge.common.config.Configuration;

public class Config {
	private static final String CAT_GENERAL = "general";
	private static final String CAT_NETWORK = "network";
	private static final String CAT_WORLD = "world";
	
	public static boolean genTitanium = true;			//Wether or not to generate Rutile Ore in the World
	public static int[] oreRutile = new int[] {4, 8, 30, 5, 80};
	public static boolean retroGen = false;				//Wether or not to perform Retrogen
	public static boolean retrogenRemaining = false;	//Wether or not there is remianing chunks to be retrogenned
	public static boolean retroGenFlagLog = false;		//Wether or not to log thwe Flagging of chunks for Retrogen
	public static boolean allowTierTwo = true; 			//Wether or not to allow the making of Industrial grade Pipes
	public static float baseNetCost = 30.0f; 			//Cost in RF/t
	
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
		oreRutile[0] = cfg.getInt("rutileVeinSizeMax", CAT_WORLD, 4, 2, 6, "Maximum Vein size of Rutile Ore clusters.(Between 2 and 6");
		oreRutile[1] = cfg.getInt("rutileMinY", CAT_WORLD, 8, 8, 16, "Minimum Y height at wich Rutile Spawns.(Between 8 and 16)");
		oreRutile[2] = cfg.getInt("rutileMaxY", CAT_WORLD, 30, 20, 45, "Maximum Y height at wich Rutile Spawns.(Between 20 and 40");
		oreRutile[3] = cfg.getInt("rutileChunkOccurence", CAT_WORLD, 5, 1, 10, "Number of times Rutile can Spawn within a single chunk.(Between 1 and 10");
		oreRutile[4] = cfg.getInt("rutileSPawnWeight", CAT_WORLD, 80, 10, 100, "Spawn Weight of Rutile Ore.(Between 10 and 100)");
		
		retroGen = cfg.getBoolean("retroGen", CAT_WORLD, false, "Wether or not Post-Creation World Gen should be performed.");
		retrogenRemaining = cfg.getBoolean("retrogenRemianing", CAT_WORLD, false, "Wether or not there are still Chunks to be retro Generated with LP Ore. DO NOT TOUCH THIS!");
		retroGenFlagLog = cfg.getBoolean("retrogenFlagLogging", CAT_WORLD, false, "Wether or not you want LP to log wich Chunks it flags for Retrogen. This can be a bit spammy!");
	}
}
