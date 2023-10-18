package vazkii.zetaimplforge.event.client;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import vazkii.zeta.event.bus.FiredAs;
import vazkii.zeta.event.client.ZAddItemColorHandlers;

@FiredAs(ZAddItemColorHandlers.class)
public record ForgeZAddItemColorHandlers(RegisterColorHandlersEvent.Item e) implements ZAddItemColorHandlers {
	@Override
	public void register(ItemColor c, ItemLike... items) {
		e.register(c, items);
	}

	@Override
	public ItemColors getItemColors() {
		return e.getItemColors();
	}
}
