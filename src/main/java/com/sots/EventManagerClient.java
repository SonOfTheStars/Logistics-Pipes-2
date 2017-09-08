package com.sots;

import com.sots.proxies.ClientProxy;

import com.sots.tiles.TileBasicPipe;
import com.sots.tiles.tesr.TileRenderBasicPipe;
import com.sots.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = References.MODID)
public class EventManagerClient {
    public static float tickCounter = 0;
    public static int ticks = 0;

    static EntityPlayer clientPlayer = null;

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent event) {
        ResourceLocation particleGlint = new ResourceLocation("lptwo:entity/particle_glint");
        event.getMap().registerSprite(particleGlint);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT) {
            ticks++;
            LogisticsPipes2.proxy.getParticleRenderer().updateParticles();
        }
    }

    @SubscribeEvent
    public static void onRenderAfterWorld(RenderWorldLastEvent event) {
        tickCounter++;
        GlStateManager.pushMatrix();
        LogisticsPipes2.proxy.getParticleRenderer().renderParticles(Minecraft.getMinecraft().player, event.getPartialTicks());
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileBasicPipe.class, new TileRenderBasicPipe());
    }
}
