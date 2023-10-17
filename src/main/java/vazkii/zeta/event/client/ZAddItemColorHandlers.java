package vazkii.zeta.event.client;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.level.ItemLike;
import vazkii.zeta.event.bus.IZetaLoadEvent;

public interface ZAddItemColorHandlers extends IZetaLoadEvent {
	void register(ItemColor c, ItemLike... items);
	ItemColors getItemColors();
}
