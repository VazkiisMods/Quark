package org.violetmoon.zeta.client.event.load;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import java.util.function.Consumer;

public record ZRegisterReloadListeners(Consumer<PreparableReloadListener> manager) implements IZetaLoadEvent, Consumer<PreparableReloadListener> {
	@Override
	public void accept(PreparableReloadListener bleh) {
		manager.accept(bleh);
	}
}
