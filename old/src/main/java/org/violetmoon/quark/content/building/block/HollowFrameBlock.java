package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.violetmoon.quark.api.ICrawlSpaceBlock;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.module.ZetaModule;

public abstract class HollowFrameBlock extends ZetaBlock implements SimpleWaterloggedBlock, ICrawlSpaceBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private static final VoxelShape SHELL = Shapes.join(Block.box(0, 0, 0, 16, 16, 16), Block.box(2, 2, 2, 14, 14, 14), BooleanOp.ONLY_FIRST);
	private static final VoxelShape SHAPE_BOTTOM = Block.box(2, 0, 2, 14, 2, 14);
	private static final VoxelShape SHAPE_TOP = Block.box(2, 14, 2, 14, 16, 14);
	private static final VoxelShape SHAPE_NORTH = Block.box(2, 2, 0, 14, 14, 2);
	private static final VoxelShape SHAPE_SOUTH = Block.box(2, 2, 14, 14, 14, 16);
	private static final VoxelShape SHAPE_WEST = Block.box(0, 2, 2, 2, 14, 14);
	private static final VoxelShape SHAPE_EAST = Block.box(14, 2, 2, 16, 14, 14);

	private static final byte FLAG_BOTTOM = 1;
	private static final byte FLAG_TOP = 1 << 1;
	private static final byte FLAG_NORTH = 1 << 2;
	private static final byte FLAG_SOUTH = 1 << 3;
	private static final byte FLAG_WEST = 1 << 4;
	private static final byte FLAG_EAST = 1 << 5;

	private static final VoxelShape[] SHAPES = new VoxelShape[1 << 6];

	static {
		for(byte shapeCode = 0; shapeCode < 1 << 6; shapeCode++) {
			VoxelShape shape = SHELL;

			if(bottom(shapeCode))
				shape = Shapes.join(shape, SHAPE_BOTTOM, BooleanOp.ONLY_FIRST);
			if(top(shapeCode))
				shape = Shapes.join(shape, SHAPE_TOP, BooleanOp.ONLY_FIRST);
			if(north(shapeCode))
				shape = Shapes.join(shape, SHAPE_NORTH, BooleanOp.ONLY_FIRST);
			if(south(shapeCode))
				shape = Shapes.join(shape, SHAPE_SOUTH, BooleanOp.ONLY_FIRST);
			if(west(shapeCode))
				shape = Shapes.join(shape, SHAPE_WEST, BooleanOp.ONLY_FIRST);
			if(east(shapeCode))
				shape = Shapes.join(shape, SHAPE_EAST, BooleanOp.ONLY_FIRST);

			SHAPES[shapeCode] = shape;
		}
	}

	public HollowFrameBlock(String regname, @Nullable ZetaModule module, Properties properties) {
		super(regname, module, properties);

		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
	}

	public static boolean bottom(byte shapeCode) {
		return (shapeCode & FLAG_BOTTOM) != 0;
	}

	public static boolean top(byte shapeCode) {
		return (shapeCode & FLAG_TOP) != 0;
	}

	public static boolean north(byte shapeCode) {
		return (shapeCode & FLAG_NORTH) != 0;
	}

	public static boolean south(byte shapeCode) {
		return (shapeCode & FLAG_SOUTH) != 0;
	}

	public static boolean west(byte shapeCode) {
		return (shapeCode & FLAG_WEST) != 0;
	}

	public static boolean east(byte shapeCode) {
		return (shapeCode & FLAG_EAST) != 0;
	}

	public static boolean hasDirection(byte shapeCode, Direction direction) {
		return switch(direction) {
		case DOWN -> bottom(shapeCode);
		case UP -> top(shapeCode);
		case NORTH -> north(shapeCode);
		case SOUTH -> south(shapeCode);
		case WEST -> west(shapeCode);
		case EAST -> east(shapeCode);
		};
	}

	public boolean hasDirection(BlockState state, Direction direction) {
		return hasDirection(getShapeCode(state), direction);
	}

	protected static byte shapeCode(boolean bottom, boolean top, boolean north, boolean south, boolean west, boolean east) {
		byte flag = 0;
		if(bottom)
			flag |= FLAG_BOTTOM;
		if(top)
			flag |= FLAG_TOP;
		if(north)
			flag |= FLAG_NORTH;
		if(south)
			flag |= FLAG_SOUTH;
		if(west)
			flag |= FLAG_WEST;
		if(east)
			flag |= FLAG_EAST;
		return flag;
	}

	protected static byte shapeCode(BlockState state, BooleanProperty bottom, BooleanProperty top, BooleanProperty north, BooleanProperty south, BooleanProperty west, BooleanProperty east) {
		return shapeCode(state.getValue(bottom), state.getValue(top), state.getValue(north), state.getValue(south), state.getValue(west), state.getValue(east));
	}

	public abstract byte getShapeCode(BlockState state);

	@Override
	public boolean canCrawl(Level level, BlockState state, BlockPos pos, Direction direction) {
		return hasDirection(state, direction.getOpposite());
	}

	@Override
	public boolean hasDynamicShape() {
		return true;
	}

	@NotNull
	@Override
	public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
		return SHAPES[getShapeCode(state)];
	}

	@NotNull
	@Override
	public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
		byte code = getShapeCode(state);
		if(ctx.isDescending()) {
			code &= ~FLAG_TOP;
			if(ctx instanceof EntityCollisionContext eCtx && eCtx.getEntity() instanceof LivingEntity living &&
					living.getY() >= pos.getY() + SHAPE_BOTTOM.max(Direction.Axis.Y))
				code &= ~FLAG_BOTTOM;
		}
		return SHAPES[code];
	}

	@Override
	public boolean isLadderZeta(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		if(entity.isVisuallyCrawling() && entity.isShiftKeyDown())
			return false;

		byte shapeCode = getShapeCode(state);
		if(!bottom(shapeCode) && !top(shapeCode))
			return false;

		Vec3 eyePos = entity.getEyePosition();
		double pad = 2.0 / 16.0;
		if(eyePos.x > (pos.getX() + pad) && eyePos.z > (pos.getZ() + pad) && eyePos.x < (pos.getX() + 1 - pad) && eyePos.z < (pos.getZ() + 1 - pad))
			return true;

		return super.isLadderZeta(state, level, pos, entity);
	}

	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
		return defaultBlockState().setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
		byte shapeCode = getShapeCode(state);
		return !state.getValue(WATERLOGGED) && bottom(shapeCode) && top(shapeCode);
	}

	@NotNull
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@NotNull
	@Override
	public BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos facingPos) {
		if(state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, facing, facingState, level, pos, facingPos);
	}

	@Override
	public boolean useShapeForLightOcclusion(@NotNull BlockState p_56967_) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(@NotNull Builder<Block, BlockState> def) {
		super.createBlockStateDefinition(def);

		def.add(WATERLOGGED);
	}
}
