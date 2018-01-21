package com.sots.item.modules;

import java.util.UUID;

import com.sots.item.LPItemBase;
import com.sots.module.IModule;
import com.sots.module.ModuleExtract;
import com.sots.util.References;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModuleExtract extends ItemModuleBase{

	public ItemModuleExtract() {
		setRegistryName(References.RN_MODULE_EXTRACTOR);
		setUnlocalizedName(References.NAME_MODULE_EXTRACTOR);
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	protected IModule getModuleLogic() {
		return new ModuleExtract();
	}
}
