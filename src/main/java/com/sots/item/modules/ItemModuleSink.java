package com.sots.item.modules;

import com.sots.module.logic.IModuleLogic;
import com.sots.module.logic.SinkLogic;
import com.sots.util.References;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModuleSink extends ItemModuleBase{
	public ItemModuleSink() {
		setRegistryName(References.RN_MODULE_SINK);
		setUnlocalizedName(References.NAME_MODULE_SINK);
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	public IModuleLogic getModLogic() {
		return new SinkLogic();
	}

	@Override
	public boolean canInsert() {
		return true;
	}
	
	
}
