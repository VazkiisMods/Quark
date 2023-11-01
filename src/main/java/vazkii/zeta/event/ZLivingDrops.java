package vazkii.zeta.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import vazkii.zeta.event.bus.IZetaPlayEvent;

import java.util.Collection;

public interface ZLivingDrops extends IZetaPlayEvent {
    LivingEntity getEntity();
    Collection<ItemEntity> getDrops();
}
