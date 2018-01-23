package com.sots.module;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.sots.tiles.TileRoutedPipe;

import net.minecraft.nbt.NBTTagCompound;

public class ModuleBase implements IModule {
	
	protected UUID MODULE_ID;

	@Override
	public boolean execute(TileRoutedPipe te) {
		return false;
	}

	@Override
	public boolean canExecute() {
		return false;
	}

	@Override
	public boolean canInsert() {
		return false;
	}

	@Override
	public ModuleType modType() {
		return ModuleType.NONE;
	}

	@Override
	public void setUUID(UUID uuid) {
		MODULE_ID = uuid;
	}

	@Override
	@Nonnull
	public UUID getUUID() {
		return MODULE_ID;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setUniqueId("moduleID", getUUID());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		setUUID(compound.getUniqueId("moduleID"));
	}

	@Override
	public void connect(TileRoutedPipe te) {
		
	}

	@Override
	public void disconnect(TileRoutedPipe te) {
		
	}

	@Override
	public void onRemoved(TileRoutedPipe te) {
		
	}

	@Override
	public void onAdd(TileRoutedPipe te) {
		
	}
}
