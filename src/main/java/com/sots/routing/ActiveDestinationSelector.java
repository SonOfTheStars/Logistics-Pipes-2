package com.sots.routing;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.sots.LogisticsPipes2;

import net.minecraft.item.Item;

public class ActiveDestinationSelector {

    private Map<Item, List<UUID>> sources = new HashMap<Item, List<UUID>>();

    public void registerPassiveSource(UUID id, Item item) {
        if (sources.containsKey(item)) {
            if (sources.get(item).contains(id)) {
                LogisticsPipes2.logger.warn("Tried to register passive destination [" + id + "] for item [" + item + "] twice");
            } else {
                sources.get(item).add(id);
                LogisticsPipes2.logger.info("Registered passive destination [" + id + "] for item [" + item + "]");
            }
        } else {
            List<UUID> tmp = new ArrayList<UUID>();
            tmp.add(id);
            sources.put(item, tmp);
            LogisticsPipes2.logger.info("Registered passive destination [" + id + "] for item [" + item + "]");
        }
    }

    public void unregisterPassiveSource(UUID id, Item item) {
        if (sources.containsKey(item)) {
            if (sources.get(item).contains(id)) {
                sources.get(item).remove(id);
                LogisticsPipes2.logger.info("Unregistered passive destination [" + id + "] for item [" + item + "]");
                if (sources.get(item).size() == 0) {
                    sources.remove(item);
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
    public UUID getPassiveSourceFor(Item item) {
        if (!sources.containsKey(item)) {
            return null;
        }
        List<UUID> list = sources.get(item);
        return list.get(((int) Math.random()) * list.size());
    }

}

