package com.sots.module;

import java.util.ArrayList;
import java.util.UUID;

import com.sots.tiles.TileRoutedPipe;
import com.sots.util.data.Tuple;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModuleSink extends ModuleBase implements IModule{
	
	private boolean hasRegistered = false;
	
	public ModuleSink(UUID ID) {
		super(ID);
	}
	
	@Override
	public boolean execute(TileRoutedPipe te) {
		if(!hasRegistered) {
			if(te.hasInventory()) {
				ArrayList<ItemStack> stacks = te.getItemTypesInInventory(te.network.getDirectionForDestination(te.nodeID));
				if(!stacks.isEmpty()) {
					stacks.forEach(p -> {
						te.network.registerItemStorage(new Tuple<UUID, Item>(te.nodeID, p.getItem()));
					});
					hasRegistered=true;
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean canExecute() {return true;}
	
	@Override
	public boolean canInsert() {return true;}
	
	@Override
	public ModuleType modType() {return ModuleType.SINK;}

}
