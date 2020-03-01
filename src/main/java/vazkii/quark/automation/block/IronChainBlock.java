package vazkii.quark.automation.block;

import java.util.Locale;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.Module;

public class IronChainBlock extends QuarkBlock implements IWaterLoggable {

	private static final VoxelShape SHAPE = makeCuboidShape(6, 0, 6, 10, 16, 10);
	
	public static final EnumProperty<ChainType> TYPE = EnumProperty.create("type", ChainType.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public IronChainBlock(Module module) {
		super("iron_chain", module, ItemGroup.TRANSPORTATION, Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F).sound(SoundType.LANTERN));
		setDefaultState(getDefaultState().with(TYPE, ChainType.MIDDLE).with(WATERLOGGED, false));
		
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(TYPE, WATERLOGGED);
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos upPos = pos.up();
		BlockState upState = worldIn.getBlockState(upPos);
		return upState.getBlock() == this || upState.isSolidSide(worldIn, upPos, Direction.DOWN);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context)
				.with(TYPE, getChainType(context.getWorld(), context.getPos()))
				.with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if(!state.isValidPosition(worldIn, pos)) {
			worldIn.playEvent(2001, pos, Block.getStateId(worldIn.getBlockState(pos)));
			spawnDrops(state, worldIn, pos);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		} else {
			ChainType curr = state.get(TYPE);
			ChainType target = getChainType(worldIn, pos);
			if(target != curr)
				worldIn.setBlockState(pos, state.with(TYPE, target));
		}
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IFluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public BlockState updatePostPlacement(@Nonnull BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if(stateIn.get(WATERLOGGED))
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));

		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public boolean allowsMovement(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, PathType type) {
		return type == PathType.WATER && worldIn.getFluidState(pos).isTagged(FluidTags.WATER); 
	}
	
	public ChainType getChainType(World world, BlockPos pos) {
		BlockPos up = pos.up();
		BlockState state = world.getBlockState(up);
		if(hasSolidSide(state, world, up, Direction.DOWN))
			return ChainType.TOP;
		
		BlockPos down = pos.down();
		if(world.isAirBlock(down) || world.getBlockState(down).getBlock() == Blocks.WATER)
			return ChainType.BOTTOM;
		
		return ChainType.MIDDLE;
	}
	
	public enum ChainType implements IStringSerializable {
		TOP, MIDDLE, BOTTOM;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
		
	}

}
