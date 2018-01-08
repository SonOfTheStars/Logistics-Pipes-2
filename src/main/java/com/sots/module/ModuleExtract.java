package com.sots.module;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import com.sots.tiles.TileRoutedPipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class ModuleExtract extends ModuleBase implements IModule{

	public ModuleExtract(UUID ID) {
		super(ID);
	}

	@Override
	public boolean execute(TileRoutedPipe te) {
		boolean hasWorked = false;
		int count = 0;
		int slot = 0;
		EnumFacing face = te.network.getDirectionForDestination(te.nodeID);
		ArrayList<ItemStack> stacks = te.getItemTypesInInventory(face);
		
		UUID nodeT = te.network.getStorageNodesForItem(stacks.get(0).getItem()).get(0);
		while(!hasWorked) {
			if(nodeT.equals(te.nodeID))
				count+=1;
				ItemStack stack = stacks.get(slot).copy();
				if(stack.getCount()>=count) {
					stack.setCount(1);//Only send one item per destination
					te.routeItemTo(nodeT, stack);
				}
				else {
					if (stacks.size() <= slot + 1) {
						break;
					}
					slot+=1;
					count = 1;
					stack = stacks.get(slot).copy();
					stack.setCount(1);//Only send one item per destination
					te.routeItemTo(nodeT, stack);
				}
		}
		
		return false;
	}

	@Override
	public boolean canExecute() {return true;}

	@Override
	public boolean canInsert() {return true;}

	@Override
	public ModuleType modType() {return ModuleType.EXTRACT;}

}
