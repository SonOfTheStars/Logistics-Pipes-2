package com.sots.item.modules;

import java.util.UUID;

import com.sots.item.LPItemBase;
import com.sots.module.IModule;
import com.sots.module.ModuleBase;
import com.sots.util.References;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModuleBase extends LPItemBase implements IItemModule{
	
	public ItemModuleBase() {
		setRegistryName(References.RN_FPGA);
		setUnlocalizedName(References.NAME_FPGA);
		setCreativeTab(CreativeTabs.MATERIALS);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public IModule getModLogic(){
		return new ModuleBase(UUID.randomUUID());
	}
}
