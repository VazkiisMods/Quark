package org.violetmoon.zetaimplforge.client.event.load;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import org.violetmoon.zeta.client.event.load.ZTooltipComponents;

import java.util.function.Function;

public record ForgeZTooltipComponents(RegisterClientTooltipComponentFactoriesEvent e) implements ZTooltipComponents {
	@Override
	public <T extends TooltipComponent> void register(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
		e.register(type, factory);
	}
}
