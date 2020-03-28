package vazkii.quark.mobs.entity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.proxy.CommonProxy;
import vazkii.quark.mobs.ai.FavorBlockGoal;
import vazkii.quark.mobs.ai.PassivePassengerGoal;
import vazkii.quark.mobs.ai.TemptGoalButNice;
import vazkii.quark.mobs.module.FrogsModule;

@SuppressWarnings("deprecation")
public class FrogEntity extends AnimalEntity implements IEntityAdditionalSpawnData, IShearable {

	public static final ResourceLocation FROG_LOOT_TABLE = new ResourceLocation("quark", "entities/frog");

	private static final DataParameter<Integer> TALK_TIME = EntityDataManager.createKey(FrogEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Float> SIZE_MODIFIER = EntityDataManager.createKey(FrogEntity.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> HAS_SWEATER = EntityDataManager.createKey(FrogEntity.class, DataSerializers.BOOLEAN);

	private static final Ingredient TEMPTATION_ITEMS = Ingredient.merge(Lists.newArrayList(
			Ingredient.fromItems(Items.SPIDER_EYE),
			Ingredient.fromTag(ItemTags.FISHES)
	));
	private static final Ingredient TEMPTATION_ITEMS_BUT_NICE = Ingredient.merge(Lists.newArrayList(
			Ingredient.fromItems(Items.SPIDER_EYE, Items.CLOCK),
			Ingredient.fromTag(ItemTags.FISHES)
	));
	
	public int spawnCd = -1;
	public int spawnChain = 30;

	public boolean isDuplicate = false;
	private boolean sweatered = false;

	public FrogEntity(EntityType<? extends FrogEntity> type, World worldIn) {
		this(type, worldIn, 1);
	}

	public FrogEntity(EntityType<? extends FrogEntity> type, World worldIn, float sizeModifier) {
		super(type, worldIn);
		if (sizeModifier != 1)
			dataManager.set(SIZE_MODIFIER, sizeModifier);

		this.jumpController = new FrogJumpController();
		this.moveController = new FrogMoveController();
		this.setMovementSpeed(0.0D);
	}

	@Override
	protected void registerData() {
		super.registerData();

		dataManager.register(TALK_TIME, 0);
		dataManager.register(SIZE_MODIFIER, 1f);
		dataManager.register(HAS_SWEATER, false);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new PassivePassengerGoal(this));
		goalSelector.addGoal(1, new SwimGoal(this));
		goalSelector.addGoal(2, new FrogPanicGoal(1.25));
		goalSelector.addGoal(3, new BreedGoal(this, 1.0));
		goalSelector.addGoal(4, new TemptGoalButNice(this, 1.2, false, TEMPTATION_ITEMS, TEMPTATION_ITEMS_BUT_NICE));
		goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
		goalSelector.addGoal(6, new FavorBlockGoal(this, 1, Blocks.LILY_PAD));
		goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1, 0.5F));
		goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 6));
		goalSelector.addGoal(9, new LookRandomlyGoal(this));
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Nonnull
	@Override
	public MovementController getMoveHelper() {
		return moveController;
	}

	@Nonnull
	@Override
	public JumpController getJumpController() {
		return jumpController;
	}

	@Override
	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntitySize size) {
		return 0.2f * size.height;
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		return MiscUtil.isEntityInsideOpaqueBlock(this);
	}

	public int getTalkTime() {
		return dataManager.get(TALK_TIME);
	}

	public float getSizeModifier() {
		return dataManager.get(SIZE_MODIFIER);
	}
	
	public static boolean canBeSweatered() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DAY_OF_MONTH) == 1;
	}

	@Override
	public void tick() {
		if(!world.isRemote && !sweatered) {
			setSweater(CommonProxy.jingleTheBells && (getUniqueID().getLeastSignificantBits() % 10 == 0));
			sweatered = true;
		}
		
		if (this.jumpTicks != this.jumpDuration) ++this.jumpTicks;
		else if (this.jumpDuration != 0) {
			this.jumpTicks = 0;
			this.jumpDuration = 0;
			this.setJumping(false);
		}

		super.tick();

		int talkTime = getTalkTime();
		if (talkTime > 0)
			dataManager.set(TALK_TIME, talkTime - 1);

		if (FrogsModule.enableBigFunny && spawnCd > 0 && spawnChain > 0) {
			spawnCd--;
			if (spawnCd == 0 && !world.isRemote) {
				float multiplier = 0.8F;
				FrogEntity newFrog = new FrogEntity(FrogsModule.frogType, world);
				Vec3d pos = getPositionVector();
				newFrog.setPosition(pos.x, pos.y, pos.z);
				newFrog.setMotion((Math.random() - 0.5) * multiplier, (Math.random() - 0.5) * multiplier, (Math.random() - 0.5) * multiplier);
				newFrog.isDuplicate = true;
				newFrog.spawnCd = 2;
				newFrog.spawnChain = spawnChain - 1;
				world.addEntity(newFrog);
				spawnChain = 0;
			}
		}

		this.prevRotationYaw = this.prevRotationYawHead;
		this.rotationYaw = this.rotationYawHead;
	}

	@Override
	protected boolean canDropLoot() {
		return !isDuplicate && super.canDropLoot();
	}

	@Nonnull
	@Override
	protected ResourceLocation getLootTable() {
		return FROG_LOOT_TABLE;
	}

	private int droppedLegs = -1;

	@Override
	protected void dropLoot(@Nonnull DamageSource source, boolean damagedByPlayer) {
		droppedLegs = 0;
		super.dropLoot(source, damagedByPlayer);
		droppedLegs = -1;
	}

	@Nullable
	@Override
	public ItemEntity entityDropItem(ItemStack stack, float offsetY) {
		if (droppedLegs >= 0) {
			int count = Math.max(4 - droppedLegs, 0);
			droppedLegs += stack.getCount();

			if (stack.getCount() > count) {
				ItemStack copy = stack.copy();
				copy.shrink(count);
				copy.getOrCreateChildTag("display")
						.putString("LocName", "item.quark.frog_maybe_leg.name");

				stack = stack.copy();
				stack.shrink(copy.getCount());

				super.entityDropItem(copy, offsetY);
			}
		}

		return super.entityDropItem(stack, offsetY);
	}

	@Override
	public boolean processInteract(PlayerEntity player, @Nonnull Hand hand) {
		if (super.processInteract(player, hand))
			return true;

		ItemStack stack = player.getHeldItem(hand);
		
		LocalDate date = LocalDate.now();
		if(DayOfWeek.from(date) == DayOfWeek.WEDNESDAY && stack.getItem() == Items.CLOCK) {
			if(!world.isRemote && spawnChain > 0 && !isDuplicate) {
				if(FrogsModule.enableBigFunny) {
					spawnCd = 50;
					dataManager.set(TALK_TIME, 80);
				}
					
				Vec3d pos = getPositionVector();
				world.playSound(null, pos.x, pos.y, pos.z, QuarkSounds.ENTITY_FROG_WEDNESDAY, SoundCategory.NEUTRAL, 1F, 1F);
			}

			return true;
		}
		
		if(stack.getItem().isIn(ItemTags.WOOL) && !hasSweater()) {
			if(!world.isRemote) {
				setSweater(true);
				Vec3d pos = getPositionVector();
				world.playSound(null, pos.x, pos.y, pos.z, SoundType.CLOTH.getPlaceSound(), SoundCategory.PLAYERS, 1F, 1F);
				stack.shrink(1);
			}
			
			player.swingArm(hand);
			return true;
		}

		return false;
	}
	
	@Override
	public boolean isShearable(ItemStack item, IWorldReader world, BlockPos pos) {
		return hasSweater();
	}
	
	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorld iworld, BlockPos pos, int fortune) {
		setSweater(false);
		Vec3d epos = getPositionVector();
		world.playSound(null, epos.x, epos.y, epos.z, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1F, 1F);
		
		return Lists.newArrayList();
	}

	@Nullable
	@Override
	public AgeableEntity createChild(@Nonnull AgeableEntity otherParent) {
		if (isDuplicate)
			return null;

		float sizeMod = getSizeModifier();
		if (otherParent instanceof FrogEntity) {
			if (((FrogEntity) otherParent).isDuplicate)
				return null;

			sizeMod += ((FrogEntity) otherParent).getSizeModifier();
			sizeMod /= 2;
		}

		double regression = rand.nextGaussian() / 20;
		regression *= Math.abs((sizeMod + regression) / sizeMod);

		return new FrogEntity(FrogsModule.frogType, world, MathHelper.clamp(sizeMod + (float) regression, 0.25f, 2.0f));
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		LocalDate date = LocalDate.now();
		return !stack.isEmpty() &&
				(FrogsModule.enableBigFunny && DayOfWeek.from(date) == DayOfWeek.WEDNESDAY ?
						TEMPTATION_ITEMS_BUT_NICE : TEMPTATION_ITEMS).test(stack);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		spawnCd = compound.getInt("Cooldown");
		if (compound.contains("Chain"))
			spawnChain = compound.getInt("Chain");
		dataManager.set(TALK_TIME, compound.getInt("DudeAmount"));

		float sizeModifier = compound.contains("FrogAmount") ? compound.getFloat("FrogAmount") : 1f;
		dataManager.set(SIZE_MODIFIER, sizeModifier);

		isDuplicate = compound.getBoolean("FakeFrog");
		
		sweatered = compound.getBoolean("SweaterComp");
		setSweater(compound.getBoolean("Sweater"));
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putFloat("FrogAmount", getSizeModifier());
		compound.putInt("Cooldown", spawnCd);
		compound.putInt("Chain", spawnChain);
		compound.putInt("DudeAmount", getTalkTime());
		compound.putBoolean("FakeFrog", isDuplicate);
		compound.putBoolean("SweaterComp", sweatered);
		compound.putBoolean("Sweater", hasSweater());
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return QuarkSounds.ENTITY_FROG_IDLE;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return QuarkSounds.ENTITY_FROG_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return QuarkSounds.ENTITY_FROG_DIE;
	}

	protected SoundEvent getJumpSound() {
		return QuarkSounds.ENTITY_FROG_JUMP;
	}
	
	public boolean hasSweater() {
		return dataManager.get(HAS_SWEATER);
	}
	
	public void setSweater(boolean sweater) {
		dataManager.set(HAS_SWEATER, sweater);
	}

	// Begin copypasta from EntityRabbit

	private int jumpTicks;
	private int jumpDuration;
	private boolean wasOnGround;
	private int currentMoveTypeDuration;

	@Override
	public void updateAITasks() {
		if (this.currentMoveTypeDuration > 0) --this.currentMoveTypeDuration;

		if (this.onGround) {
			if (!this.wasOnGround) {
				this.setJumping(false);
				this.checkLandingDelay();
			}

			FrogJumpController jumpHelper = (FrogJumpController) this.jumpController;

			if (!jumpHelper.getIsJumping()) {
				if (this.moveController.isUpdating() && this.currentMoveTypeDuration == 0) {
					Path path = this.navigator.getPath();
					Vec3d vec3d = new Vec3d(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ());

					if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength())
						vec3d = path.getPosition(this);

					this.calculateRotationYaw(vec3d.x, vec3d.z);
					this.startJumping();
				}
			} else if (!jumpHelper.canJump()) this.enableJumpControl();
		}

		this.wasOnGround = this.onGround;
	}

	@Override
	public void spawnRunningParticles() {
		// NO-OP
	}

	private void calculateRotationYaw(double x, double z) {
		Vec3d pos = getPositionVector();
		this.rotationYaw = (float) (MathHelper.atan2(z - pos.z, x - pos.x) * (180D / Math.PI)) - 90.0F;
	}

	private void enableJumpControl() {
		((FrogJumpController) this.jumpController).setCanJump(true);
	}

	private void disableJumpControl() {
		((FrogJumpController) this.jumpController).setCanJump(false);
	}

	private void updateMoveTypeDuration() {
		if (this.moveController.getSpeed() < 2.2D)
			this.currentMoveTypeDuration = 10;
		else
			this.currentMoveTypeDuration = 1;
	}

	private void checkLandingDelay() {
		this.updateMoveTypeDuration();
		this.disableJumpControl();
	}

	@Override
	public void notifyDataManagerChange(@Nonnull DataParameter<?> parameter) {
		if (parameter.equals(SIZE_MODIFIER))
			recalculateSize();

		super.notifyDataManagerChange(parameter);
	}

	@Override
	protected void jump() {
		super.jump();
		double d0 = this.moveController.getSpeed();

		if (d0 > 0.0D) {
			Vec3d motion = getMotion();
			double d1 = motion.x * motion.x + motion.z * motion.z;

			if (d1 < 0.01) this.moveRelative(0.1F, new Vec3d(0.0F, 0.0F, 1.0F));
		}

		if (!this.world.isRemote)
			this.world.setEntityState(this, (byte) 1);
	}

	public void setMovementSpeed(double newSpeed) {
		this.getNavigator().setSpeed(newSpeed);
		this.moveController.setMoveTo(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ(), newSpeed);
	}

	@Override
	public void setJumping(boolean jumping) {
		super.setJumping(jumping);

		if (jumping)
			this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
	}

	public void startJumping() {
		this.setJumping(true);
		this.jumpDuration = 10;
		this.jumpTicks = 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 1) {
			this.createRunningParticles();
			this.jumpDuration = 10;
			this.jumpTicks = 0;
		} else
			super.handleStatusUpdate(id);
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeFloat(getSizeModifier());
	}

	@Override
	public void readSpawnData(PacketBuffer buffer) {
		dataManager.set(SIZE_MODIFIER, buffer.readFloat());
	}

	public class FrogJumpController extends JumpController {
		private boolean canJump;

		public FrogJumpController() {
			super(FrogEntity.this);
		}

		public boolean getIsJumping() {
			return this.isJumping;
		}

		public boolean canJump() {
			return this.canJump;
		}

		public void setCanJump(boolean canJumpIn) {
			this.canJump = canJumpIn;
		}

		@Override
		public void tick() {
			if (this.isJumping) {
				startJumping();
				this.isJumping = false;
			}
		}
	}

	public class FrogMoveController extends MovementController {
		private double nextJumpSpeed;

		public FrogMoveController() {
			super(FrogEntity.this);
		}

		@Override
		public void tick() {
			if (onGround && !isJumping && !((FrogJumpController) jumpController).getIsJumping())
				setMovementSpeed(0.0D);
			else if (this.isUpdating()) setMovementSpeed(this.nextJumpSpeed);

			super.tick();
		}

		@Override
		public void setMoveTo(double x, double y, double z, double speedIn) {
			if (isInWater()) speedIn = 1.5D;

			super.setMoveTo(x, y, z, speedIn);

			if (speedIn > 0.0D) this.nextJumpSpeed = speedIn;
		}
	}

	public class FrogPanicGoal extends PanicGoal {

		public FrogPanicGoal(double speedIn) {
			super(FrogEntity.this, speedIn);
		}

		@Override
		public void tick() {
			super.tick();
			setMovementSpeed(this.speed);
		}
	}
}

