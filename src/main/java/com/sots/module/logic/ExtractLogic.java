package com.sots.module.logic;

import static com.sots.module.logic.ModuleType.MODULE_EXTRACT;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.routing.offers.OfferType;
import com.sots.routing.promises.PromiseType;
import com.sots.tiles.TileRoutedPipe;
import com.sots.util.References;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class ExtractLogic implements IModuleLogic{
	
	private int ticksTillOp = References.MOD_BASE_OPERATION_RATE;

	@Override
	public void execute(TileRoutedPipe te) {
		if(ticksTillOp!=0) {
			ticksTillOp-=1;
		}
		else {
			ticksTillOp=References.MOD_BASE_OPERATION_RATE;
			if(te.hasInventory() && te.hasNetwork()) {
				EnumFacing face = te.getNetwork().getDirectionForDestination(te.nodeID);
				ArrayList<ItemStack> stacks = te.getItemsInInventory(face);
				try {
					if(!stacks.isEmpty()) {
						Optional<ItemStack> toProcess = stacks.stream().filter(s -> te.getNetwork().hasTypedOfferFor(OfferType.SINK, s.getItem())).findFirst();
						if(toProcess.isPresent()) {
							ItemStack stack = toProcess.get().copy();
							Optional<UUID> targetNodeId = te.getNetwork().getClosestOfferer(OfferType.SINK, stack.getItem(), te.nodeID);
							if(targetNodeId.isPresent()) {
								ItemStack copy = stack.copy();
								copy.setCount(References.MOD_EXTRACT_BASE_COUNT);
								te.routeItemTo(targetNodeId.get(), copy.getItem(), PromiseType.PROMISE_SINK, References.MOD_EXTRACT_BASE_COUNT);
							}
						}
					}
				} catch (Exception e) {
					return;
				}
			}
		}
	}

	@Override
	public ModuleType getType() {
		return MODULE_EXTRACT;
	}

	@Override
	public void onInvalidate(TileRoutedPipe te) {}

}
