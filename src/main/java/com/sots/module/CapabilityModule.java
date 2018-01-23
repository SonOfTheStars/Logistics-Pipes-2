package com.sots.module;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityModule {

    // DO NOT USE THE DEFAULT IMPL
    @CapabilityInject(IModule.class)
    public static Capability<IModule> CAPABILITY_MODULE = null;

    public static void register(){
        CapabilityManager.INSTANCE.register(IModule.class, new Capability.IStorage<IModule>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IModule> capability, IModule instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IModule> capability, IModule instance, EnumFacing side, NBTBase nbt) {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, DummyModule::new);
    }
}
