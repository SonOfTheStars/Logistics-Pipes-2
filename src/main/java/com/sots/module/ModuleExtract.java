package com.sots.module;

import java.util.ArrayList;
import java.util.UUID;

import com.sots.tiles.TileRoutedPipe;
import com.sots.util.References;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModuleExtract extends ModuleBase implements IModule{
	
	private int ticksTillOp = References.MOD_BASE_OPERATION_RATE;

	public ModuleExtract(UUID ID) {
		super(ID);
	}

	@Override
	public boolean execute(TileRoutedPipe te) {
		if(ticksTillOp!=0) {
			ticksTillOp-=1;
		}else {
			
			ticksTillOp=References.MOD_BASE_OPERATION_RATE;
			if(te.hasInventory() && te.hasNetwork()) {
				boolean hasWorked = false;
				int tryDest = 0;
				int tryItem = 0;
				ArrayList<ItemStack> stacks = te.getItemsInInventory(te.getNetwork().getDirectionForDestination(te.nodeID));
				try {
					if(!stacks.isEmpty()) {
						Item item = stacks.get(tryItem).getItem();
						while(!te.getNetwork().hasStorageForItem(item) && tryItem<stacks.size()) {
							tryItem+=1;
							item = stacks.get(tryItem).getItem();
						}
						UUID nodeT = te.getNetwork().getClosestStorageNode(item, te.nodeID, tryDest);
						while(!hasWorked) {
							if(nodeT==null) {
								return false;
							}
							if(nodeT.equals(te.nodeID)) {
								tryDest+=1;
								nodeT = te.getNetwork().getClosestStorageNode(item, te.nodeID, tryDest);
								continue;
							}
							
							ItemStack stack = stacks.get(tryItem).copy();
							if(stack.getCount()>References.MOD_EXTRACT_BASE_COUNT) {
								stack.setCount(References.MOD_EXTRACT_BASE_COUNT);
							}
							te.routeItemTo(nodeT, stack);
							hasWorked=true;
						}
					}
				} catch (Exception e) {
					return false;
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
	public ModuleType modType() {return ModuleType.EXTRACT;}
	
	@Override
	public void disconnect() {
		//Doesnt need to be disconnected
		return;
	}

}
