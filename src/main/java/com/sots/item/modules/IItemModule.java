package com.sots.item.modules;

import com.sots.module.logic.IModuleLogic;

public interface IItemModule {
	public IModuleLogic getModLogic();
	
	public boolean canInsert();
}
