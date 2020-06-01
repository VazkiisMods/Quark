/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 19:51 AM (EST)]
 */
package vazkii.quark.mobs.entity;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
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
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.mobs.ai.RaveGoal;
import vazkii.quark.mobs.module.CrabsModule;

/**
 *                 XXXXXXXXXX                            XXXXXXXXXX
 *              XXXXXXXXXXXXXXX                        XXXXXXXXXXXXXXX
 *            XXXXXXXXXXXXX XXXX                      XXXX XXXXXXXXXXXXX
 *           XXXXXXXXXXXXX XXXXX                      XXXXX XXXXXXXXXXXXX
 *          XXXXXXXXXXXXX XXXXXX                      XXXXXX XXXXXXXXXXXXX
 *         XXXXXXXXXXXXXXXXXXXXX                      XXXXXXXXXXXXXXXXXXXXX
 *        XXXXXXXXXXXXXXXXXXXXXX                      XXXXXXXXXXXXXXXXXXXXXX
 *       XXXXXXXXXXXXXXXXXXXXXX                        XXXXXXXXXXXXXXXXXXXXXX
 *       XXXXXXXXXXXXXXXXXXXXX  XXXXX            XXXXX  XXXXXXXXXXXXXXXXXXXXX
 *      XXXXXXXXXXXXXXXXXXXXX  XXXXXXXX        XXXXXXXX  XXXXXXXXXXXXXXXXXXXXX
 *      XXXXXXXXXXXXXXXXXXX    XXXXXXXX        XXXXXXXX    XXXXXXXXXXXXXXXXXXX
 *      XXXXXXXXXXXXXXXXX      XXXXXXXXX      XXXXXXXXX      XXXXXXXXXXXXXXXXX
 *      XXXXXXXXXXXXXXX          XXXXXXXXXXXXXXXXXXXX          XXXXXXXXXXXXXXX
 *      XXXXXXXXXXXX          XXXXXXXXXXXXXXXXXXXXXXXXXX          XXXXXXXXXXXX
 *      XXXXXXXXXX        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX        XXXXXXXXXX
 *       XXXXXXXXXX     XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX     XXXXXXXXXX
 *        XXXXXXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  XXXXXXXXXX
 * XXXX     XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX     XXXX
 * XXXXXX    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX    XXXXXX
 * XXXXXXXX    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX    XXXXXXXX
 *  XXXXXXXX     XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX    XXXXXXXX
 *   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *       XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *          XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 * XXXXXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  XXXXXXXXX
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 * XXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *    XXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXX
 *         XXX    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX    XXX
 *            XXXXXXXXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  XXXXXXXXXXX
 *       XXXXXXXXXXXXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  XXXXXXXXXXXXXXXX
 *    XXXXXXXXXXXXXXXXX   XXXXXXXX     XXXXXXXX     XXXXXXXX   XXXXXXXXXXXXXXXXX
 *    XXXXXXXXXXXXXX    XXXXXXXXX                    XXXXXXXXX    XXXXXXXXXXXXXX
 *     XXXXXXXXXX      XXXXXXXX                       XXXXXXXXX      XXXXXXXXXX
 *                     XXXXXXXX                        XXXXXXXX
 *                     XXXXXXXX                        XXXXXXXX
 *                      XXXXXXX                        XXXXXXX
 *                      XXXXXX                          XXXXXX
 *                      XXXXX                            XXXXX
 */
public class CrabEntity extends AnimalEntity implements IEntityAdditionalSpawnData {

	public static final ResourceLocation CRAB_LOOT_TABLE = new ResourceLocation("quark", "entities/crab");

	private static final DataParameter<Float> SIZE_MODIFIER = EntityDataManager.createKey(CrabEntity.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(CrabEntity.class, DataSerializers.VARINT);

	private static final Ingredient TEMPTATION_ITEMS = Ingredient.merge(Lists.newArrayList(
			Ingredient.fromItems(Items.WHEAT, Items.CHICKEN),
			Ingredient.fromTag(ItemTags.FISHES)
	));

	private static int lightningCooldown;

	private boolean crabRave;
    private BlockPos jukeboxPosition;

	public CrabEntity(EntityType<? extends CrabEntity> type, World worldIn) {
		this(type, worldIn, 1);
	}

	public CrabEntity(EntityType<? extends CrabEntity> type, World worldIn, float sizeModifier) {
		super(type, worldIn);
		this.setPathPriority(PathNodeType.LAVA, -1.0F);
		if (sizeModifier != 1)
			dataManager.set(SIZE_MODIFIER, sizeModifier);
	}

	public static boolean spawnPredicate(EntityType<? extends AnimalEntity> type, IWorld world, SpawnReason reason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).getMaterial() == Material.SAND && world.getLight(pos) > 8;
	}

	public static void rave(IWorld world, BlockPos pos, boolean raving) {
		for(CrabEntity crab : world.getEntitiesWithinAABB(CrabEntity.class, (new AxisAlignedBB(pos)).grow(3.0D)))
			crab.party(pos, raving);
	}

