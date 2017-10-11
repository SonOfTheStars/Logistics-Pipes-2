package com.sots.network;

import com.sots.network.message.MessagePipeContentUpdate;
import com.sots.util.References;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class LPPacketHandler {
	public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(References.MODID);
	
	private static int id = 0;
	
	public static void registerMessages() {
		INSTANCE.registerMessage(MessagePipeContentUpdate.MessageHolder.class, MessagePipeContentUpdate.class, id++, Side.CLIENT);
	}

}
