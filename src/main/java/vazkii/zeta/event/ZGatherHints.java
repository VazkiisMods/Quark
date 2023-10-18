package vazkii.zeta.event;

import java.util.function.BiConsumer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import vazkii.zeta.event.bus.IZetaPlayEvent;

//TODO ZETA: keeping this as a BiConsumer because it's more similar to the existing design; this could be fleshed out a bit more
public interface ZGatherHints extends IZetaPlayEvent, BiConsumer<Item, Component> { }
