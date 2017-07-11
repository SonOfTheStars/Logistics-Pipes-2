package com.sots.module;

import java.util.UUID;

import com.sots.tiles.TileGenericPipe;

public class ModuleExtract extends ModuleBase implements IModule{

	public ModuleExtract(UUID ID) {
		super(ID);
	}

	@Override
	public boolean execute(TileGenericPipe te) {
		return false;
	}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public boolean canInsert() {
		return true;
	}

	@Override
	public ModuleType modType() {
		return ModuleType.EXTRACT;
	}

}
