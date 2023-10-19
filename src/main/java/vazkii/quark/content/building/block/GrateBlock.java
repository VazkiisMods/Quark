package vazkii.quark.content.building.block;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import vazkii.quark.api.ICrawlSpaceBlock;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.SimpleFluidloggedBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GrateBlock extends QuarkBlock implements SimpleFluidloggedBlock, ICrawlSpaceBlock {
	private static final VoxelShape TRUE_SHAPE = box(0, 15, 0, 16, 16, 16);
	private static final Float2ObjectArrayMap<VoxelShape> WALK_BLOCK_CACHE = new Float2ObjectArrayMap<>();

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty LAVALOGGED = BooleanProperty.create("lavalogged");

	public GrateBlock(QuarkModule module) {
		super("grate", module, CreativeModeTab.TAB_DECORATIONS,
				Block.Properties.of(Material.METAL)
						.strength(5, 10)
						.sound(SoundType.METAL)
						.noOcclusion());

		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(LAVALOGGED, false));
		RenderLayerHandler.setRenderType(this, RenderLayerHandler.RenderTypeSkeleton.CUTOUT);
	}

	private static VoxelShape createNewBox(double stepHeight) {
		return box(0, 15, 0, 16, 17 + 16 * stepHeight, 16);
	}

	@Override
	public boolean hasDynamicShape() {
		return true;
	}

	@Override
	public boolean canCrawl(Level level, BlockState state, BlockPos pos, Direction direction) {
		return true;
	}

	@Override
	public double crawlHeight(Level level, BlockState state, BlockPos pos, Direction direction) {
		return 0;
	}

	@Override
	public boolean isLog(ServerPlayer sp, BlockState state, BlockPos pos, Direction direction) {
		return false;
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return TRUE_SHAPE;
	}

	private static VoxelShape getCachedShape(float stepHeight) {
		return WALK_BLOCK_CACHE.computeIfAbsent(stepHeight, GrateBlock::createNewBox);
	}

	@Override
	public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
		if (collidingEntity instanceof Animal || collidingEntity instanceof WaterAnimal)
			if (!(collidingEntity instanceof Animal animal && animal.getLeashHolder() != null))
				return !(collidingEntity instanceof WaterAnimal waterAnimal && waterAnimal.getLeashHolder() != null);
		return false;
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		Entity entity = context instanceof EntityCollisionContext ? ((EntityCollisionContext) context).getEntity() : null;

		if (entity != null) {
			if (entity instanceof ItemEntity || entity instanceof ExperienceOrb)
				return Shapes.empty();

			boolean preventedType = entity instanceof Animal || entity instanceof WaterAnimal;
			boolean leashed = (entity instanceof Animal animal && animal.getLeashHolder() != null) ||
					(entity instanceof WaterAnimal waterAnimal && waterAnimal.getLeashHolder() != null);

			boolean onGrate = world.getBlockState(entity.blockPosition().offset(0, -1, 0)).getBlock() instanceof GrateBlock;

			if (preventedType && !leashed && !onGrate) {
				return getCachedShape(entity.getStepHeight());
			}

			return TRUE_SHAPE;
		}

		return TRUE_SHAPE;
	}

	@Nullable
	@Override
	public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
		if (entity instanceof Animal || entity instanceof WaterAnimal)
			return BlockPathTypes.DAMAGE_OTHER;
		return null;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Fluid fluidAt = context.getLevel().getFluidState(context.getClickedPos()).getType();
		BlockState state = defaultBlockState();
		return acceptsFluid(fluidAt) ? withFluid(state, fluidAt) : state;
	}

	@Override
	public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull PathComputationType path) {
		return false;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return fluidContained(state).getFluidType().getLightLevel();
	}

	@Override
	public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos) {
		return fluidContained(state) == Fluids.EMPTY;
	}

	@Override
	public boolean isValidSpawn(BlockState state, BlockGetter world, BlockPos pos, Type type, EntityType<?> entityType) {
		return false;
	}

	@Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block updatedBlock, @Nonnull BlockPos neighbor, boolean isMoving) {
		super.neighborChanged(state, level, pos, updatedBlock, neighbor, isMoving);
		if (!pos.below().equals(neighbor)) {
			BlockState neighborState = level.getBlockState(neighbor);
			if (neighborState.getFluidState().is(FluidTags.WATER) &&
					fluidContained(state).isSame(Fluids.LAVA)) {
				level.destroyBlock(pos, true);
				level.setBlock(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, neighbor, Blocks.OBSIDIAN.defaultBlockState()), 3);
				level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
			}
		}
	}

	@Nonnull
	@Override
	public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
		if (state.getValue(LAVALOGGED) && state.getValue(WATERLOGGED))
			state = withFluid(state, Fluids.WATER);

		Fluid fluid = fluidContained(state);
		if (fluid != Fluids.EMPTY)
			level.scheduleTick(pos, fluid, fluid.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, pos, facingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, LAVALOGGED);
	}

	@Nonnull
	@Override
	public FluidState getFluidState(@Nonnull BlockState state) {
		FluidState contained = fluidContained(state).defaultFluidState();
		if (contained.hasProperty(BlockStateProperties.FALLING))
			contained = contained.setValue(BlockStateProperties.FALLING, false);
		return contained;
	}

	@Override
	public boolean acceptsFluid(@Nonnull Fluid fluid) {
		return fluid == Fluids.WATER || fluid == Fluids.LAVA;
	}

	@Nonnull
	@Override
	public BlockState withFluid(@Nonnull BlockState state, @Nonnull Fluid fluid) {
		return state
				.setValue(WATERLOGGED, fluid == Fluids.WATER)
				.setValue(LAVALOGGED, fluid == Fluids.LAVA);
	}

	@Nonnull
	@Override
	public Fluid fluidContained(@Nonnull BlockState state) {
		if (state.getValue(WATERLOGGED))
			return Fluids.WATER;
		else if (state.getValue(LAVALOGGED))
			return Fluids.LAVA;
		else
			return Fluids.EMPTY;
	}
}