    @Override
	public float getBlockPathWeight(BlockPos pos, IWorldReader world) {
		return world.getBlockState(pos.down()).getBlock() == Blocks.SAND ? 10.0F : world.getBrightness(pos) - 0.5F;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Nonnull
	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	@Override
	protected void registerData() {
		super.registerData();

		dataManager.register(SIZE_MODIFIER, 1f);
		dataManager.register(VARIANT, -1);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return QuarkSounds.ENTITY_CRAB_IDLE;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return QuarkSounds.ENTITY_CRAB_DIE;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return QuarkSounds.ENTITY_CRAB_HURT;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntitySize size) {
		return 0.2f * size.height;
	}

	public float getSizeModifier() {
		return dataManager.get(SIZE_MODIFIER);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
		this.goalSelector.addGoal(2, new RaveGoal(this));
		this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, false, TEMPTATION_ITEMS));
		this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
	}


	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(3.0D);
		this.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(2.0D);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		return MiscUtil.isEntityInsideOpaqueBlock(this);
	}

	@Override
	public void tick() {
		super.tick();
		
		if(!world.isRemote && dataManager.get(VARIANT) == -1) {
			int variant = 0;
			if(rand.nextBoolean()) {
				variant += rand.nextInt(2) + 1;
			}
			
			dataManager.set(VARIANT, variant);
		}

		if (inWater)
			stepHeight = 1F;
		else
			stepHeight = 0.6F;

		if (lightningCooldown > 0) {
			lightningCooldown--;
			extinguish();
		}

		Vec3d pos = getPositionVec();
        if(isRaving() && (jukeboxPosition == null || jukeboxPosition.distanceSq(pos.x, pos.y, pos.z, true) > 24.0D || world.getBlockState(jukeboxPosition).getBlock() != Blocks.JUKEBOX))
        	party(null, false);
		
		if(isRaving() && world.isRemote && ticksExisted % 10 == 0) {
			BlockPos below = getPosition().down();
			BlockState belowState = world.getBlockState(below);
			if(belowState.getMaterial() == Material.SAND)
				world.playEvent(2001, below, Block.getStateId(belowState));
		}
	}

	@Nonnull
	@Override
	public EntitySize getSize(Pose poseIn) {
		return super.getSize(poseIn).scale(this.getSizeModifier());
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
	public boolean isInvulnerableTo(@Nonnull DamageSource source) {
		return super.isInvulnerableTo(source) ||
				source == DamageSource.LIGHTNING_BOLT ||
				getSizeModifier() > 1 && source.isFireDamage();
	}

	@Override
	public void onStruckByLightning(LightningBoltEntity lightningBolt) {
		if (lightningCooldown > 0 || world.isRemote)
			return;

		float sizeMod = getSizeModifier();
		if (sizeMod <= 15) {
			this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("Lightning Bonus", 0.5, Operation.ADDITION));
			this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier("Lightning Debuff", -0.05, Operation.ADDITION));
			this.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier(new AttributeModifier("Lightning Bonus", 0.125, Operation.ADDITION));
			float sizeModifier = Math.min(sizeMod + 1, 16);
			this.dataManager.set(SIZE_MODIFIER, sizeModifier);
			recalculateSize();

			lightningCooldown = 150;
		}
	}

	@Override
	public void applyEntityCollision(@Nonnull Entity entityIn) {
		if (getSizeModifier() <= 1)
			super.applyEntityCollision(entityIn);
	}

	@Override
	protected void collideWithEntity(Entity entityIn) {
		super.collideWithEntity(entityIn);
		if (world.getDifficulty() != Difficulty.PEACEFUL) {
			if (entityIn instanceof LivingEntity && !(entityIn instanceof CrabEntity))
				entityIn.attackEntityFrom(DamageSource.CACTUS, 1f);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return !stack.isEmpty() && TEMPTATION_ITEMS.test(stack);
	}

	@Nullable
	@Override
	public AgeableEntity createChild(@Nonnull AgeableEntity other) {
		return new CrabEntity(CrabsModule.crabType, world);
	}

	@Nonnull
	@Override
	protected ResourceLocation getLootTable() {
		return CRAB_LOOT_TABLE;
	}

	public int getVariant() {
		return Math.max(0, dataManager.get(VARIANT));
	}
	
	public void party(BlockPos pos, boolean isPartying) {
		// A separate method, due to setPartying being side-only.
		jukeboxPosition = pos;
		crabRave = isPartying;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
    public void setPartying(BlockPos pos, boolean isPartying) {
		party(pos, isPartying);
    }

	public boolean isRaving() {
        return crabRave;
    }

	@Override
	public void notifyDataManagerChange(@Nonnull DataParameter<?> parameter) {
		if (parameter.equals(SIZE_MODIFIER))
			recalculateSize();

		super.notifyDataManagerChange(parameter);
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

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		lightningCooldown = compound.getInt("LightningCooldown");

		if (compound.contains("EnemyCrabRating")) {
			float sizeModifier = compound.getFloat("EnemyCrabRating");
			dataManager.set(SIZE_MODIFIER, sizeModifier);
		}
		
		if(compound.contains("Variant"))
			dataManager.set(VARIANT, compound.getInt("Variant"));
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putFloat("EnemyCrabRating", getSizeModifier());
		compound.putInt("LightningCooldown", lightningCooldown);
		compound.putInt("Variant", dataManager.get(VARIANT));
	}
	
}
