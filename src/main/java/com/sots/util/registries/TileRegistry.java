package com.sots.util.registries;

import com.sots.tiles.TileBasicPipe;
import com.sots.tiles.TileNetworkCore;
import com.sots.tiles.TileRoutedPipe;
import com.sots.tiles.tesr.TileRenderBasicPipe;
import com.sots.util.References;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileRegistry {
	public static void init() {
		GameRegistry.registerTileEntity(TileBasicPipe.class, References.MODID +"_tilebaasicpipe");
		GameRegistry.registerTileEntity(TileRoutedPipe.class, References.MODID+"_tileroutedpipe");
		GameRegistry.registerTileEntity(TileNetworkCore.class, References.MODID+"_tilenetworkcore");
	}
	
	public static void bindRenders() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileBasicPipe.class, new TileRenderBasicPipe());
	}
}
