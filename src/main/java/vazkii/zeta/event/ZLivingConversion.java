package vazkii.zeta.event;

import net.minecraft.world.entity.LivingEntity;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZLivingConversion extends IZetaPlayEvent {
    LivingEntity getEntity();

    interface Post extends IZetaPlayEvent, ZLivingConversion {
        LivingEntity getOutcome();
    }
}
