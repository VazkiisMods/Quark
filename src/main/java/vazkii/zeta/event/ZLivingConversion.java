package vazkii.zeta.event;

import net.minecraft.world.entity.LivingEntity;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.Living;

public interface ZLivingConversion extends IZetaPlayEvent, Living {
    interface Post extends ZLivingConversion {
        LivingEntity getOutcome();
    }
}
