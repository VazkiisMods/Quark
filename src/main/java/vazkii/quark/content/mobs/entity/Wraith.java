package vazkii.quark.content.mobs.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.content.mobs.module.WraithModule;

import javax.annotation.Nonnull;

public class Wraith extends Zombie {

	public static final ResourceLocation LOOT_TABLE = new ResourceLocation("quark:entities/wraith");

	private static final EntityDataAccessor<String> IDLE_SOUND = SynchedEntityData.defineId(Wraith.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<String> HURT_SOUND = SynchedEntityData.defineId(Wraith.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<String> DEATH_SOUND = SynchedEntityData.defineId(Wraith.class, EntityDataSerializers.STRING);
	private static final String TAG_IDLE_SOUND = "IdleSound";
	private static final String TAG_HURT_SOUND = "HurtSound";
	private static final String TAG_DEATH_SOUND = "DeathSound";

	public Wraith(EntityType<? extends Wraith> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		entityData.define(IDLE_SOUND, "");
		entityData.define(HURT_SOUND, "");
		entityData.define(DEATH_SOUND, "");
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 15)
				.add(Attributes.FOLLOW_RANGE, 35)
				.add(Attributes.MOVEMENT_SPEED, 0.28)
				.add(Attributes.ATTACK_DAMAGE, 3)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1)
				.add(Attributes.ARMOR, 0)
				.add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0);
	}

	@Override
	protected void populateDefaultEquipmentSlots(@Nonnull DifficultyInstance difficulty) {
		// NO-OP
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return getSound(IDLE_SOUND);
	}

	@Override
	protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource) {
		return getSound(HURT_SOUND);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return getSound(DEATH_SOUND);
	}

	@Override
	public float getVoicePitch() {
		return random.nextFloat() * 0.1F + 0.75F;
	}

	public SoundEvent getSound(EntityDataAccessor<String> param) {
		ResourceLocation loc = new ResourceLocation(entityData.get(param));
		return ForgeRegistries.SOUND_EVENTS.getValue(loc);
	}

	@Override
	public void tick() {
		super.tick();

		AABB aabb = getBoundingBox();
		double x = aabb.minX + Math.random() * (aabb.maxX - aabb.minX);
		double y = aabb.minY + Math.random() * (aabb.maxY - aabb.minY);
		double z = aabb.minZ + Math.random() * (aabb.maxZ - aabb.minZ);
		getCommandSenderWorld().addParticle(ParticleTypes.MYCELIUM, x, y, z, 0, 0, 0);
	}

	@Override
	public boolean doHurtTarget(@Nonnull Entity entityIn) {
		boolean did = super.doHurtTarget(entityIn);
		if(did) {
			if(entityIn instanceof LivingEntity living)
				living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));

			double dx = getX() - entityIn.getX();
			double dz = getZ() - entityIn.getZ();
			Vec3 vec = new Vec3(dx, 0, dz).normalize().add(0, 0.5, 0).normalize().scale(0.85);
			setDeltaMovement(vec);
		}

		return did;
	}

	@Override
	public SpawnGroupData finalizeSpawn(@Nonnull ServerLevelAccessor worldIn, @Nonnull DifficultyInstance difficultyIn, @Nonnull MobSpawnType reason, SpawnGroupData spawnDataIn, CompoundTag dataTag) {
		int wraithSoundsSize = WraithModule.validWraithSounds.size();
		if(wraithSoundsSize > 0) {
			int idx = random.nextInt(wraithSoundsSize);
			String sound = WraithModule.validWraithSounds.get(idx);
			String[] split = sound.split("\\|");
	
			entityData.set(IDLE_SOUND, split[0]);
			entityData.set(HURT_SOUND, split[1]);
			entityData.set(DEATH_SOUND, split[2]);
		}

		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier, @Nonnull DamageSource source) {
		return false;
	}

	@Override
	public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		compound.putString(TAG_IDLE_SOUND, entityData.get(IDLE_SOUND));
		compound.putString(TAG_HURT_SOUND, entityData.get(HURT_SOUND));
		compound.putString(TAG_DEATH_SOUND, entityData.get(DEATH_SOUND));
	}

	@Override
	public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		entityData.set(IDLE_SOUND, compound.getString(TAG_IDLE_SOUND));
		entityData.set(HURT_SOUND, compound.getString(TAG_HURT_SOUND));
		entityData.set(DEATH_SOUND, compound.getString(TAG_DEATH_SOUND));
	}

	@Nonnull
	@Override
	protected ResourceLocation getDefaultLootTable() {
		return LOOT_TABLE;
	}

	@Override
	public void setBaby(boolean childZombie) {
		// NO-OP
	}

	@Override
	public boolean isBaby() {
		return false;
	}

	@Override
	public float getWalkTargetValue(@Nonnull BlockPos pos, LevelReader worldIn) {
		BlockState state = worldIn.getBlockState(pos);
		return state.is(WraithModule.wraithSpawnableTag) ? 1F : 0F;
	}

	@Override
	public boolean hurt(@Nonnull DamageSource source, float amount) {
		if (!super.hurt(source, amount)) {
			return false;
		} else return this.level instanceof ServerLevel;
	}

	@Override
	protected void handleAttributes(float difficulty) {}

}
