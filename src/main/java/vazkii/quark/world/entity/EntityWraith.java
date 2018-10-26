/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 03:49:22 (GMT)]
 */
package vazkii.quark.world.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityWraith extends EntityZombie {

	public static final ResourceLocation LOOT_TABLE = new ResourceLocation("quark:entities/wraith");

	private static final DataParameter<Integer> SOUND_TYPE = EntityDataManager.<Integer>createKey(EntityWraith.class, DataSerializers.VARINT);
	private static final String TAG_SOUND_TYPE = "SoundType";

	private static SoundEvent[][] SOUNDS = new SoundEvent[][] {
		{ SoundEvents.ENTITY_SHEEP_AMBIENT, SoundEvents.ENTITY_SHEEP_HURT, SoundEvents.ENTITY_SHEEP_DEATH },
		{ SoundEvents.ENTITY_COW_AMBIENT, SoundEvents.ENTITY_COW_HURT, SoundEvents.ENTITY_COW_DEATH },
		{ SoundEvents.ENTITY_PIG_AMBIENT, SoundEvents.ENTITY_PIG_HURT, SoundEvents.ENTITY_PIG_DEATH },
		{ SoundEvents.ENTITY_CHICKEN_AMBIENT, SoundEvents.ENTITY_CHICKEN_HURT, SoundEvents.ENTITY_CHICKEN_DEATH },
		{ SoundEvents.ENTITY_HORSE_AMBIENT, SoundEvents.ENTITY_HORSE_HURT, SoundEvents.ENTITY_HORSE_DEATH },
		{ SoundEvents.ENTITY_CAT_AMBIENT, SoundEvents.ENTITY_CAT_HURT, SoundEvents.ENTITY_CAT_DEATH },
		{ SoundEvents.ENTITY_WOLF_AMBIENT, SoundEvents.ENTITY_WOLF_HURT, SoundEvents.ENTITY_WOLF_DEATH },
		{ SoundEvents.ENTITY_VILLAGER_AMBIENT, SoundEvents.ENTITY_VILLAGER_HURT, SoundEvents.ENTITY_VILLAGER_DEATH },
		{ SoundEvents.ENTITY_POLAR_BEAR_AMBIENT, SoundEvents.ENTITY_POLAR_BEAR_HURT, SoundEvents.ENTITY_POLAR_BEAR_DEATH },
		{ SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundEvents.ENTITY_ZOMBIE_HURT, SoundEvents.ENTITY_ENDERMEN_DEATH },
		{ SoundEvents.ENTITY_SKELETON_AMBIENT, SoundEvents.ENTITY_SKELETON_HURT, SoundEvents.ENTITY_SKELETON_DEATH },
		{ SoundEvents.ENTITY_SPIDER_AMBIENT, SoundEvents.ENTITY_SPIDER_HURT, SoundEvents.ENTITY_SPIDER_DEATH },
		{ null, SoundEvents.ENTITY_CREEPER_HURT, SoundEvents.ENTITY_CREEPER_DEATH },
		{ SoundEvents.ENTITY_ENDERMEN_AMBIENT, SoundEvents.ENTITY_ENDERMEN_HURT, SoundEvents.ENTITY_ENDERMEN_DEATH },
		{ SoundEvents.ENTITY_ZOMBIE_PIG_AMBIENT, SoundEvents.ENTITY_ZOMBIE_PIG_HURT, SoundEvents.ENTITY_ZOMBIE_PIG_DEATH },
		{ SoundEvents.ENTITY_WITCH_AMBIENT, SoundEvents.ENTITY_WITCH_HURT, SoundEvents.ENTITY_WITCH_DEATH },
		{ SoundEvents.ENTITY_BLAZE_AMBIENT, SoundEvents.ENTITY_BLAZE_HURT, SoundEvents.ENTITY_BLAZE_DEATH },
		{ SoundEvents.ENTITY_LLAMA_AMBIENT, SoundEvents.ENTITY_LLAMA_HURT, SoundEvents.ENTITY_LLAMA_DEATH }
	};

	public EntityWraith(World worldIn) {
		super(worldIn);
		isImmuneToFire = true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(SOUND_TYPE, -1);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0);
		getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		// NO-OP
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return getSoundForIndex(0);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return getSoundForIndex(1);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return getSoundForIndex(2);
	}

	@Override
	protected float getSoundPitch() {
		return rand.nextFloat() * 0.1F + 0.75F;
	}

	public int getSoundType() {
		return dataManager.get(SOUND_TYPE);
	}

	public SoundEvent getSoundForIndex(int i) {
		int soundType = getSoundType();
		if(soundType == -1)
			return null;

		return SOUNDS[soundType][i];
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(getSoundType() == -1)
			dataManager.set(SOUND_TYPE, rand.nextInt(SOUNDS.length));


		AxisAlignedBB aabb = getEntityBoundingBox();
		double x = aabb.minX + Math.random() * (aabb.maxX - aabb.minX);
		double y = aabb.minY + Math.random() * (aabb.maxY - aabb.minY);
		double z = aabb.minZ + Math.random() * (aabb.maxZ - aabb.minZ);
		getEntityWorld().spawnParticle(EnumParticleTypes.TOWN_AURA, x, y, z, 0, 0, 0);
	}


	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean did = super.attackEntityAsMob(entityIn);
		if(did) {
			if(entityIn instanceof EntityLivingBase)
				((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));

			double dx = posX - entityIn.posX;
			double dz = posZ - entityIn.posZ;
			Vec3d vec = new Vec3d(dx, 0, dz).normalize().addVector(0, 0.5, 0).normalize().scale(0.85);
			motionX = vec.x;
			motionY = vec.y;
			motionZ = vec.z;
		}

		return did;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source == DamageSource.FALL)
			return false;

		return super.attackEntityFrom(source, amount);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		compound.setInteger(TAG_SOUND_TYPE, getSoundType());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		dataManager.set(SOUND_TYPE, compound.getInteger(TAG_SOUND_TYPE));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}

	@Override
	public boolean getCanSpawnHere() {
		BlockPos blockpos = new BlockPos(posX, getEntityBoundingBox().minY - 1, posZ);
		return super.getCanSpawnHere() && getEntityWorld().getBlockState(blockpos).getBlock() == Blocks.SOUL_SAND;
	}

}
