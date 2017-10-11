package com.sots.network.message;

import com.sots.routing.interfaces.IPipe;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessagePipeContentUpdate implements IMessage{
	public NBTTagCompound tag = new NBTTagCompound();
	
	public MessagePipeContentUpdate() {
		//
	}
	
	

	public MessagePipeContentUpdate(NBTTagCompound tag) {
		this.tag = tag;
	}



	@Override
	public void fromBytes(ByteBuf buf) {
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, tag);
	}
	
	public static class MessageHolder implements IMessageHandler<MessagePipeContentUpdate, IMessage>
	{
		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(MessagePipeContentUpdate message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(()->{
				NBTTagList tags = message.tag.getTagList("data", Constants.NBT.TAG_COMPOUND);
				for(int i =0; i <tags.tagCount(); i++) {
					NBTTagCompound tag = tags.getCompoundTagAt(i);
					TileEntity te = Minecraft.getMinecraft().player.getEntityWorld().getTileEntity(new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")));
					if(te != null && te instanceof IPipe) {
						te.readFromNBT(tag);
						te.markDirty();
					}
				}
			});
			return null;
		}
		
	}

}
