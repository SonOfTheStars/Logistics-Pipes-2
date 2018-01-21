package com.sots.item.modules;

import java.util.UUID;

import com.sots.item.LPItemBase;
import com.sots.module.IModule;
import com.sots.module.ModuleSink;
import com.sots.util.References;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModuleSink extends LPItemBase implements IItemModule{
	public ItemModuleSink() {
		setRegistryName(References.RN_MODULE_SINK);
		setUnlocalizedName(References.NAME_MODULE_SINK);
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	public IModule getModLogic() {
		return new ModuleSink();
	}
}
