package com.sots.module;

import java.util.UUID;

import com.sots.tiles.TileGenericPipe;
import com.sots.tiles.TileRoutedPipe;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IModule extends ICapabilitySerializable<NBTTagCompound> {

	enum ModuleType{
		SINK, SORT, CRAFT, EXTRACT, NONE
	}
	
	/**
	 * Executes the Modules Logic
	 * @param te The Pipe executing this Module
	 * @return True if the Logic could be executed. If a module returns false, Items used in its operation will be spilled!
	 */
	boolean execute(TileRoutedPipe te);
	
	/**
	 * @return True if the Module can execute its Logic, False if not
	 */
	boolean canExecute();
	
	/**
	 * @return True if the Module can be right-clicked into a Pipe, False if not
	 */
	boolean canInsert();

	/**
	 * @return The Modules execution Type
	 */
	ModuleType modType();

	default boolean hasGui() {
		return false;
	}

	default GuiContainer getGui(EntityPlayer player) {
		return null;
	}

	default Container getContainer(EntityPlayer player) {
		return null;
	}

	@Override
	default boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return false;
	}

	@Nullable
	@Override
	default <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return null;
	}

	@Override
	default NBTTagCompound serializeNBT() {
		return writeToNBT(new NBTTagCompound());
	}


	default NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return compound;
	}

	@Override
	default void deserializeNBT(NBTTagCompound compound) {
	}

	default boolean conflictwithOther(IModule other) {
		return false;
	}

	void setUUID(UUID uuid);

	@Nonnull
	UUID getUUID();

	abstract void connect(TileRoutedPipe te);

	abstract void disconnect(TileRoutedPipe te);

	abstract void onRemoved(TileRoutedPipe te);

	abstract void onAdd(TileRoutedPipe te);
	
}
