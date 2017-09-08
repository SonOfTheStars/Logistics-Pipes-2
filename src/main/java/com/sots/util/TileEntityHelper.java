package com.sots.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileEntityHelper {

    public static IItemHandler getInventoryFromTile(TileEntity tileEntity, EnumFacing facing) {
        if (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)) {
            return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
        }
        if (tileEntity instanceof ISidedInventory) {
            return new SidedInvWrapper((ISidedInventory) tileEntity, facing);
        }
        if (tileEntity instanceof IInventory) {
            return new InvWrapper((IInventory) tileEntity);
        }
        return null;
    }
}
