package vazkii.zeta.event.client;

import java.util.function.Consumer;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import vazkii.zeta.event.bus.IZetaLoadEvent;

public record ZRegisterReloadListeners(Consumer<PreparableReloadListener> manager) implements IZetaLoadEvent, Consumer<PreparableReloadListener> {
	@Override
	public void accept(PreparableReloadListener bleh) {
		manager.accept(bleh);
	}
}
