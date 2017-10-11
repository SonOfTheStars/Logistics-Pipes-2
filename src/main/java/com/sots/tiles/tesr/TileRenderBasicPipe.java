package com.sots.tiles.tesr;

import java.util.Set;

import com.sots.routing.LPRoutedItem;
import com.sots.tiles.TileGenericPipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TileRenderBasicPipe extends TileEntitySpecialRenderer<TileGenericPipe>{
	float angle=0f;
	@Override
	public void renderTileEntityAt(TileGenericPipe te, double x, double y, double z, float partialTicks,
			int destroyStage) {
		if(!te.getWorld().isBlockLoaded(te.getPos(), false))
			return;
		Set<LPRoutedItem> displays = te.getContents();
		if(!displays.isEmpty()) {
			int i = 1;
			i++;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x+.5, y+.5, z+.5);
//		if(!displays.isEmpty()) {
//			for(LPRoutedItem item : displays) {
//				float scale = .625f;
//				GlStateManager.scale(scale, scale, scale);
//				Minecraft.getMinecraft().getRenderItem().renderItem(item.getContent(), ItemCameraTransforms.TransformType.FIXED);
//			}
//		}
		float scale = .625f;
		GlStateManager.scale(scale, scale, scale);
		Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(Items.APPLE, 1), ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}
}
