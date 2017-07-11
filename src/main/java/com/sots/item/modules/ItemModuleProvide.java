package com.sots.item.modules;

import com.sots.item.LPItemBase;
import com.sots.module.IModule;
import com.sots.util.References;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModuleProvide extends LPItemBase implements IItemModule{
	public ItemModuleProvide() {
		setRegistryName(References.RN_MODULE_PROVIDER);
		setUnlocalizedName(References.NAME_MODULE_PROVIDER);
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public IModule getModLogic() {
		// TODO Auto-generated method stub
		return null;
	}
}
