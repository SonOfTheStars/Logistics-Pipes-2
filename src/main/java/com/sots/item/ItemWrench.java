package com.sots.item;

import com.sots.util.References;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.model.ModelLoader;

public class ItemWrench extends LPItemBase{
	
	public ItemWrench() {
		setRegistryName(References.RN_ITEM_WRENCH);
		setUnlocalizedName(References.NAME_ITEM_WRENCH);
		setCreativeTab(CreativeTabs.TOOLS);
	}

	@Override
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

}
