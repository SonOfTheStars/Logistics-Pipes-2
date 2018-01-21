package com.sots.module;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModuleCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {
    private final IModule module;

    public ModuleCapabilityProvider(IModule module) {
        if (module == null){
            throw new IllegalArgumentException("the module can not be null");
        }
        this.module = module;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityModule.CAPABILITY_MODULE;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityModule.CAPABILITY_MODULE ? (T) module : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return module.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        module.deserializeNBT(nbt);
    }
}
