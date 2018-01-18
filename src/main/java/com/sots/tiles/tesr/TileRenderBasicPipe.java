package com.sots.tiles.tesr;

import java.util.Set;

import com.sots.routing.*;
import com.sots.tiles.TileGenericPipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.UniversalBucket;

public class TileRenderBasicPipe extends TileEntitySpecialRenderer<TileGenericPipe> {

	@Override
	public void renderTileEntityAt(TileGenericPipe te, double x, double y, double z, float partialTicks,
			int destroyStage) {
		if(!te.getWorld().isBlockLoaded(te.getPos(), false))
			return;
		Set<LPRoutedObject> displays = te.getContents();
		//Set<LPRoutedFluid> displays_fluid = te.getContents_fluid();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x+.5, y+.5, z+.5);
		if(!displays.isEmpty()) {
			for(LPRoutedObject item : displays) {
				item.render(te, partialTicks);
			//for(LPRoutedItem item : displays) {
				//ItemStack stack = item.getContent();
				//if (!stack.isEmpty()) {
					//RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
					//GlStateManager.enableRescaleNormal();
					//GlStateManager.alphaFunc(516, 0.1F);
					//GlStateManager.enableBlend();
					//RenderHelper.enableStandardItemLighting();
					//GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					//GlStateManager.pushMatrix();
					//Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					//IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stack, te.getWorld(), null);
					//calculateTranslation(item, partialTicks);
					//if(stack.getItem() instanceof ItemBlock) {
						//GlStateManager.scale(.3f, .3f, .3f);
					//} else {
						//GlStateManager.rotate((((float) getWorld().getTotalWorldTime() + partialTicks) / 40F) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
						//GlStateManager.scale(.5f, .5f, .5f);
					//}
					//itemRenderer.renderItem(stack, ibakedmodel);
					//GlStateManager.disableRescaleNormal();
					//GlStateManager.disableBlend();
					//GlStateManager.popMatrix();
				//}
			}
		}
		//if(!displays_fluid.isEmpty()) {
			//for(LPRoutedFluid fluid : displays_fluid) {
				//ItemStack stack = UniversalBucket.getFilledBucket(new UniversalBucket(), fluid.getContent().getFluid());
				//if (!stack.isEmpty()) {
					//RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
					//GlStateManager.enableRescaleNormal();
					//GlStateManager.alphaFunc(516, 0.1F);
					//GlStateManager.enableBlend();
					//RenderHelper.enableStandardItemLighting();
					//GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					//GlStateManager.pushMatrix();
					//Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					//IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stack, te.getWorld(), null);
					//calculateTranslation(fluid, partialTicks);
					//if(stack.getItem() instanceof ItemBlock) {
						//GlStateManager.scale(.3f, .3f, .3f);
					//} else {
						//GlStateManager.rotate((((float) getWorld().getTotalWorldTime() + partialTicks) / 40F) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
						//GlStateManager.scale(.5f, .5f, .5f);
					//}
					//itemRenderer.renderItem(stack, ibakedmodel);
					//GlStateManager.disableRescaleNormal();
					//GlStateManager.disableBlend();
					//GlStateManager.popMatrix();
				//}
			//}
		//}
		GlStateManager.popMatrix();
	}

	//public void calculateTranslation(LPRoutedItem item, float partialTicks) {
		////double itemTicks = item.ticks;
		//float itemTicks = item.ticks + partialTicks;
		//double newX = (item.getHeading().getDirectionVec().getX() * (itemTicks / item.TICK_MAX - 0.5));
		//double newY = (item.getHeading().getDirectionVec().getY() * (itemTicks / item.TICK_MAX - 0.5));
		//double newZ = (item.getHeading().getDirectionVec().getZ() * (itemTicks / item.TICK_MAX - 0.5));

		////System.out.println("Translating: " + newX + ", " + newY + ", " + newZ + " : PartialTicks=" + partialTicks);
		//GlStateManager.translate(newX, newY, newZ);
	//}

	//public void calculateTranslation(LPRoutedFluid fluid, float partialTicks) {
		////double itemTicks = item.ticks;
		//float fluidTicks = fluid.ticks + partialTicks;
		//double newX = (fluid.getHeading().getDirectionVec().getX() * (fluidTicks / fluid.TICK_MAX - 0.5));
		//double newY = (fluid.getHeading().getDirectionVec().getY() * (fluidTicks / fluid.TICK_MAX - 0.5));
		//double newZ = (fluid.getHeading().getDirectionVec().getZ() * (fluidTicks / fluid.TICK_MAX - 0.5));

		////System.out.println("Translating: " + newX + ", " + newY + ", " + newZ + " : PartialTicks=" + partialTicks);
		//GlStateManager.translate(newX, newY, newZ);
	//}
}
