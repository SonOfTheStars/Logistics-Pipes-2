package com.sots.module;

import java.util.UUID;

import com.sots.tiles.TileRoutedPipe;

public interface IModule {
	public static enum ModuleType{
		SINK, SORT, CRAFT, EXTRACT, NONE
	}
	
	/**
	 * Executes the Modules Logic
	 * @param te The Pipe executing this Module
	 * @return True if the Logic could be executed. If a module returns false, Items used in its operation will be spilled!
	 */
	public boolean execute(TileRoutedPipe te);
	
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
	
	public void disconnect();
	
	public static IModule getFromType(String t) {
		IModule mod = null;
		switch(t) {
			case "SINK":{
				mod= new ModuleSink(UUID.randomUUID());
				break;
			}
			case "EXTRACT":{
				mod = new ModuleExtract(UUID.randomUUID());
				break;
			}
			default:{
				mod = null;
				break;
			}
		}
		
		return mod;
	}
	
}
