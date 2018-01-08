package com.sots.module;

import java.util.UUID;

import com.sots.tiles.TileRoutedPipe;

public class ModuleSink extends ModuleBase implements IModule{

	public ModuleSink(UUID ID) {
		super(ID);
	}
	
	@Override
	public boolean execute(TileRoutedPipe te) {
		return false;
	}
	
	@Override
	public boolean canExecute() {return true;}
	
	@Override
	public boolean canInsert() {return true;}
	
	@Override
	public ModuleType modType() {return ModuleType.SINK;}

}
