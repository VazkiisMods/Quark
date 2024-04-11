package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.api.ICrawlSpaceBlock;
import org.violetmoon.zeta.block.ZetaPillarBlock;
import org.violetmoon.zeta.module.ZetaModule;

public class HollowPillarBlock extends ZetaPillarBlock implements SimpleWaterloggedBlock, ICrawlSpaceBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private static final VoxelShape SHAPE_BOTTOM = Block.box(0F, 0F, 0F, 16F, 2F, 16F);
	private static final VoxelShape SHAPE_TOP = Block.box(0F, 14F, 0F, 16F, 16F, 16F);
	private static final VoxelShape SHAPE_NORTH = Block.box(0F, 0F, 0F, 2F, 16F, 16F);
	private static final VoxelShape SHAPE_SOUTH = Block.box(14F, 0F, 0F, 16F, 16F, 16F);
	private static final VoxelShape SHAPE_EAST = Block.box(0F, 0F, 0F, 16F, 16F, 2F);
	private static final VoxelShape SHAPE_WEST = Block.box(0F, 0F, 14F, 16F, 16F, 16F);

	private static final VoxelShape SHAPE_X = Shapes.or(SHAPE_BOTTOM, SHAPE_TOP, SHAPE_EAST, SHAPE_WEST);
	private static final VoxelShape SHAPE_Y = Shapes.or(SHAPE_NORTH, SHAPE_SOUTH, SHAPE_EAST, SHAPE_WEST);
	private static final VoxelShape SHAPE_Z = Shapes.or(SHAPE_BOTTOM, SHAPE_TOP, SHAPE_NORTH, SHAPE_SOUTH);

	public HollowPillarBlock(String regname, ZetaModule module, Properties properties) {
		super(regname, module, properties);

		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
	}

	@Override
	public boolean canCrawl(Level level, BlockState state, BlockPos pos, Direction direction) {
		return state.getValue(HollowPillarBlock.AXIS) == direction.getAxis();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return switch(state.getValue(AXIS)) {
		case X -> SHAPE_X;
		case Y -> SHAPE_Y;
		case Z -> SHAPE_Z;
		};
	}

	@Override
	public boolean isLadderZeta(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		if(state.getValue(AXIS) != Axis.Y)
			return false;

		Vec3 eyePos = entity.getEyePosition();
		double pad = 2.0 / 16.0;
		if(eyePos.x > (pos.getX() + pad) && eyePos.z > (pos.getZ() + pad) && eyePos.x < (pos.getX() + 1 - pad) && eyePos.z < (pos.getZ() + 1 - pad))
			return true;

		return super.isLadderZeta(state, level, pos, entity);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
		return !state.getValue(WATERLOGGED) && state.getValue(AXIS) == Axis.Y;
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
	public boolean useShapeForLightOcclusion(BlockState p_56967_) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> def) {
		super.createBlockStateDefinition(def);

		def.add(WATERLOGGED);
	}

}
