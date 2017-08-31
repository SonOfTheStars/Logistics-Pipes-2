package com.sots.util;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class Misc {
	public static Random rand = new Random();
	
	public static void spawnInventoryInWorld(World world, double x, double y, double z, IItemHandler inventory){
		if (inventory != null && !world.isRemote){
			for (int i = 0; i < inventory.getSlots(); i ++){
				if (inventory.getStackInSlot(i) != ItemStack.EMPTY){
					world.spawnEntity(new EntityItem(world,x,y,z,inventory.getStackInSlot(i)));
				}
			}
		}
	}
}
