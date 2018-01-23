package com.sots;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sots.network.LPPacketHandler;
import com.sots.network.message.MessagePipeContentUpdate;
import com.sots.proxies.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
	
	public static Map<BlockPos, TileEntity> toUpdate = new ConcurrentHashMap<>();
	
	public static void markTEForUpdate(BlockPos pos, TileEntity tile){
		if (!toUpdate.containsKey(pos)){
			toUpdate.put(pos, tile);
		}
		else {
			toUpdate.replace(pos, tile);
		}
	}
	
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
			ClientProxy.particleRender.renderParticles(event.getPartialTicks());
			GlStateManager.popMatrix();
		}
	}
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event){
		if (!event.world.isRemote && event.phase == TickEvent.Phase.END){
			NBTTagList list = new NBTTagList();
			TileEntity[] updateArray = toUpdate.values().toArray(new TileEntity[toUpdate.size()]);
			for (TileEntity t : updateArray) {
				list.appendTag(t.getUpdateTag());
			}
			if (!list.hasNoTags()){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("data", list);
				LPPacketHandler.INSTANCE.sendToAll(new MessagePipeContentUpdate(tag));
			}
			toUpdate.clear();
		}
	}
}
