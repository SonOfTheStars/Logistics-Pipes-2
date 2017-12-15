package com.sots.util.registries;

import java.util.ArrayList;
import java.util.List;

import com.sots.pipe.*;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PipeRegistry {
	
	public static List<BlockGenericPipe> registry = new ArrayList<BlockGenericPipe>();
	
	public static BlockGenericPipe network_core;
	public static BlockGenericPipe pipe_basic;
	public static BlockGenericPipe pipe_routed;
	public static BlockGenericPipe pipe_blocking;
	
	public static void init() {
		network_core = new NetworkCore();
		pipe_basic = new PipeBasic();
		pipe_routed = new PipeRouted();
		pipe_blocking = new PipeBlocking();
		
		
		registry.add(network_core);
		registry.add(pipe_basic);
		registry.add(pipe_routed);
		registry.add(pipe_blocking);
		
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
