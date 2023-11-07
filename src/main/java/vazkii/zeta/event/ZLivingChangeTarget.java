package vazkii.zeta.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import vazkii.zeta.event.bus.Cancellable;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.Living;

public interface ZLivingChangeTarget extends IZetaPlayEvent, Cancellable, Living {
    LivingEntity getNewTarget();
    LivingChangeTargetEvent.ILivingTargetType getTargetType();
}
