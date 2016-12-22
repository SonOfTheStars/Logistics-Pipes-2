package com.sots.util.registries;

import java.util.ArrayList;
import java.util.List;

import com.sots.pipe.BlockGenericPipe;
import com.sots.pipe.PipeBasic;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PipeRegistry {
	
	public static List<BlockGenericPipe> registry = new ArrayList<BlockGenericPipe>();
	
	public static BlockGenericPipe pipe_basic;
	
	public static void init() {
		pipe_basic = new PipeBasic();
		
		
		registry.add(pipe_basic);
		
		for(BlockGenericPipe pipe : registry) {
			GameRegistry.register(pipe);
			
			GameRegistry.register(new ItemBlock(pipe), pipe.getRegistryName());
		}
	}
	
	public static void initModels() {
		for(BlockGenericPipe pipe : registry) {
			pipe.initModel();
		}
	}
}
