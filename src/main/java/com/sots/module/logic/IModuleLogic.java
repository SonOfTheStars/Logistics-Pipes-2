package com.sots.module.logic;

import com.sots.tiles.TileRoutedPipe;

public interface IModuleLogic {
	
	public abstract void execute(TileRoutedPipe te);
	
	public abstract ModuleType getType();
	
	public void onInvalidate(TileRoutedPipe te);
}


