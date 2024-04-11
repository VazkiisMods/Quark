package org.violetmoon.quark.api.event;

import net.minecraftforge.eventbus.api.Event;

public class QuarkModuleEvent extends Event {

	public final String eventName;

	public QuarkModuleEvent(String eventName) {
		this.eventName = eventName;
	}

}
