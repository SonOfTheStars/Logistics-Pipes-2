package com.sots.tiles.tesr;

import java.util.Set;

import com.sots.routing.LPRoutedItem;
import com.sots.tiles.TileGenericPipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

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
		if(!displays.isEmpty()) {
			for(LPRoutedItem item : displays) {
				ItemStack stack = item.getContent();
				if (!stack.isEmpty()) {
					RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
					GlStateManager.enableRescaleNormal();
					GlStateManager.alphaFunc(516, 0.1F);
					GlStateManager.enableBlend();
					RenderHelper.enableStandardItemLighting();
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					GlStateManager.pushMatrix();
					Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stack, te.getWorld(), null);
					calculateTranslation(item, partialTicks);
					GlStateManager.rotate((((float) getWorld().getTotalWorldTime() + partialTicks) / 40F) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
					GlStateManager.scale(.5f, .5f, .5f);
					itemRenderer.renderItem(stack, ibakedmodel);
					GlStateManager.disableRescaleNormal();
					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
				}
				//System.out.println("Rendering item: " + item.getContent());
			}
		}
//		float scale = .625f;
//		GlStateManager.scale(scale, scale, scale);
//		Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(Items.APPLE, 1), ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}

	public void calculateTranslation(LPRoutedItem item, float partialTicks) {
		double itemTicks = item.ticks;
		double newX = (item.getHeading().getDirectionVec().getX() * (itemTicks / item.TICK_MAX - 0.5));
		double newY = (item.getHeading().getDirectionVec().getY() * (itemTicks / item.TICK_MAX - 0.5));
		double newZ = (item.getHeading().getDirectionVec().getZ() * (itemTicks / item.TICK_MAX - 0.5));

		//System.out.println("Translating: " + newX + ", " + newY + ", " + newZ + " : PartialTicks=" + partialTicks);
		GlStateManager.translate(newX, newY, newZ);
	}
}
