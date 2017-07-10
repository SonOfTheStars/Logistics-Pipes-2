package com.sots.module;

import java.util.UUID;

import com.sots.tiles.TileGenericPipe;

public class ModuleBase implements IModule {
	
	protected final UUID MODULE_ID;
	
	public ModuleBase(UUID ID){
		MODULE_ID = ID;
	}

	@Override
	public boolean execute(TileGenericPipe te) {
		return false;
	}

	@Override
	public boolean canExecute() {
		return false;
	}

	@Override
	public boolean canInsert() {
		return false;
	}

	@Override
	public ModuleType modType() {
		return ModuleType.NONE;
	}

}
