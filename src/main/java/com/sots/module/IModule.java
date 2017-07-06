package com.sots.module;

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
}
