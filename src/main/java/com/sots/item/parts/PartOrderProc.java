package com.sots.item.parts;

import com.sots.item.LPItemBase;
import com.sots.util.References;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PartOrderProc extends LPItemBase{
	public PartOrderProc() {
		setRegistryName(References.RN_ORDER_PROC);
		setUnlocalizedName(References.NAME_ORDER_PROC);
		setCreativeTab(CreativeTabs.REDSTONE);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
