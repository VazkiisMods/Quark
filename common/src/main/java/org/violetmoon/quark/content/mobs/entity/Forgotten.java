package org.violetmoon.quark.content.mobs.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.mobs.module.ForgottenModule;
import org.violetmoon.quark.content.tools.base.RuneColor;
import org.violetmoon.quark.content.tools.module.ColorRunesModule;

import java.util.stream.Stream;

public class Forgotten extends Skeleton {

	public static final EntityDataAccessor<ItemStack> SHEATHED_ITEM = SynchedEntityData.defineId(Forgotten.class, EntityDataSerializers.ITEM_STACK);

	public static final ResourceLocation FORGOTTEN_LOOT_TABLE = new ResourceLocation("quark", "entities/forgotten");

	public Forgotten(EntityType<? extends Forgotten> type, Level world) {
		super(type, world);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(SHEATHED_ITEM, ItemStack.EMPTY);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.MAX_HEALTH, 60)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1);
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		SpawnGroupData ilivingentitydata = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
		reassessWeaponGoal();

		return ilivingentitydata;
	}

	@Override
	public void tick() {
		super.tick();

		if(!level().isClientSide) {
			LivingEntity target = getTarget();
			boolean shouldUseBow = target == null;
			if(!shouldUseBow) {
				MobEffectInstance eff = target.getEffect(MobEffects.BLINDNESS);
				shouldUseBow = eff == null || eff.getDuration() < 20;
			}

			boolean isUsingBow = getMainHandItem().getItem() instanceof BowItem;
			if(shouldUseBow != isUsingBow)
				swap();
		}

		double w = getBbWidth() * 2;
		double h = getBbHeight();
		level().addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX() + Math.random() * w - w / 2, getY() + Math.random() * h, getZ() + Math.random() * w - w / 2, 0, 0, 0);
	}

	private void swap() {
		ItemStack curr = getMainHandItem();
		ItemStack off = entityData.get(SHEATHED_ITEM);

		setItemInHand(InteractionHand.MAIN_HAND, off);
		entityData.set(SHEATHED_ITEM, curr);

		Stream<WrappedGoal> stream = goalSelector.getRunningGoals();
		stream.map(WrappedGoal::getGoal)
				.filter(g -> g instanceof MeleeAttackGoal || g instanceof RangedBowAttackGoal<?>)
				.forEach(Goal::stop);

		reassessWeaponGoal();
	}

	@NotNull
	@Override
	protected ResourceLocation getDefaultLootTable() {
		return FORGOTTEN_LOOT_TABLE;
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		CompoundTag sheathed = new CompoundTag();
		entityData.get(SHEATHED_ITEM).save(sheathed);
		compound.put("sheathed", sheathed);
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		CompoundTag sheathed = compound.getCompound("sheathed");
		entityData.set(SHEATHED_ITEM, ItemStack.of(sheathed));
	}

	@Override
	protected float getStandingEyeHeight(@NotNull Pose poseIn, @NotNull EntityDimensions sizeIn) {
		return 2.1F;
	}

	@Override
	protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHitIn) {
		// NO-OP
	}

	@Override
	public boolean canPickUpLoot() {
		return false;
	}

	@Override
	protected void populateDefaultEquipmentSlots(@NotNull RandomSource rand, @NotNull DifficultyInstance difficulty) {
		super.populateDefaultEquipmentSlots(rand, difficulty);

		prepareEquipment();
	}

	public void prepareEquipment() {
		ItemStack bow = new ItemStack(Items.BOW);
		ItemStack sheathed = new ItemStack(Items.IRON_SWORD);

		EnchantmentHelper.enchantItem(random, bow, 20, false);
		EnchantmentHelper.enchantItem(random, sheathed, 20, false);

		if(Quark.ZETA.modules.isEnabled(ColorRunesModule.class) && random.nextBoolean()) {
			DyeColor color = DyeColor.values()[random.nextInt(DyeColor.values().length)];
			RuneColor rune = RuneColor.byDyeColor(color);

			if (rune != null) {
				ColorRunesModule.withRune(bow, rune);
				ColorRunesModule.withRune(sheathed, rune);
			}
		}

		setItemSlot(EquipmentSlot.MAINHAND, bow);
		entityData.set(SHEATHED_ITEM, sheathed);

		setItemSlot(EquipmentSlot.HEAD, new ItemStack(ForgottenModule.forgotten_hat));
	}

	@NotNull
	@Override
	protected AbstractArrow getArrow(@NotNull ItemStack arrowStack, float distanceFactor) {
		AbstractArrow arrow = super.getArrow(arrowStack, distanceFactor);
		if(arrow instanceof Arrow arrowInstance) {
			ItemStack stack = new ItemStack(Items.TIPPED_ARROW);
			PotionUtils.setCustomEffects(stack, ImmutableSet.of(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0)));
			arrowInstance.setEffectsFromItem(stack);
		}

		return arrow;
	}

	@NotNull
	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
