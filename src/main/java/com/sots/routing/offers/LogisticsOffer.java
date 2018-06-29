package com.sots.routing.offers;

import java.util.UUID;

import net.minecraft.item.Item;

public class LogisticsOffer {
	public final UUID id;
	public final OfferType type;
	public final Item content;
	public final UUID node;
	
	private LogisticsOffer(OfferType type, Item content, UUID node) {
		this.id = UUID.randomUUID();
		this.type = type;
		this.content = content;
		this.node = node;
	}
	
	public static LogisticsOffer make(OfferType type, Item content, UUID node) {
		return new LogisticsOffer(type, content, node);
	}
}
