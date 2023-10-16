package vazkii.quark.content.mobs.entity;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.NetworkHooks;
import vazkii.zeta.util.ItemNBTHelper;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.content.mobs.module.ForgottenModule;
import vazkii.quark.content.tools.module.ColorRunesModule;

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
	public SpawnGroupData finalizeSpawn(@Nonnull ServerLevelAccessor worldIn, @Nonnull DifficultyInstance difficultyIn, @Nonnull MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		SpawnGroupData ilivingentitydata = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
		reassessWeaponGoal();

		return ilivingentitydata;
	}

	@Override
	public void tick() {
		super.tick();

		if(!level.isClientSide) {
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
		level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX() + Math.random() * w - w/2, getY() + Math.random() * h, getZ() + Math.random() * w - w/2, 0, 0, 0);
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


	@Nonnull
	@Override
	protected ResourceLocation getDefaultLootTable() {
		return FORGOTTEN_LOOT_TABLE;
	}

	@Override
	public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		CompoundTag sheathed = new CompoundTag();
		entityData.get(SHEATHED_ITEM).save(sheathed);
		compound.put("sheathed", sheathed);
	}

	@Override
	public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		CompoundTag sheathed = compound.getCompound("sheathed");
		entityData.set(SHEATHED_ITEM, ItemStack.of(sheathed));
	}

	@Override
	protected float getStandingEyeHeight(@Nonnull Pose poseIn, @Nonnull EntityDimensions sizeIn) {
		return 2.1F;
	}

	@Override
	protected void dropCustomDeathLoot(@Nonnull DamageSource source, int looting, boolean recentlyHitIn) {
		// NO-OP
	}
	
	@Override
	public boolean canPickUpLoot() {
		return false;
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(RandomSource rand, @Nonnull DifficultyInstance difficulty) {
		super.populateDefaultEquipmentSlots(rand, difficulty);

		prepareEquipment();
	}

	public void prepareEquipment() {
		ItemStack bow = new ItemStack(Items.BOW);
		ItemStack sheathed = new ItemStack(Items.IRON_SWORD);

		EnchantmentHelper.enchantItem(random, bow, 20, false);
		EnchantmentHelper.enchantItem(random, sheathed, 20, false);

		if(ModuleLoader.INSTANCE.isModuleEnabled(ColorRunesModule.class) && random.nextBoolean()) {
			List<Item> items = MiscUtil.getTagValues(level.registryAccess(), ColorRunesModule.runesLootableTag);
			if (!items.isEmpty()) {
				ItemStack item = new ItemStack(items.get(random.nextInt(items.size())));
				CompoundTag runeNbt = item.serializeNBT();

				ItemNBTHelper.setBoolean(bow, ColorRunesModule.TAG_RUNE_ATTACHED, true);
				ItemNBTHelper.setBoolean(sheathed, ColorRunesModule.TAG_RUNE_ATTACHED, true);

				ItemNBTHelper.setCompound(bow, ColorRunesModule.TAG_RUNE_COLOR, runeNbt);
				ItemNBTHelper.setCompound(sheathed, ColorRunesModule.TAG_RUNE_COLOR, runeNbt);
			}
		}

		setItemSlot(EquipmentSlot.MAINHAND, bow);
		entityData.set(SHEATHED_ITEM, sheathed);

		setItemSlot(EquipmentSlot.HEAD, new ItemStack(ForgottenModule.forgotten_hat));
	}

	@Nonnull
	@Override
	protected AbstractArrow getArrow(@Nonnull ItemStack arrowStack, float distanceFactor) {
		AbstractArrow arrow = super.getArrow(arrowStack, distanceFactor);
		if(arrow instanceof Arrow arrowInstance) {
			ItemStack stack = new ItemStack(Items.TIPPED_ARROW);
			PotionUtils.setCustomEffects(stack, ImmutableSet.of(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0)));
			arrowInstance.setEffectsFromItem(stack);
		}

		return arrow;
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
