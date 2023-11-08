package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.LivingGetter;

public interface ZLivingConversion extends IZetaPlayEvent, Cancellable, LivingGetter {
    interface Pre extends ZLivingConversion {
        EntityType<? extends LivingEntity> getOutcome();
    }

    interface Post extends ZLivingConversion {
        LivingEntity getOutcome();
    }
}
