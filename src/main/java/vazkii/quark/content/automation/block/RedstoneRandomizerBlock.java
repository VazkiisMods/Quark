package vazkii.quark.content.automation.block;

import java.util.EnumSet;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.automation.base.RandomizerPowerState;

/**
 * @author WireSegal
 * Created at 9:57 AM on 8/26/19.
 */

public class RedstoneRandomizerBlock extends QuarkBlock {

	protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<RandomizerPowerState> POWERED = EnumProperty.create("powered", RandomizerPowerState.class);

	public RedstoneRandomizerBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);

		registerDefaultState(defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(POWERED, RandomizerPowerState.OFF));

		RenderLayerHandler.setRenderType(this, RenderLayerHandler.RenderTypeSkeleton.CUTOUT);
	}

	@Override
	public void tick(@Nonnull BlockState state, @Nonnull ServerLevel world, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		boolean isPowered = isPowered(state);
		boolean willBePowered = shouldBePowered(world, pos, state);
		if(isPowered != willBePowered) {
			if (!willBePowered)
				state = state.setValue(POWERED, RandomizerPowerState.OFF);
			else
				state = state.setValue(POWERED, rand.nextBoolean() ? RandomizerPowerState.LEFT : RandomizerPowerState.RIGHT);

			world.setBlockAndUpdate(pos, state);
		}
	}

	protected void updateState(Level world, BlockPos pos, BlockState state) {
		boolean isPowered = isPowered(state);
		boolean willBePowered = shouldBePowered(world, pos, state);
		if (isPowered != willBePowered && !world.getBlockTicks().willTickThisTick(pos, this)) {
			TickPriority priority = isPowered ? TickPriority.VERY_HIGH : TickPriority.HIGH;

			world.scheduleTick(pos, this, 2, priority);
		}

	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return SHAPE;
	}

	@Override
	public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader world, BlockPos pos) {
		return canSupportRigidBlock(world, pos.below());
	}

	protected boolean isPowered(BlockState state) {
		return state.getValue(POWERED) != RandomizerPowerState.OFF;
	}

	@Override
	public int getDirectSignal(BlockState blockState, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
		return blockState.getSignal(blockAccess, pos, side);
	}

	@Override
	public int getSignal(BlockState blockState, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
		RandomizerPowerState powerState = blockState.getValue(POWERED);
		return switch (powerState) {
			case RIGHT -> blockState.getValue(FACING).getClockWise() == side ? 15 : 0;
			case LEFT -> blockState.getValue(FACING).getCounterClockWise() == side ? 15 : 0;
			default -> 0;
		};
	}

	@Override
	public void neighborChanged(BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
		if (state.canSurvive(world, pos))
			this.updateState(world, pos, state);
		else
			breakAndDrop(this, state, world, pos);
	}

	public static void breakAndDrop(Block block, BlockState state, Level world, BlockPos pos) {
		dropResources(state, world, pos, null);
		world.removeBlock(pos, false);

		for(Direction direction : Direction.values())
			world.updateNeighborsAt(pos.relative(direction), block);
	}

	protected boolean shouldBePowered(Level world, BlockPos pos, BlockState state) {
		return this.calculateInputStrength(world, pos, state) > 0;
	}

	protected int calculateInputStrength(Level world, BlockPos pos, BlockState state) {
		Direction face = state.getValue(FACING);
		BlockPos checkPos = pos.relative(face);
		int strength = world.getSignal(checkPos, face);
		if (strength >= 15) {
			return strength;
		} else {
			BlockState checkState = world.getBlockState(checkPos);
			return Math.max(strength, checkState.getBlock() == Blocks.REDSTONE_WIRE ? checkState.getValue(RedStoneWireBlock.POWER) : 0);
		}
	}

	@Override
	public boolean isSignalSource(@Nonnull BlockState state) {
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
		if (this.shouldBePowered(world, pos, state)) {
			world.scheduleTick(pos, this, 1);
		}
	}

	@Override
	public void onPlace(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
		notifyNeighbors(this, world, pos, state);
	}

	@Override
	public void onRemove(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (!isMoving && state.getBlock() != newState.getBlock()) {
			super.onRemove(state, world, pos, newState, false);
			notifyNeighbors(this, world, pos, state);
		}
	}

	public static void notifyNeighbors(Block block, Level world, BlockPos pos, BlockState state) {
		Direction face = state.getValue(FACING);
		BlockPos neighborPos = pos.relative(face.getOpposite());
		if (ForgeEventFactory.onNeighborNotify(world, pos, world.getBlockState(pos), EnumSet.of(face.getOpposite()), false).isCanceled())
			return;
		world.neighborChanged(neighborPos, block, pos);
		world.updateNeighborsAtExceptFromFacing(neighborPos, block, face);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		if (stateIn.getValue(POWERED) != RandomizerPowerState.OFF) {
			double x = (pos.getX() + 0.5D) + (rand.nextFloat() - 0.5D) * 0.2D;
			double y = (pos.getY() + 0.4D) + (rand.nextFloat() - 0.5D) * 0.2D;
			double z = (pos.getZ() + 0.5D) + (rand.nextFloat() - 0.5D) * 0.2D;

			worldIn.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

}
