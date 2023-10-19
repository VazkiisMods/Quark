package vazkii.quark.content.world.block;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IForgeShearable;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.world.module.ChorusVegetationModule;

public class ChorusVegetationBlock extends QuarkBlock implements BonemealableBlock, IForgeShearable {

	protected static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 13, 14);

	private final boolean simple;

	public ChorusVegetationBlock(String regname, QuarkModule module, boolean simple) {
		super(regname, module, CreativeModeTab.TAB_DECORATIONS,
				BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT)
				.noCollission()
				.instabreak()
				.sound(SoundType.GRASS)
				.offsetType(OffsetType.XZ)
				.randomTicks());

		this.simple = simple;
		RenderLayerHandler.setRenderType(this, RenderLayerHandler.RenderTypeSkeleton.CUTOUT);
	}

	@Override
	public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel worldIn, @Nonnull BlockPos pos, RandomSource random) {
		if(random.nextDouble() < ChorusVegetationModule.passiveTeleportChance)
			teleport(pos, random, worldIn, state);
	}

	@Override
	public void animateTick(@Nonnull BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
		worldIn.addParticle(ParticleTypes.PORTAL, pos.getX() + 0.2 + rand.nextDouble() * 0.6, pos.getY() + 0.3, pos.getZ() + 0.2 + rand.nextDouble() * 0.6, 0, 0, 0);
	}

	@Override
	public void entityInside(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull Entity entity) {
		if(simple && worldIn instanceof ServerLevel serverLevel && entity instanceof LivingEntity && !(entity instanceof EnderMan) && !(entity instanceof Endermite)) {
			BlockPos target = teleport(pos, worldIn.random, serverLevel, state);

			if(target != null && worldIn.random.nextDouble() < ChorusVegetationModule.endermiteSpawnChance) {
				Endermite mite = new Endermite(EntityType.ENDERMITE, worldIn);
				mite.setPos(target.getX(), target.getY(), target.getZ());
				worldIn.addFreshEntity(mite);
			}
		}
	}

	@Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		if(worldIn instanceof ServerLevel serverLevel)
			runAwayFromWater(pos, worldIn.random, serverLevel, state);
	}

	private void runAwayFromWater(BlockPos pos, RandomSource random, ServerLevel worldIn, BlockState state) {
		for(Direction d : Direction.values()) {
			BlockPos test = pos.relative(d);
			FluidState fluid = worldIn.getFluidState(test);
			if(fluid.getType() == Fluids.WATER || fluid.getType() == Fluids.FLOWING_WATER) {
				teleport(pos, random, worldIn, state, 8, 1);
				return;
			}
		}
	}

	private BlockPos teleport(BlockPos pos, RandomSource random, ServerLevel worldIn, BlockState state) {
		return teleport(pos, random, worldIn, state, 4, (1.0 - ChorusVegetationModule.teleportDuplicationChance));
	}

	private BlockPos teleport(BlockPos pos, RandomSource random, ServerLevel worldIn, BlockState state, int range, double growthChance) {
		int xOff;
		int zOff;
		do {
			xOff = random.nextInt(range + 1) - (range / 2);
			zOff = random.nextInt(range + 1) - (range / 2);
		} while(xOff == 0 && zOff == 0);
		BlockPos newPos = pos.offset(xOff, 10, zOff);

		for(int i = 0; i < 20; i++) {
			BlockState stateAt = worldIn.getBlockState(newPos);
			if(stateAt.getBlock() == Blocks.END_STONE)
				break;

			else newPos = newPos.below();
		}

		if(worldIn.getBlockState(newPos).getBlock() == Blocks.END_STONE && worldIn.getBlockState(newPos.above()).isAir()) {
			newPos = newPos.above();
			worldIn.setBlockAndUpdate(newPos, state);

			if(random.nextDouble() < growthChance) {
				worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				worldIn.sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() - 0.25, pos.getZ(), 50, 0.25, 0.25, 0.25, 1);
				worldIn.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 0.1F, 5F + random.nextFloat());
			}

			worldIn.sendParticles(ParticleTypes.REVERSE_PORTAL, newPos.getX() + 0.5, newPos.getY() - 0.25, newPos.getZ(), 50, 0.25, 0.25, 0.25, 0.05);

			return newPos;
		}

		return null;
	}

	@Override
	public boolean isValidBonemealTarget(@Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(@Nonnull Level worldIn, @Nonnull RandomSource rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(@Nonnull ServerLevel worldIn, RandomSource rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
		for(int i = 0; i < (3 + rand.nextInt(3)); i++)
			teleport(pos, rand, worldIn, state, 10, 0);
		teleport(pos, rand, worldIn, state, 4, 1);
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return SHAPE;
	}

	@Nonnull
	@Override
	public BlockState updateShape(BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor worldIn, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
		return !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public boolean canSurvive(@Nonnull BlockState state, LevelReader worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.below()).getBlock() == Blocks.END_STONE;
	}

	@Override
	public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull PathComputationType type) {
		return (type == PathComputationType.AIR && !this.hasCollision) || super.isPathfindable(state, worldIn, pos, type);
	}

}
