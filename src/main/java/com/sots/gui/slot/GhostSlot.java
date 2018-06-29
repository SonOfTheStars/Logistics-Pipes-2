package com.sots.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
/**
 * A Ghost slot Implementation. Copies an ItemStack when clicked upon but does not decrement the Stack.
 *
 * @author SonOfTheStars
 */
public class GhostSlot extends Slot{
	public int slotIndex = 0;

	public GhostSlot(IInventory inventory, int index, int x, int y) {

		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {

		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return true;
	}

	@Override
	public void putStack(ItemStack stack) {

		if (!isItemValid(stack)) {
			return;
		}
		if (!stack.isEmpty()) {
			stack.setCount(1);
		}
		inventory.setInventorySlotContents(this.slotIndex, stack);
		onSlotChanged();
	}
}
