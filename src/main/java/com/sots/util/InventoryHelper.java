package com.sots.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sots.util.helpers.ItemHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class InventoryHelper {
	
	public static List<Item> getItemTypesInInventory(TileEntity tile, EnumFacing side){
		ArrayList<Item> result = new ArrayList<Item>();
		
		if (!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			return result;
		}
		IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
		for(int i=0; i < itemHandler.getSlots(); i++) {
			ItemStack slotStack = itemHandler.getStackInSlot(i);
			if(ItemHelper.isEmptyOrZero(slotStack)) {
				continue;
			}
			Item item = slotStack.getItem();
			if(!result.contains(item)) {
				result.add(item);
			}
		}
		return result;
	}
	
	public static Optional<ItemStack> extractStack(TileEntity tile, EnumFacing side, Item type, int amount) {
		if (!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			return Optional.empty();
		}
		IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
		for(int i=0; i < itemHandler.getSlots(); i++) {
			ItemStack slotStack = itemHandler.getStackInSlot(i);
			if(ItemHelper.isEmptyOrZero(slotStack)) {
				continue;
			}
			if(ItemHelper.itemsEqual(type, slotStack.getItem())) {
				ItemStack out = itemHandler.extractItem(i, amount, false);
				while(out.getCount()<amount && i<itemHandler.getSlots()) {
					for(i++; i<itemHandler.getSlots(); i++) {
						slotStack = itemHandler.getStackInSlot(i);
						if(ItemHelper.itemsEqualWithMetadata(out, slotStack, true)) {
							ItemStack overspill = itemHandler.extractItem(i, amount-out.getCount(), false);
							if(!ItemHelper.isEmptyOrZero(overspill)) {
								out.grow(overspill.getCount());
							}
						}
					}
				}
				return Optional.of(out);
			}
		}
		
		return Optional.empty();
	}
}
