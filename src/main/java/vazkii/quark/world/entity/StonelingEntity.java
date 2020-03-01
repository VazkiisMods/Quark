package vazkii.quark.world.entity;

import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.extensions.IForgeWorldServer;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.base.ai.IfFlagGoal;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.ai.ActWaryGoal;
import vazkii.quark.world.ai.FavorBlockGoal;
import vazkii.quark.world.ai.RunAndPoofGoal;
import vazkii.quark.world.module.PassiveCreaturesModule;
import vazkii.quark.world.module.StonelingsModule;

public class StonelingEntity extends CreatureEntity {

	public static final ResourceLocation CARRY_LOOT_TABLE = new ResourceLocation("quark", "entities/stoneling_carry");

	private static final DataParameter<ItemStack> CARRYING_ITEM = EntityDataManager.createKey(StonelingEntity.class, DataSerializers.ITEMSTACK);
	private static final DataParameter<Byte> VARIANT = EntityDataManager.createKey(StonelingEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Float> HOLD_ANGLE = EntityDataManager.createKey(StonelingEntity.class, DataSerializers.FLOAT);

	private static final String TAG_CARRYING_ITEM = "carryingItem";
	private static final String TAG_VARIANT = "variant";
	private static final String TAG_HOLD_ANGLE = "itemAngle";
	private static final String TAG_PLAYER_MADE = "playerMade";

	private ActWaryGoal waryGoal;

	private boolean isTame;

	public StonelingEntity(EntityType<? extends StonelingEntity> type, World worldIn) {
		super(type, worldIn);
		this.setPathPriority(PathNodeType.DAMAGE_CACTUS, 1.0F);
		this.setPathPriority(PathNodeType.DANGER_CACTUS, 1.0F);
	}

	@Override
	protected void registerData() {
		super.registerData();

		dataManager.register(CARRYING_ITEM, ItemStack.EMPTY);
		dataManager.register(VARIANT, (byte) 0);
		dataManager.register(HOLD_ANGLE, 0F);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.2, 0.98F));
		goalSelector.addGoal(4, new FavorBlockGoal(this, 0.2, Tags.Blocks.ORES_DIAMOND));
		goalSelector.addGoal(3, new IfFlagGoal(new TemptGoal(this, 0.6, Ingredient.fromTag(Tags.Items.GEMS_DIAMOND), false), () -> StonelingsModule.enableDiamondHeart && !StonelingsModule.tamableStonelings));
		goalSelector.addGoal(2, new RunAndPoofGoal<>(this, PlayerEntity.class, 4, 0.5, 0.5));
		goalSelector.addGoal(1, waryGoal = new ActWaryGoal(this, 0.1, 6, () -> StonelingsModule.cautiousStonelings));
		goalSelector.addGoal(0, new IfFlagGoal(new TemptGoal(this, 0.6, Ingredient.fromTag(Tags.Items.GEMS_DIAMOND), false), () -> StonelingsModule.tamableStonelings));

	}


	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8);
		getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
	}


	@Override
	public void tick() {
		super.tick();

		if (inWater)
			stepHeight = 1F;
		else
			stepHeight = 0.6F;

		if (!world.isRemote && world.getDifficulty() == Difficulty.PEACEFUL && !isTame) {
			remove();
			for (Entity passenger : getRecursivePassengers())
				if (!(passenger instanceof PlayerEntity))
					passenger.remove();
		}

		this.prevRenderYawOffset = this.prevRotationYaw;
		this.renderYawOffset = this.rotationYaw;
	}

	@Override
	public EntityClassification getClassification(boolean forSpawnCount) {
		if (isTame)
			return EntityClassification.CREATURE;
		return EntityClassification.MONSTER;
	}

	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return !isTame;
	}
	
	@Override
	public void checkDespawn() {
		boolean wasAlive = isAlive();
		super.checkDespawn();
		if (!isAlive() && wasAlive)
			for (Entity passenger : getRecursivePassengers())
				if (!(passenger instanceof PlayerEntity))
					passenger.remove();
	}

	@Override
	protected boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(!stack.isEmpty() && stack.getItem() == Items.NAME_TAG) {
			stack.interactWithEntity(player, this, hand);
			return true;
		} else
			return super.processInteract(player, hand);
	}

	@Nonnull
	@Override
	public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
		if(hand == Hand.MAIN_HAND) {
			ItemStack playerItem = player.getHeldItem(hand);
			Vec3d pos = getPositionVector();

			if(!world.isRemote) {
				if (isPlayerMade()) {
					if (!player.isDiscrete() && !playerItem.isEmpty()) {

						EnumStonelingVariant currentVariant = getVariant();
						EnumStonelingVariant targetVariant = null;
						Block targetBlock = null;
						mainLoop: for (EnumStonelingVariant variant : EnumStonelingVariant.values()) {
							for (Block block : variant.getBlocks()) {
								if (block.asItem() == playerItem.getItem()) {
									targetVariant = variant;
									targetBlock = block;
									break mainLoop;
								}
							}
						}

						if (targetVariant != null) {
							if (world instanceof ServerWorld) {
								((ServerWorld) world).spawnParticle(ParticleTypes.HEART, pos.x, pos.y + getHeight(), pos.z, 1, 0.1, 0.1, 0.1, 0.1);
								if (targetVariant != currentVariant)
									((ServerWorld) world).spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, targetBlock.getDefaultState()), pos.x, pos.y + getHeight() / 2, pos.z, 16, 0.1, 0.1, 0.1, 0.25);
							}

							if (targetVariant != currentVariant) {
								playSound(QuarkSounds.ENTITY_STONELING_EAT, 1F, 1F);
								dataManager.set(VARIANT, targetVariant.getIndex());
							}

							playSound(QuarkSounds.ENTITY_STONELING_PURR, 1F, 1F + world.rand.nextFloat() * 1F);

							heal(1);

							if (!player.abilities.isCreativeMode)
								playerItem.shrink(1);

							return ActionResultType.SUCCESS;
						}

						return ActionResultType.PASS;
					}

					ItemStack stonelingItem = dataManager.get(CARRYING_ITEM);

					if (!stonelingItem.isEmpty() || !playerItem.isEmpty()) {
						player.setHeldItem(hand, stonelingItem.copy());
						dataManager.set(CARRYING_ITEM, playerItem.copy());

						if (playerItem.isEmpty())
							playSound(QuarkSounds.ENTITY_STONELING_GIVE, 1F, 1F);
						else playSound(QuarkSounds.ENTITY_STONELING_TAKE, 1F, 1F);
					}
				} else if (StonelingsModule.tamableStonelings && playerItem.getItem().isIn(Tags.Items.GEMS_DIAMOND)) {
					heal(8);

					setPlayerMade(true);

					playSound(QuarkSounds.ENTITY_STONELING_PURR, 1F, 1F + world.rand.nextFloat() * 1F);

					if (!player.abilities.isCreativeMode)
						playerItem.shrink(1);

					if (world instanceof ServerWorld)
						((ServerWorld) world).spawnParticle(ParticleTypes.HEART, pos.x, pos.y + getHeight(), pos.z, 4, 0.1, 0.1, 0.1, 0.1);

					return ActionResultType.SUCCESS;
				}
			}
		}

		return ActionResultType.PASS;
	}

	@Nullable
	@Override
	public ILivingEntityData onInitialSpawn(IWorld world, DifficultyInstance difficulty, SpawnReason spawnReason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compound) {
		byte variant;
		if (data instanceof EnumStonelingVariant)
			variant = ((EnumStonelingVariant) data).getIndex();
		else
			variant = (byte) world.getRandom().nextInt(EnumStonelingVariant.values().length);

		dataManager.set(VARIANT, variant);
		dataManager.set(HOLD_ANGLE, world.getRandom().nextFloat() * 90 - 45);

		if(!isTame && !world.isRemote() && world instanceof IForgeWorldServer) {
			if (ModuleLoader.INSTANCE.isModuleEnabled(PassiveCreaturesModule.class) && PassiveCreaturesModule.frogConfig.enabled && rand.nextDouble() < 0.01) {
				FrogEntity frog = new FrogEntity(PassiveCreaturesModule.frogType, world.getWorld(), 0.25f);
				Vec3d pos = getPositionVector();

				frog.setPosition(pos.x, pos.y, pos.z);
				world.addEntity(frog);
				frog.startRiding(this);
			} else {
				List<ItemStack> items = ((IForgeWorldServer) world).getWorldServer().getServer().getLootTableManager()
						.getLootTableFromLocation(CARRY_LOOT_TABLE).generate(new LootContext.Builder((ServerWorld) world).build(LootParameterSets.EMPTY));
				if (!items.isEmpty())
					dataManager.set(CARRYING_ITEM, items.get(0));
			}
		}

		return super.onInitialSpawn(world, difficulty, spawnReason, data, compound);
	}


	@Override
	public boolean isInvulnerableTo(@Nonnull DamageSource source) {
		return source == DamageSource.CACTUS || source.isProjectile() || super.isInvulnerableTo(source);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}


	@Override
	public boolean isNotColliding(IWorldReader worldReader) {
		return worldReader.checkNoEntityCollision(this, VoxelShapes.create(getBoundingBox()));
	}

	@Override
	public double getMountedYOffset() {
		return this.getHeight();
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	@Override
	protected int decreaseAirSupply(int air) {
		return air;
	}

	@Override
	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	@Override
	protected void damageEntity(@Nonnull DamageSource damageSrc, float damageAmount) {
		super.damageEntity(damageSrc, damageAmount);

		if(!isPlayerMade() && damageSrc.getTrueSource() instanceof PlayerEntity) {
			startle();
			for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(this,
					getBoundingBox().grow(16))) {
				if (entity instanceof StonelingEntity) {
					StonelingEntity stoneling = (StonelingEntity) entity;
					if (!stoneling.isPlayerMade() && stoneling.getEntitySenses().canSee(this)) {
						startle();
					}
				}
			}
		}
	}

	public boolean isStartled() {
		return waryGoal.isStartled();
	}

	public void startle() {
		waryGoal.startle();
		Set<PrioritizedGoal> entries = Sets.newHashSet(goalSelector.goals);

		for (PrioritizedGoal task : entries)
			if (task.getGoal() instanceof TemptGoal)
				goalSelector.removeGoal(task.getGoal());
	}

	@Override
	protected void dropSpecialItems(DamageSource damage, int looting, boolean wasRecentlyHit) {
		super.dropSpecialItems(damage, looting, wasRecentlyHit);

		ItemStack stack = getCarryingItem();
		if(!stack.isEmpty())
			entityDropItem(stack, 0F);
	}

	public void setPlayerMade(boolean value) {
		isTame = value;
	}

	public ItemStack getCarryingItem() {
		return dataManager.get(CARRYING_ITEM);
	}

	public EnumStonelingVariant getVariant() {
		return EnumStonelingVariant.byIndex(dataManager.get(VARIANT));
	}

	public float getItemAngle() {
		return dataManager.get(HOLD_ANGLE);
	}

	public boolean isPlayerMade() {
		return isTame;
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		if(compound.contains(TAG_CARRYING_ITEM, 10)) {
			CompoundNBT itemCmp = compound.getCompound(TAG_CARRYING_ITEM);
			ItemStack stack = ItemStack.read(itemCmp);
			dataManager.set(CARRYING_ITEM, stack);
		}

		dataManager.set(VARIANT, compound.getByte(TAG_VARIANT));
		dataManager.set(HOLD_ANGLE, compound.getFloat(TAG_HOLD_ANGLE));
		setPlayerMade(compound.getBoolean(TAG_PLAYER_MADE));
	}

	@Override
	public boolean canEntityBeSeen(Entity entityIn) {
		Vec3d pos = getPositionVector();
		Vec3d epos = entityIn.getPositionVec();
		
		Vec3d origin = new Vec3d(pos.x, pos.y + getEyeHeight(), pos.z);
		float otherEyes = entityIn.getEyeHeight();
		for (float height = 0; height <= otherEyes; height += otherEyes / 8) {
			if (this.world.rayTraceBlocks(new RayTraceContext(origin, epos.add(0, height, 0), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() == RayTraceResult.Type.MISS)
				return true;
		}

		return false;
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);

		compound.put(TAG_CARRYING_ITEM, getCarryingItem().serializeNBT());

		compound.putByte(TAG_VARIANT, getVariant().getIndex());
		compound.putFloat(TAG_HOLD_ANGLE, getItemAngle());
		compound.putBoolean(TAG_PLAYER_MADE, isPlayerMade());
	}


	public static boolean validLight(IWorld world, BlockPos pos, Random rand) {
		if (world.getLightFor(LightType.SKY, pos) > rand.nextInt(32)) {
			return false;
		} else {
			int light = world.getWorld().isThundering() ? world.getNeighborAwareLightSubtracted(pos, 10) : world.getLight(pos);
			return light <= rand.nextInt(8);
		}
	}

	public static boolean spawnPredicate(EntityType<? extends StonelingEntity> type, IWorld world, SpawnReason reason, BlockPos pos, Random rand) {
		return world.getDifficulty() != Difficulty.PEACEFUL && pos.getY() <= StonelingsModule.maxYLevel && validLight(world, pos, rand) && validLocation(type, world, reason, pos);
	}

	public static boolean validLocation(@Nonnull EntityType<? extends MobEntity> type, @Nonnull IWorld world, SpawnReason reason, BlockPos pos) {
		BlockPos below = pos.down();
		if (reason == SpawnReason.SPAWNER)
			return true;
		BlockState state = world.getBlockState(below);
		return state.getMaterial() == Material.ROCK && state.canEntitySpawn(world, below, type);
	}

	@Override
	public boolean canSpawn(@Nonnull IWorld world, SpawnReason reason) {
		BlockState state = world.getBlockState((new BlockPos(this)).down());
		if (state.getMaterial() != Material.ROCK)
			return false;
		return StonelingsModule.dimensions.canSpawnHere(world) && super.canSpawn(world, reason);
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return QuarkSounds.ENTITY_STONELING_CRY;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return QuarkSounds.ENTITY_STONELING_DIE;
	}

	@Override
	public int getTalkInterval() {
		return 1200;
	}

	@Override
	public void playAmbientSound() {
		SoundEvent sound = this.getAmbientSound();

		if (sound != null) this.playSound(sound, this.getSoundVolume(), 1f);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (hasCustomName()) {
			String customName = getName().getString();
			if (customName.equalsIgnoreCase("michael stevens") || customName.equalsIgnoreCase("vsauce"))
				return QuarkSounds.ENTITY_STONELING_MICHAEL;
		}

		return null;
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public float getBlockPathWeight(BlockPos pos, IWorldReader world) {
		return 0.5F - world.getBrightness(pos);
	}
}
