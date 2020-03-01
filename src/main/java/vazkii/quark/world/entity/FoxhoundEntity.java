/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 12:04 AM (EST)]
 */
package vazkii.quark.world.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.BegGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.NonTamedTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.tweaks.ai.WantLoveGoal;
import vazkii.quark.world.ai.FindPlaceToSleepGoal;
import vazkii.quark.world.ai.SleepGoal;
import vazkii.quark.world.module.FoxhoundModule;

public class FoxhoundEntity extends WolfEntity implements IMob {

	public static final ResourceLocation FOXHOUND_LOOT_TABLE = new ResourceLocation("quark", "entities/foxhound");

	private static final DataParameter<Boolean> TEMPTATION = EntityDataManager.createKey(FoxhoundEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SLEEPING = EntityDataManager.createKey(FoxhoundEntity.class, DataSerializers.BOOLEAN);

	private int timeUntilPotatoEmerges = 0;

	public FoxhoundEntity(EntityType<? extends FoxhoundEntity> type, World worldIn) {
		super(type, worldIn);
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		this.setPathPriority(PathNodeType.LAVA, 1.0F);
		this.setPathPriority(PathNodeType.DANGER_FIRE, 1.0F);
		this.setPathPriority(PathNodeType.DAMAGE_FIRE, 1.0F);
	}

	@Override
	protected void registerData() {
		super.registerData();
		setCollarColor(DyeColor.ORANGE);
		dataManager.register(TEMPTATION, false);
		dataManager.register(SLEEPING, false);
	}

	@Override
	public boolean isNoDespawnRequired() {
		return super.isNoDespawnRequired();
	}

	@Override
	public boolean preventDespawn() {
		return isTamed();
	}

	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return !isTamed();
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		return MiscUtil.isEntityInsideOpaqueBlock(this);
	}

	@Override
	public void tick() {
		super.tick();

		if (!world.isRemote && world.getDifficulty() == Difficulty.PEACEFUL && !isTamed()) {
			remove();
			return;
		}

		//		if (!world.isRemote && TinyPotato.tiny_potato != null) {
		//			if (timeUntilPotatoEmerges == 1) {
		//				timeUntilPotatoEmerges = 0;
		//				ItemStack stack = new ItemStack(TinyPotato.tiny_potato);
		//				ItemNBTHelper.setBoolean(stack, "angery", true);
		//				entityDropItem(stack, 0f);
		//				playSound(SoundEvents.ENTITY_GENERIC_HURT, 1f, 1f);
		//			} else if (timeUntilPotatoEmerges > 1) {
		//				timeUntilPotatoEmerges--;
		//			}
		//		}

		if (WantLoveGoal.needsPets(this)) {
			Entity owner = getOwner();
			if (owner != null && owner.getDistanceSq(this) < 1 && !owner.isInWater() && !owner.isImmuneToFire() && (!(owner instanceof PlayerEntity) || !((PlayerEntity) owner).isCreative()))
				owner.setFire(5);
		}

		Vec3d pos = getPositionVec();
		if(this.world.isRemote)
			this.world.addParticle(isSleeping() ? ParticleTypes.SMOKE : ParticleTypes.FLAME, pos.x + (this.rand.nextDouble() - 0.5D) * this.getWidth(), pos.y + (this.rand.nextDouble() - 0.5D) * this.getHeight(), pos.z + (this.rand.nextDouble() - 0.5D) * this.getWidth(), 0.0D, 0.0D, 0.0D);

		if(isTamed()) {
			BlockPos below = getPosition().down();
			TileEntity tile = world.getTileEntity(below);
			if (tile instanceof FurnaceTileEntity) {
				FurnaceTileEntity furnace = (FurnaceTileEntity) tile;
				int cookTime = furnace.cookTime;
				if (cookTime > 0 && cookTime % 3 == 0) {
					List<FoxhoundEntity> foxhounds = world.getEntitiesWithinAABB(FoxhoundEntity.class, new AxisAlignedBB(getPosition()),
							(fox) -> fox != null && fox.isTamed());
					if(!foxhounds.isEmpty() && foxhounds.get(0) == this)
						furnace.cookTime = Math.min(furnace.cookTimeTotal - 1, cookTime + 1);
				}
			}
		}
	}

	@Override
	public boolean isWet() {
		return false;
	}

	@Nonnull
	@Override
	protected ResourceLocation getLootTable() {
		return FOXHOUND_LOOT_TABLE;
	}

	protected SleepGoal sleepGoal;

