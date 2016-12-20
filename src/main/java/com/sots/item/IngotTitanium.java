package com.sots.item;

import com.sots.util.References;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class IngotTitanium extends LPItemBase{
	
	public IngotTitanium() {
		setRegistryName(References.RN_INGOT_TITANIUM);
		setUnlocalizedName(References.NAME_INGOT_TITANIUM);
		setCreativeTab(CreativeTabs.MATERIALS);
		setOreName("ingotTitanium");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
