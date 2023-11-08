package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import org.violetmoon.zeta.event.play.entity.living.ZLivingChangeTarget;

public class ForgeZLivingChangeTarget implements ZLivingChangeTarget {
    private final LivingChangeTargetEvent e;

    public ForgeZLivingChangeTarget(LivingChangeTargetEvent e) {
        this.e = e;
    }

    @Override
    public LivingEntity getEntity() {
        return e.getEntity();
    }

    @Override
    public LivingEntity getNewTarget() {
        return e.getNewTarget();
    }

    @Override
    public LivingChangeTargetEvent.ILivingTargetType getTargetType() {
        return e.getTargetType();
    }

    @Override
    public boolean isCanceled() {
        return e.isCanceled();
    }

    @Override
    public void setCanceled(boolean cancel) {
        e.setCanceled(cancel);
    }
}
