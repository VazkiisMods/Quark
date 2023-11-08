package org.violetmoon.zeta.event.play.entity;

import net.minecraft.world.entity.item.ItemEntity;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.PlayerGetter;

public interface ZEntityItemPickup extends IZetaPlayEvent, PlayerGetter {
    ItemEntity getItem();
}