	@Override
	protected void registerGoals() {
		this.sitGoal = new SitGoal(this);
		this.sleepGoal = new SleepGoal(this);
		this.goalSelector.addGoal(1, new SwimGoal(this));
		this.goalSelector.addGoal(2, this.sleepGoal);
		this.goalSelector.addGoal(3, this.sitGoal);
		this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
		this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(6, new FindPlaceToSleepGoal(this, 0.8D, true));
		this.goalSelector.addGoal(7, new FindPlaceToSleepGoal(this, 0.8D, false));
		this.goalSelector.addGoal(8, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
		this.goalSelector.addGoal(9, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(10, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(11, new BegGoal(this, 8.0F));
		this.goalSelector.addGoal(12, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.addGoal(12, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setCallsForHelp());
		this.targetSelector.addGoal(4, new NonTamedTargetGoal<>(this, AnimalEntity.class, false,
				target -> target instanceof SheepEntity || target instanceof RabbitEntity));
		this.targetSelector.addGoal(4, new NonTamedTargetGoal<>(this, PlayerEntity.class, false,
				target -> !isTamed()));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractSkeletonEntity.class, false));
	}

	@Override
	public boolean isAngry() {
		return (!isTamed() && world.getDifficulty() != Difficulty.PEACEFUL) || super.isAngry();
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (entityIn.isImmuneToFire()) {
			if (entityIn instanceof PlayerEntity)
				return false;
			return super.attackEntityAsMob(entityIn);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this).setFireDamage(),
				((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));

		if (flag) {
			entityIn.setFire(5);
			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	@Override
	public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
		setWoke();
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean processInteract(PlayerEntity player, @Nonnull Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);

		if(itemstack.getItem() == Items.BONE && !isTamed())
			return false;

		if (!this.isTamed() && !itemstack.isEmpty()) {
			if (itemstack.getItem() == Items.COAL && (world.getDifficulty() == Difficulty.PEACEFUL || player.isCreative() || player.getActivePotionEffect(Effects.FIRE_RESISTANCE) != null) && !world.isRemote) {
				if (rand.nextDouble() < FoxhoundModule.tameChance) {
					this.setTamedBy(player);
					this.navigator.clearPath();
					this.setAttackTarget(null);
					this.sitGoal.setSitting(true);
					this.setHealth(20.0F);
					this.playTameEffect(true);
					this.world.setEntityState(this, (byte)7);
				} else {
					this.playTameEffect(false);
					this.world.setEntityState(this, (byte)6);
				}

				if (!player.isCreative())
					itemstack.shrink(1);
				return true;
			}
		}

		//		if (itemstack.getItem() == Item.getItemFromBlock(TinyPotato.tiny_potato)) {
		//			this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1F, 0.5F + (float) Math.random() * 0.5F);
		//			if (!player.isCreative())
		//				itemstack.shrink(1);
		//
		//			this.timeUntilPotatoEmerges = 1201;
		//
		//			return true;
		//		}

		if (!world.isRemote) {
			setWoke();
		}

		return super.processInteract(player, hand);
	}

	@Override
	public boolean canMateWith(AnimalEntity otherAnimal) {
		return super.canMateWith(otherAnimal) && otherAnimal instanceof FoxhoundEntity;
	}

	@Override
	public WolfEntity createChild(AgeableEntity otherParent) {
		WolfEntity entitywolf = new FoxhoundEntity(FoxhoundModule.foxhoundType, this.world);
		UUID uuid = this.getOwnerId();

		if (uuid != null) {
			entitywolf.setOwnerId(uuid);
			entitywolf.setTamed(true);
		}

		return entitywolf;
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("OhLawdHeComin", timeUntilPotatoEmerges);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		timeUntilPotatoEmerges = compound.getInt("OhLawdHeComin");
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return isSleeping() ? null : super.getAmbientSound();
	}

	@Override
	public boolean isSleeping() {
		return dataManager.get(SLEEPING);
	}

	public void setSleeping(boolean sleeping) {
		dataManager.set(SLEEPING, sleeping);
	}


	public static boolean canSpawnHere(IWorld world, BlockPos pos, Random rand) {
		if (world.getLightFor(LightType.SKY, pos) > rand.nextInt(32)) {
			return false;
		} else {
			int light = world.getWorld().isThundering() ? world.getNeighborAwareLightSubtracted(pos, 10) : world.getLight(pos);
			return light <= rand.nextInt(8);
		}
	}

	@Override
	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		return worldIn.getBlockState(pos.down()).getBlock() == Blocks.NETHERRACK ? 10.0F : worldIn.getBrightness(pos) - 0.5F;
	}

	public static boolean spawnPredicate(EntityType<? extends FoxhoundEntity> type, IWorld world, SpawnReason reason, BlockPos pos, Random rand) {
		return world.getDifficulty() != Difficulty.PEACEFUL && canSpawnHere(world, pos, rand) && spawnPredicate(type, world, reason, pos, rand);
	}

	public SleepGoal getSleepGoal() {
		return sleepGoal;
	}

	private void setWoke() {
		SleepGoal sleep = getSleepGoal();
		if(sleep != null) {
			setSleeping(false);
			sleep.setSleeping(false);
		}
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
