package com.sots.module;

import com.sots.tiles.TileGenericPipe;
import com.sots.tiles.TileRoutedPipe;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ModuleBase implements IModule {
	
	protected UUID MODULE_ID;

	@Override
	public boolean execute(TileGenericPipe te) {
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
	public void connect(TileGenericPipe te) {}

	@Override
	public void disconnect() {}

	@Override
	public void onRemoved() {

	}

	@Override
	public void onAdd(TileGenericPipe te) {

	}
}
