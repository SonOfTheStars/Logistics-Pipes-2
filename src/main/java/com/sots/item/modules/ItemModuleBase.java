package com.sots.item.modules;

import com.sots.GuiHandler;
import com.sots.LogisticsPipes2;
import com.sots.item.LPItemBase;
import com.sots.module.CapabilityModule;
import com.sots.module.IModule;
import com.sots.module.ModuleCapabilityProvider;
import com.sots.util.References;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class ItemModuleBase extends LPItemBase{
	
	public ItemModuleBase() {
		//setRegistryName(References.RN_FPGA);
		setUnlocalizedName(References.NAME_FPGA);
		setCreativeTab(CreativeTabs.MATERIALS);
		setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (!worldIn.isRemote){
			IModule module = playerIn.getHeldItem(handIn).getCapability(CapabilityModule.CAPABILITY_MODULE, null);
			if (module.hasGui()){
				playerIn.openGui(LogisticsPipes2.instance, GuiHandler.guiIDs.MODULEGUI.ordinal(), worldIn, handIn.ordinal(), 0, 0);
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new ModuleCapabilityProvider(getModuleLogic());
	}

	protected abstract IModule getModuleLogic();
}
