package org.violetmoon.quark.api.event;

import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when a module's state (enabled/disabled) is changed.
 * Cancel the event to force the module disabled.
 */
@Cancelable
public class ModuleStateChangedEvent extends QuarkModuleEvent {

	public final boolean enabled;

	public ModuleStateChangedEvent(String eventName, boolean enabled) {
		super(eventName);
		this.enabled = enabled;
	}

}
