package com.sots.module.logic;

import static com.sots.module.logic.ModuleType.MODULE_SINK;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.sots.routing.offers.LogisticsOffer;
import com.sots.routing.offers.OfferType;
import com.sots.tiles.TileRoutedPipe;
import com.sots.util.helpers.ItemHelper;

import net.minecraft.item.Item;

public class SinkLogic implements IModuleLogic{
	
	private int ticksTillOp = 20;
	private Set<LogisticsOffer> offers = new HashSet<>();
	
	@Override
	public void execute(TileRoutedPipe te) {
		if(ticksTillOp!=0) {
			ticksTillOp-=1;
		}else {
			ticksTillOp=20;
			if(te.hasInventory() && te.hasNetwork()) {
				ArrayList<Item> types = te.getItemTypesInInventory(te.getNetwork().getDirectionForDestination(te.nodeID));
				if(!types.isEmpty()) {
					types.forEach(p -> {
						if(offers.stream().noneMatch(o -> ItemHelper.itemsEqual(o.content, p))) {
							offers.add(LogisticsOffer.make(OfferType.SINK, p, te.nodeID));
						}
					});
				}
				if(!offers.isEmpty())
					offers.forEach(o -> te.getNetwork().addOffer(o));
			}
		}
		
	}
	
	@Override
	public ModuleType getType() {
		return MODULE_SINK;
	}

	@Override
	public void onInvalidate(TileRoutedPipe te) {
		offers.forEach(o -> te.getNetwork().deleteOffer(o.id));
	}
	
}
