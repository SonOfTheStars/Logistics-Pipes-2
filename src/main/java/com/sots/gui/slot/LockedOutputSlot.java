package com.sots.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

/**
 * A crafting output slot that can not be withdrawn from
 * @author SonOfTheStars
 *
 */

public class LockedOutputSlot extends SlotCrafting{
	
	public LockedOutputSlot(EntityPlayer player, InventoryCrafting craftMatrix, IInventory inventory, int index, int x, int y) {

		super(player, craftMatrix, inventory, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {

		return false;
	}
}
