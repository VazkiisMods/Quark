package vazkii.quark.automation.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.automation.module.GravisandModule;

import javax.annotation.Nonnull;

public class GravisandEntity extends FallingBlockEntity {

	private static final DataParameter<Float> DIRECTION = EntityDataManager.createKey(GravisandEntity.class, DataSerializers.FLOAT);

	private static final String TAG_DIRECTION = "fallDirection";

	private final BlockState fallTile = GravisandModule.gravisand.getDefaultState();

	public GravisandEntity(EntityType<? extends GravisandEntity> type, World world) {
		super(type, world);
	}

	public GravisandEntity(World world, double x, double y, double z, float direction) {
		this(GravisandModule.gravisandType, world);
		this.preventEntitySpawning = true;
		this.setPosition(x, y + (double)((1.0F - this.getHeight()) / 2.0F), z);
		this.setMotion(Vec3d.ZERO);
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
		this.setOrigin(new BlockPos(this));
		dataManager.set(DIRECTION, direction);
	}


	@Override
	protected void registerData() {
		super.registerData();

		dataManager.register(DIRECTION, 0F);
	}

	// Mostly vanilla copy but supporting directional falling
	@Override
	public void tick() {
		Vec3d pos = getPositionVec();
		if (this.fallTile.isAir(world, new BlockPos(this)) || pos.y > 300 || pos.y < -50) {
			this.remove();
		} else {
			this.prevPosX = pos.x;
			this.prevPosY = pos.y;
			this.prevPosZ = pos.z;
			Block block = this.fallTile.getBlock();
			if (this.fallTime++ == 0) {
				BlockPos blockpos = new BlockPos(this);
				if (this.world.getBlockState(blockpos).getBlock() == block) {
					this.world.removeBlock(blockpos, false);
				} else if (!this.world.isRemote) {
					this.remove();
					return;
				}
			}

			if (!this.hasNoGravity()) {
				this.setMotion(this.getMotion().add(0.0D, 0.04D * getFallDirection(), 0.0D));
			}

			this.move(MoverType.SELF, this.getMotion());
			if (!this.world.isRemote) {
				BlockPos fallTarget = new BlockPos(this);
				boolean flag = this.fallTile.getBlock() instanceof ConcretePowderBlock;
				boolean flag1 = flag && this.world.getFluidState(fallTarget).isTagged(FluidTags.WATER);
				double d0 = this.getMotion().lengthSquared();
				if (flag && d0 > 1.0D) {
					BlockRayTraceResult blockraytraceresult = this.world.rayTraceBlocks(new RayTraceContext(new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ), pos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, this));
					if (blockraytraceresult.getType() != RayTraceResult.Type.MISS && this.world.getFluidState(blockraytraceresult.getPos()).isTagged(FluidTags.WATER)) {
						fallTarget = blockraytraceresult.getPos();
						flag1 = true;
					}
				}

				if (!collidedVertically && !flag1) {
					if (!this.world.isRemote && (this.fallTime > 100 && (fallTarget.getY() < 1 || fallTarget.getY() > 256) || this.fallTime > 600)) {
						if (this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
							this.entityDropItem(block);
						}

						this.remove();
					}
				} else {
					BlockState blockstate = this.world.getBlockState(fallTarget);
					this.setMotion(this.getMotion().mul(0.7D, -0.5D, 0.7D));
					if (blockstate.getBlock() != Blocks.MOVING_PISTON) {
						this.remove();
						Direction facing = getFallDirection() < 0 ? Direction.DOWN : Direction.UP;
						boolean flag2 = blockstate.isReplaceable(new DirectionalPlaceContext(this.world, fallTarget, facing, ItemStack.EMPTY, facing.getOpposite()));
						boolean flag3 = this.fallTile.isValidPosition(this.world, fallTarget);
						if (flag2 && flag3) {
							this.world.setBlockState(fallTarget, this.fallTile, 3);
						} else if (this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
							this.entityDropItem(block);
						}
					} else if (this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
						this.entityDropItem(block);
					}
				}
			}
		}

		this.setMotion(this.getMotion().scale(0.98D));
	}

	@Override
	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	private float getFallDirection() {
		return dataManager.get(DIRECTION);
	}

	@Override
	protected void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);

		compound.putFloat(TAG_DIRECTION, getFallDirection());
	}

	@Override
	protected void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		dataManager.set(DIRECTION, compound.getFloat(TAG_DIRECTION));
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Nonnull
	@Override
	public BlockState getBlockState() {
		return fallTile;
	}

}
