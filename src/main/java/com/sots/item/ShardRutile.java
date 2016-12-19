package com.sots.item;

import com.sots.util.References;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ShardRutile extends Item{
	
	public ShardRutile() {
		setRegistryName(References.RN_SHARD_RUTILE);
		setUnlocalizedName(References.NAME_SHARD_RUTILE);
		setCreativeTab(CreativeTabs.MATERIALS);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
		
	
}
