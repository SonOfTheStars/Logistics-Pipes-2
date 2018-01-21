package com.sots.routing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.UUID;

import com.sots.tiles.TileGenericPipe;
import com.sots.util.data.Triple;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.*;

public class LPRoutedFluid extends LPRoutedObject<FluidStack>{
	
	public LPRoutedFluid(FluidStack content, EnumFacing initVector, TileGenericPipe holder, Deque<EnumFacing> routingInfo, TileGenericPipe destination) {
		super(content, initVector, holder, routingInfo, destination, FluidStack.class);
	}

	public LPRoutedFluid(int ticks, UUID ID) {
		super(ticks, ID, FluidStack.class);
	}

	@Override
	protected FluidStack copyContent(FluidStack content) {
		return content.copy();
	}

	@Override
	public void writeContentToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", getContent().writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readContentFromNBT(NBTTagCompound compound) {
		setContent(FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("inventory")));
	}

	@Override
	public void render(TileGenericPipe te, float partialTicks) {
		ItemStack stack = UniversalBucket.getFilledBucket(new UniversalBucket(), getContent().getFluid());
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
			Triple<Double, Double, Double> newCoords = calculateTranslation(partialTicks);
			GlStateManager.translate(newCoords.getFirst(), newCoords.getSecnd(), newCoords.getThird());
			GlStateManager.rotate((((float) te.getWorld().getTotalWorldTime() + partialTicks) / 40F) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(.5f, .5f, .5f);
			itemRenderer.renderItem(stack, ibakedmodel);
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void spawnInWorld(World world, double x, double y, double z) {
		world.spawnEntity(new EntityItem(world, x, y, z, UniversalBucket.getFilledBucket(new UniversalBucket(), getContent().getFluid())));
	}

	@Override
	public void putInBlock(TileEntity te) {
		if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getHeading().getOpposite())) {
			IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getHeading().getOpposite());
			FluidStack fluidStack = getContent();
			//for (int j = 0; j < fluidHandler.getSlots(); j++) {
				//fluidStack = fluidHandler.insertFluid(j, fluidStack, false);
			//}
			int amountLeft = fluidHandler.fill(fluidStack, true);
			//if(!fluidStack.isEmpty())
			if (amountLeft < fluidStack.amount)
				if (!te.getWorld().isRemote) {
					spawnInWorld(te.getWorld(), te.getPos().getX()+0.5, te.getPos().getY()+1.5, te.getPos().getZ()+0.5);
					//world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, UniversalBucket.getFilledBucket(new UniversalBucket(), fluidStack.getFluid())));
				}
		} else {
			if (!te.getWorld().isRemote) {
				spawnInWorld(te.getWorld(), te.getPos().getX()+0.5, te.getPos().getY()+1.5, te.getPos().getZ()+0.5);
			}
		}
	}

	@Override
	protected LPRoutedObject takeFromBlock(TileEntity te, EnumFacing face, FluidStack fluid, Deque<EnumFacing> route, TileGenericPipe destination, TileGenericPipe holder) {
		if (!te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite())) {
			return null;
		}
		IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite());
		FluidStack result = new FluidStack(fluid.getFluid(), 0);
		for (int i = 0; i < fluidHandler.getTankProperties().length; i++) {
			if (fluidHandler.getTankProperties()[i].getContents().isFluidEqual(fluid)) {
				FluidStack tmp = fluidHandler.drain(fluid, true);
				result = new FluidStack(fluid.getFluid(), tmp.amount + result.amount);
				fluid = new FluidStack(fluid.getFluid(), fluid.amount - tmp.amount);
			}
			if (fluid.amount <= 0) {
				break;
			}
		}
		if (result.amount == 0) {
			return null;
		}
		setContent(result);
		setHeading(face.getOpposite());
		setHolding(holder);
		setRoute(route);
		return this;
	}
}
