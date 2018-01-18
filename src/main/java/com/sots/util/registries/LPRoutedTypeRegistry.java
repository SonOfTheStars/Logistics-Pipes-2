package com.sots.util.registries;

import com.sots.routing.*;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class LPRoutedTypeRegistry {
	
	public static void init() {
		LPRoutedObject.registerTypeOfLPRoutedObject(LPRoutedItem.class, ItemStack.class);
		LPRoutedObject.registerTypeOfLPRoutedObject(LPRoutedFluid.class, FluidStack.class);
	}

}

