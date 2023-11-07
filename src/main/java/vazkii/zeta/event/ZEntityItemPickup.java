package vazkii.zeta.event;

import net.minecraft.world.entity.item.ItemEntity;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.helpers.PlayerGetter;

public interface ZEntityItemPickup extends IZetaPlayEvent, PlayerGetter {
    ItemEntity getItem();
}
