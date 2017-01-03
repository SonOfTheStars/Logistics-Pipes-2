package com.sots.pipe;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGenericPipe extends Block{
	
	public BlockGenericPipe(Material materialIn) {
		super(materialIn);
		setHardness(3.0f);
		setResistance(10.0f);
		setSoundType(SoundType.METAL);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
	}
}
