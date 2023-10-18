package vazkii.zetaimplforge.event.client;

import java.util.function.Function;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import vazkii.zeta.event.bus.FiredAs;
import vazkii.zeta.event.client.ZTooltipComponents;

@FiredAs(ZTooltipComponents.class)
public record ForgeZTooltipComponents(RegisterClientTooltipComponentFactoriesEvent e) implements ZTooltipComponents {
	@Override
	public <T extends TooltipComponent> void register(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
		e.register(type, factory);
	}
}
