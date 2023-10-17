package vazkii.zeta.event.client;

import java.util.function.Function;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import vazkii.zeta.event.bus.IZetaLoadEvent;

public interface ZTooltipComponents extends IZetaLoadEvent {
	<T extends TooltipComponent> void register(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory);

	default <T extends ClientTooltipComponent & TooltipComponent> void register(Class<T> type) {
		register(type, Function.identity());
	}
}
