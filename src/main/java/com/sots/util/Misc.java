package com.sots.util;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class Misc {
	public static Random rand = new Random();
	public static Vec3i vec3i = new Vec3i(0.5, 1.5, 0.5);
	
	public static void spawnInventoryInWorld(World world, double x, double y, double z, IItemHandler inventory){
		if (inventory != null && !world.isRemote){
			for (int i = 0; i < inventory.getSlots(); i ++){
				if (inventory.getStackInSlot(i) != ItemStack.EMPTY){
					world.spawnEntity(new EntityItem(world,x,y,z,inventory.getStackInSlot(i)));
				}
			}
		}
	}

	public static void spawnItemStackInWorld(World world, BlockPos pos, @Nonnull ItemStack stack){
		if (!world.isRemote){
			if (!stack.isEmpty()){
				world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack));
			}
		}
	}
}
