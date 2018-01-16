package com.sots.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sots.LogisticsPipes2;

import net.minecraft.item.Item;

public class PassiveDestinationSelector {

	private Map<Item, List<UUID>> destinations = new HashMap<Item, List<UUID>>();
	
	public void registerPassiveDestination(UUID id, Item item) {
		if (destinations.containsKey(item)) {
			if (destinations.get(item).contains(id)) {
				LogisticsPipes2.logger.warn("Tried to register passive destination [" + id + "] for item [" + item + "] twice");
			} else {
				destinations.get(item).add(id);
				LogisticsPipes2.logger.info("Registered passive destination [" + id + "] for item [" + item + "]");
			}
		} else {
			List<UUID> tmp = new ArrayList<UUID>();
			tmp.add(id);
			destinations.put(item, tmp);
			LogisticsPipes2.logger.info("Registered passive destination [" + id + "] for item [" + item + "]");
		}
	}

	public void unregisterPassiveDestination(UUID id, Item item) {
		if (destinations.containsKey(item)) {
			if (destinations.get(item).contains(id)) {
				destinations.get(item).remove(id);
				LogisticsPipes2.logger.info("Unregistered passive destination [" + id + "] for item [" + item + "]");
				if (destinations.get(item).size() == 0) {
					destinations.remove(item);
				}
			} else {
				LogisticsPipes2.logger.info("Tried to unregister passive destination [" + id + "] for item [" + item + "] despite it not being a passive destination");
			}
		} else {
			LogisticsPipes2.logger.info("Tried to unregister passive destination [" + id + "] for item [" + item + "] despite it not being a passive destination");
		}
	}


	/**
	 * Get a random passive destination for the item
	 */
	public UUID getPassiveDestinationFor(Item item) {
		if (!destinations.containsKey(item)) {
			return null;
		}
		List<UUID> list = destinations.get(item);
		return list.get(((int) Math.random())*list.size());
	}
}

