package org.violetmoon.quark.content.mobs.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.BowItem;
import org.violetmoon.quark.content.mobs.SkeletonWarrior;

public class SwitchBetweenRangedAndMeleeAttackGoal extends MeleeAttackGoal
{
    private final Skeleton entity;

    private final double moveSpeedAmp;

    private int attackCooldown;

    private final float maxAttackDistance;

    private int attackTime = -1;

    private int seeTime;

    private boolean strafingClockwise;

    private boolean strafingBackwards;

    private int strafingTime = -1;

    public SwitchBetweenRangedAndMeleeAttackGoal(SkeletonWarrior skeleton, double speedAmplifier, int delay, float maxDistance) {
        super(skeleton, speedAmplifier, false);
        this.entity = (Skeleton)skeleton;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
    }

    public void setMinAttackInterval(int p_189428_1_) {
        this.attackCooldown = p_189428_1_;
    }

    public boolean canUse() {
        return (this.entity.getTarget() != null);
    }

    protected boolean isHoldingBow() {
        return this.entity.getMainHandItem().getItem() instanceof BowItem;
    }

    public boolean canContinueToUse() {
        return (((canUse() || !this.entity.getNavigation().isDone()) && isHoldingBow()) || super.canContinueToUse());
    }

    public void start() {
        super.start();
        final InteractionHand swingingArm = this.entity.swingingArm;
    }

    public void stop() {
        super.stop();
        this.entity.getMainArm();
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.getHandSlots();
    }

    public void tick() {
        LivingEntity entitylivingbase = this.entity.getTarget();
        if (entitylivingbase != null)
            if (isHoldingBow()) {
                double d0 = this.entity.distanceToSqr(entitylivingbase.getX(), (entitylivingbase.getBoundingBox()).minY, entitylivingbase.getZ());
                boolean flag = this.entity.getSensing().hasLineOfSight(entitylivingbase);
                boolean flag1 = (this.seeTime > 0);
                if (flag != flag1)
                    this.seeTime = 0;
                if (flag) {
                    this.seeTime++;
                } else {
                    this.seeTime--;
                }
                if (d0 <= this.maxAttackDistance && this.seeTime >= 20) {
                    this.entity.getNavigation().stop();
                    this.strafingTime++;
                } else {
                    this.entity.getNavigation().moveTo((Entity)entitylivingbase, this.moveSpeedAmp);
                    this.strafingTime = -1;
                }
                if (this.strafingTime >= 20) {
                    if (this.entity.getRandom().nextFloat() < 0.3D)
                        this.strafingClockwise = !this.strafingClockwise;
                    if (this.entity.getRandom().nextFloat() < 0.3D)
                        this.strafingBackwards = !this.strafingBackwards;
                    this.strafingTime = 0;
                }
                if (this.strafingTime > -1) {
                    if (d0 > (this.maxAttackDistance * 0.75F)) {
                        this.strafingBackwards = false;
                    } else if (d0 < (this.maxAttackDistance * 0.25F)) {
                        this.strafingBackwards = true;
                    }
                    this.entity.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                    this.entity.lookAt((Entity)entitylivingbase, 30.0F, 30.0F);
                } else {
                    this.entity.getLookControl().setLookAt((Entity)entitylivingbase, 30.0F, 30.0F);
                }
                if (this.entity.isUsingItem()) {
                    if (!flag && this.seeTime < -60) {
                        this.entity.stopUsingItem();
                    } else if (flag) {
                        int i = this.entity.getTicksUsingItem();
                        if (i >= 20) {
                            this.entity.stopUsingItem();
                            this.entity.performRangedAttack(entitylivingbase, BowItem.getPowerForTime(i));
                            this.attackTime = this.attackCooldown;
                        }
                    }
                } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
                    this.entity.startUsingItem(InteractionHand.MAIN_HAND);
                }
            } else {
                this.entity.setXxa(0.0F);
                super.tick();
            }
    }
}
