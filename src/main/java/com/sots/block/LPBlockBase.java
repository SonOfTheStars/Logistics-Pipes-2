package com.sots.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class LPBlockBase extends Block{
	private String oreName;
	
	public LPBlockBase(Material material) {
		super(material);
	}

	public void initModel() {}
	
	public String getOreName() {
		return oreName;
	}

	public void setOreName(String oreName) {
		this.oreName = oreName;
	}
}
