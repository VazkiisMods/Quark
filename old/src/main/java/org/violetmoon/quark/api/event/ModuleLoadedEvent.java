package org.violetmoon.quark.api.event;

/**
 * Fired when quark's module loader loads a module. This is before
 * the module's config is resolved.
 */
public class ModuleLoadedEvent extends QuarkModuleEvent {

	public ModuleLoadedEvent(String eventName) {
		super(eventName);
	}

}
