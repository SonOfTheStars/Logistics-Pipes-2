package com.sots;

import com.sots.util.References;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = References.MODID)
public class EventManager {

    @SubscribeEvent
    public static void onRegistryRegisterBlock(RegistryEvent.Register<Item> event) {
    }

    @SubscribeEvent
    public static void onRegistryRegisterItem(RegistryEvent.Register<Block> event) {
    }
}
