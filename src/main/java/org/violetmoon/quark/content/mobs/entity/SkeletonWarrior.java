package org.violetmoon.quark.content.mobs.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.quark.content.mobs.ai.SwitchBetweenRangedAndMeleeAttackGoal;

import javax.annotation.Nullable;

public class SkeletonWarrior extends Skeleton {

    public SkeletonWarrior(EntityType<SkeletonWarrior> type, Level world) {
        super(type, world);
        xpReward = 1;
        setNoAi(false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new SkeletonWarrior.EntityAISwitchWeapons(this, 5D, 6D, new ItemStack(Items.IRON_SWORD), new ItemStack(Items.BOW)));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new SwitchBetweenRangedAndMeleeAttackGoal(this, 1.35D, 20, 15.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    protected void playStepSound(BlockPos p_32159_, BlockState p_32160_) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_32146_, DifficultyInstance p_32147_, MobSpawnType p_32148_, @Nullable SpawnGroupData p_32149_, @Nullable CompoundTag p_32150_)
    {
        p_32149_ = super.finalizeSpawn(p_32146_, p_32147_, p_32148_, p_32149_, p_32150_);

        this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * p_32147_.getEffectiveDifficulty());

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
        {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
        }

        if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty())
        {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }

        return p_32149_;
    }

    public ItemStack getBackItem()
    {
        if(this.getMainHandItem().getItem() == Items.IRON_SWORD)
        {
            return new ItemStack(Items.BOW);
        }
        else
        {
            return new ItemStack(Items.IRON_SWORD);
        }
    }

    protected AbstractArrow getArrow(ItemStack p_34189_, float p_34190_) {
        AbstractArrow abstractarrow = super.getArrow(p_34189_, p_34190_);

        if (abstractarrow instanceof Arrow)
        {
            ((Arrow)abstractarrow).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600));
        }

        return abstractarrow;
    }

    public class EntityAISwitchWeapons extends Goal
    {
        SkeletonWarrior mob;
        Entity target;
        double minDistance;
        double maxDistance;
        ItemStack weaponOne;
        ItemStack weaponTwo;

        public EntityAISwitchWeapons(SkeletonWarrior entitymob, double minDistance, double maxDistance, ItemStack weaponOne, ItemStack weaponTwo) {
            mob = entitymob;
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
            this.weaponOne = weaponOne;
            this.weaponTwo = weaponTwo;
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean canUse()
        {
            this.target = this.mob.getTarget();

            if (target == null)
            {
                return false;
            }
            else if (!target.isAlive())
            {
                return false;
            }
            else
            {
                if(((this.mob.distanceTo(this.target) < minDistance && this.mob.getMainHandItem() != weaponOne) ||
                        (this.mob.distanceTo(this.target) > maxDistance && this.mob.getMainHandItem() != weaponTwo)) && this.mob.canBeSeenByAnyone())
                {
                    return true;
                }

                return false;
            }
        }

        public void stop()
        {
            target = null;
        }

        public void tick()
        {
            if(this.mob.distanceTo(this.target) < minDistance)
            {
                this.mob.setItemSlot(EquipmentSlot.MAINHAND, weaponOne);
            }
            else if(this.mob.distanceTo(this.target) > maxDistance)
            {
                this.mob.setItemSlot(EquipmentSlot.MAINHAND, weaponTwo);
            }
        }
    }

    public void reassessWeaponGoal() {}

    public static boolean checkSkeletonWarriorSpawnRules(EntityType<? extends SkeletonWarrior> p_33018_, ServerLevelAccessor p_33019_, MobSpawnType p_33020_, BlockPos p_33021_, RandomSource p_33022_) {
        return p_33019_.getBlockState(p_33021_.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && isDarkEnoughToSpawn(p_33019_, p_33021_, p_33022_) && checkMobSpawnRules(p_33018_, p_33019_, p_33020_, p_33021_, p_33022_);
    }
}