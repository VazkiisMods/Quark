package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.LivingGetter;

public interface ZLivingChangeTarget extends IZetaPlayEvent, Cancellable, LivingGetter {
    LivingEntity getNewTarget();
    LivingChangeTargetEvent.ILivingTargetType getTargetType();
}
