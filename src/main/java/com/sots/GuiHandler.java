package com.sots;

import com.sots.module.CapabilityModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public static enum guiIDs{
        MODULEGUI
    }
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        if (ID == guiIDs.MODULEGUI.ordinal()){
            return player.getHeldItem(EnumHand.values()[x]).getCapability(CapabilityModule.CAPABILITY_MODULE, null).getContainer(player);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        if (ID == guiIDs.MODULEGUI.ordinal()){
            return player.getHeldItem(EnumHand.values()[x]).getCapability(CapabilityModule.CAPABILITY_MODULE, null).getGui(player);
        }
        return null;
    }
}
