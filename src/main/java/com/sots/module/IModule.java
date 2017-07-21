package com.sots.module;

import java.util.UUID;

import com.sots.tiles.TileGenericPipe;

public interface IModule {
	public static enum ModuleType{
		SINK, SORT, CRAFT, EXTRACT, NONE
	}
	
	/**
	 * Executes the Modules Logic
	 * @param te The Pipe executing this Module
	 * @return True if the Logic could be executed. If a module returns false, Items used in its operation will be spilled!
	 */
	public boolean execute(TileGenericPipe te);
	
	/**
	 * @return True if the Module can execute its Logic, False if not
	 */
	public boolean canExecute();
	
	/**
	 * @return True if the Module can be right-clicked into a Pipe, False if not
	 */
	public boolean canInsert();
	
	/**
	 * @return The Modules execution Type
	 */
	public ModuleType modType();
	
	public static IModule getFromType(int t) {
		IModule mod = null;
		switch(t) {
		case 0: 
			mod= new ModuleSink(UUID.randomUUID());
		case 3:
			mod = new ModuleExtract(UUID.randomUUID());
		default:
			mod = new ModuleBase(UUID.randomUUID());
		}
		
		return mod;
	}
}
