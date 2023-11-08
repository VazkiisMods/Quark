package org.violetmoon.zeta.client.event.load;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import java.util.function.Function;

public interface ZTooltipComponents extends IZetaLoadEvent {
	<T extends TooltipComponent> void register(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory);

	default <T extends ClientTooltipComponent & TooltipComponent> void register(Class<T> type) {
		register(type, Function.identity());
	}
}
