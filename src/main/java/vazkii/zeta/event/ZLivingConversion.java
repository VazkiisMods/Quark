package vazkii.zeta.event;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import vazkii.zeta.event.bus.Cancellable;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.helpers.LivingGetter;

public interface ZLivingConversion extends IZetaPlayEvent, Cancellable, LivingGetter {
    interface Pre extends ZLivingConversion {
        EntityType<? extends LivingEntity> getOutcome();
    }

    interface Post extends ZLivingConversion {
        LivingEntity getOutcome();
    }
}
