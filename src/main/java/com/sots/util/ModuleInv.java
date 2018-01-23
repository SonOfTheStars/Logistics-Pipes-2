package com.sots.util;

import com.sots.module.CapabilityModule;
import com.sots.module.IModule;
import com.sots.tiles.TileGenericPipe;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ModuleInv implements ICapabilitySerializable<NBTTagCompound> {
    private final TileGenericPipe owner;
    private final Map<UUID, IModule> modules = new HashMap<>();
    private final NonNullList<ItemStack> stacks;

    public ModuleInv(TileGenericPipe owner, int size) {
        this.owner = owner;
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public boolean hasModule(UUID uuid) {
        return modules.containsKey(uuid);
    }

    public IModule getModule(UUID uuid) {
        return modules.get(uuid);
    }

    public boolean isStackValid(@Nonnull ItemStack stack) {
        return stack.hasCapability(CapabilityModule.CAPABILITY_MODULE, null);
    }

    public void connect() {
        modules.values().forEach(iModule -> iModule.connect(owner));
    }

    public void disConnect() {
        modules.values().forEach(IModule::disconnect);
    }

    public void dropInv(World world, BlockPos pos) {
        modules.values().forEach(IModule::disconnect);
        modules.values().forEach(IModule::onRemoved);
        for (ItemStack stack : stacks) {
            Misc.spawnItemStackInWorld(world, pos, stack);
        }
        stacks.clear();
        modules.clear();
    }

    public void execute() {
        modules.values().forEach(iModule -> {
            if (iModule.canExecute()) {
                iModule.execute(owner);
            }
        });
    }

    public boolean putstack(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) {

            ItemStack existing = stacks.get(slot);
            if (existing.isEmpty()) return false;

            IModule module = existing.getCapability(CapabilityModule.CAPABILITY_MODULE, null);
            module.disconnect();
            module.onRemoved();
            modules.remove(module.getUUID());
            if (existing.getTagCompound() != null)
                existing.getTagCompound().removeTag("moduleID");
            stacks.set(slot, ItemStack.EMPTY);
            return true;
        }
        if (isStackValid(stack)) {
            IModule module = stack.getCapability(CapabilityModule.CAPABILITY_MODULE, null);
            if (moduleConflict(module)) return false;
            UUID uuid = UUID.randomUUID();
            module.setUUID(uuid);
            module.onAdd(owner);
            if (owner.hasNetwork())
                module.connect(owner);
            modules.put(uuid, module);
            stacks.set(slot, stack);
        }
        return false;
    }

    private boolean moduleConflict(IModule module) {
        for (IModule module1 : modules.values()) {
            if (module.conflictwithOther(module1))
                return true;
        }
        return false;
    }

    public boolean putstack(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            for (int i = 0; i < stacks.size(); i++) {
                ItemStack existing = stacks.get(i);
                if (existing.isEmpty()) continue;
                if (putstack(i, stack))
                    return true;
            }
        }

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack existing = stacks.get(i);
            if (!existing.isEmpty()) continue;
            if (putstack(i, stack))
                return true;
        }
        return false;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        for (IModule module : modules.values()) {
            if (module.hasCapability(capability, facing))
                return true;
        }
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        for (IModule module : modules.values()) {
            if (module.hasCapability(capability, facing))
                return module.getCapability(capability, facing);
        }
        return null;
    }

    public TileGenericPipe getOwner() {
        return owner;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return ItemStackHelper.saveAllItems(new NBTTagCompound(), stacks);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        ItemStackHelper.loadAllItems(nbt, stacks);
        modules.clear();
        for (ItemStack stack : stacks) {
            IModule module = stack.getCapability(CapabilityModule.CAPABILITY_MODULE, null);
            modules.put(Objects.requireNonNull(module).getUUID(), module);
        }
    }
}
