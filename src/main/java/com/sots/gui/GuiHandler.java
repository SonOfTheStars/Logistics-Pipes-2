package com.sots.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{
	
	private static final int TILE_PIPE_ID=0;
	private static final int ITEM_MODULE=1;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

		switch (id) {
			case TILE_PIPE_ID:
				if (tile instanceof TileGenericPipe) {
					return ((TileGenericPipe) tile).getGuiClient(player.inventory);
				}
				return null;
		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

		switch (id) {
			case TILE_ID:
				if (tile instanceof TileCore) {
					return ((TileCore) tile).getGuiServer(player.inventory);
				}
				return null;
			case TILE_CONFIG_ID:
				if (tile instanceof TileCore) {
					return ((TileCore) tile).getConfigGuiServer(player.inventory);
				}
				return null;
			default:
				if (id >= TILE_ATTACHMENT_ID && id <= TILE_ATTACHMENT_ID + 5) {
					if (tile instanceof TileGrid) {
						Attachment attachment = ((TileGrid) tile).getAttachment(id - TILE_ATTACHMENT_ID);
						if (attachment != null) {
							return attachment.getGuiServer(player.inventory);
						}
					}
				}
				return null;
		}
	}*/

}
