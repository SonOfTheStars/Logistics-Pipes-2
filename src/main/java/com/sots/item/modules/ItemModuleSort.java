	package com.sots.item.modules;

import com.sots.module.logic.IModuleLogic;
import com.sots.module.logic.SortLogic;
import com.sots.util.References;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModuleSort extends ItemModuleBase{
	public ItemModuleSort() {
		setRegistryName(References.RN_MODULE_SORT);
		setUnlocalizedName(References.NAME_MODULE_SORT);
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public IModuleLogic getModLogic() {
		return new SortLogic();
	}

	@Override
	public boolean canInsert() {
		return true;
	}
}
