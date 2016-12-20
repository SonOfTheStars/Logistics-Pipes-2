package com.sots.item;

import net.minecraft.item.Item;

public abstract class LPItemBase extends Item{
	private String oreName;
	
	public abstract void initModel();

	/**
	 * @return the oreName
	 */
	public String getOreName() {
		return oreName;
	}

	/**
	 * @param oreName the oreName to set
	 */
	public void setOreName(String oreName) {
		this.oreName = oreName;
	}
}
