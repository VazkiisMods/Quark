package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.LivingGetter;

import java.util.Collection;

public interface ZLivingDrops extends IZetaPlayEvent, Cancellable, LivingGetter {
	DamageSource getSource();
	Collection<ItemEntity> getDrops();
	int getLootingLevel();
	boolean isRecentlyHit();

	interface Lowest extends ZLivingDrops { }
}
