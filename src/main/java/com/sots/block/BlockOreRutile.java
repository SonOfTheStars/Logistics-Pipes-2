package com.sots.block;

import com.sots.util.References;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOreRutile extends LPBlockBase{
	public BlockOreRutile(){
		super(Material.ROCK);
		setUnlocalizedName(References.NAME_ORE_RUTILE);
		setRegistryName(References.RN_ORE_RUTILE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
