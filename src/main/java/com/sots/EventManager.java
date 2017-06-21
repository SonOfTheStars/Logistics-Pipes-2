package com.sots;

import com.sots.proxies.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventManager {
	public static float tickCounter = 0;
	public static int ticks = 0;
	
	static EntityPlayer clientPlayer = null;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent event) {
		ResourceLocation particleGlint = new ResourceLocation("lptwo:entity/particle_glint");
		event.getMap().registerSprite(particleGlint);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTick(TickEvent.ClientTickEvent event) {
		if(event.side == Side.CLIENT) {
			ticks++;
			ClientProxy.particleRender.updateParticles();
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderAfterWorld(RenderWorldLastEvent event){
		tickCounter++;
		if(LogisticsPipes2.proxy instanceof ClientProxy) {
			GlStateManager.pushMatrix();
			ClientProxy.particleRender.renderParticles(Minecraft.getMinecraft().thePlayer, event.getPartialTicks());
			GlStateManager.popMatrix();
		}
	}
}
