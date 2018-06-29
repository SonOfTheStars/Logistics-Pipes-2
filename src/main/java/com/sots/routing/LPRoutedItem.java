package com.sots.routing;

import java.util.Deque;
import java.util.Optional;
import java.util.UUID;

import com.sots.tiles.TileGenericPipe;
import com.sots.util.InventoryHelper;
import com.sots.util.data.Triple;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class LPRoutedItem extends LPRoutedObject<ItemStack>{

	public LPRoutedItem(ItemStack content, EnumFacing initVector, TileGenericPipe holder, Deque<EnumFacing> routingInfo, TileGenericPipe destination) {
		super(content, initVector, holder, routingInfo, destination, ItemStack.class);
		//setHeading(initVector);
		//setHolding(holder);
		//route = routingInfo;
		//ticks = 0;
		//this.stack = content.copy();
		//this.position=new Triple<Double, Double, Double>(x, y, z);
		//ID=UUID.randomUUID();
		//this.destination = destination;
	}

	public LPRoutedItem(int ticks, UUID ID) {
		super(ticks, ID, ItemStack.class);
		//this.ticks=ticks;
		//this.position=new Triple<Double, Double, Double>(x, y, z);
		//this.stack = content.copy();
		//this.ID = ID;
	}

	@Override
	protected ItemStack copyContent(ItemStack content) {
		return content.copy();
	}

	@Override
	public void writeContentToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", getContent().serializeNBT());
	}

	@Override
	public void readContentFromNBT(NBTTagCompound compound) {
		setContent(new ItemStack(compound.getCompoundTag("inventory")));
	}

	@Override
	public void render(TileGenericPipe te, float partialTicks) {
		if (!getContent().isEmpty()) {
			RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
			GlStateManager.enableRescaleNormal();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.pushMatrix();
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(getContent(), te.getWorld(), null);
			Triple<Double, Double, Double> newCoords = calculateTranslation(partialTicks);
			GlStateManager.translate(newCoords.getFirst(), newCoords.getSecnd(), newCoords.getThird());
			if(getContent().getItem() instanceof ItemBlock) {
				GlStateManager.scale(.3f, .3f, .3f);
			} else {
				GlStateManager.rotate((((float) te.getWorld().getTotalWorldTime() + partialTicks) / 40F) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(.5f, .5f, .5f);
			}
			itemRenderer.renderItem(getContent(), ibakedmodel);
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void spawnInWorld(World world, double x, double y, double z) {
		world.spawnEntity(new EntityItem(world, x, y, z, getContent()));
	}

	@Override
	public void putInBlock(TileEntity te) {
		if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getHeading().getOpposite())) {
			IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getHeading().getOpposite());
			ItemStack itemStack = getContent();
			for (int j = 0; j < itemHandler.getSlots(); j++) {
				itemStack = itemHandler.insertItem(j, itemStack, false);
			}
			if(!itemStack.isEmpty())
				if (!te.getWorld().isRemote) {
					spawnInWorld(te.getWorld(), te.getPos().getX()+0.5, te.getPos().getY()+1.5, te.getPos().getZ()+0.5);
					//world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, itemStack));
				}
		} else {
			if (!te.getWorld().isRemote) {
				spawnInWorld(te.getWorld(), te.getPos().getX()+0.5, te.getPos().getY()+1.5, te.getPos().getZ()+0.5);
				//world.spawnEntity(new EntityItem(world, pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, getContent()));
			}
		}
	}

	@Override
	protected LPRoutedObject takeFromBlock(TileEntity te, EnumFacing face, ItemStack stack, Deque<EnumFacing> route, TileGenericPipe destination, TileGenericPipe holder, int targetAmount) {
		Optional<ItemStack> extracted = InventoryHelper.extractStack(te, face.getOpposite(), stack.getItem(), targetAmount);
		if(extracted.isPresent()) {
			setContent(extracted.get());
			setHeading(face.getOpposite());
			setHolding(holder);
			setRoute(route);
			return this;
		}
		return null;
	}
}
