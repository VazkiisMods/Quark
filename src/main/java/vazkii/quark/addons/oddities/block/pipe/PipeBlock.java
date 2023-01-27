package vazkii.quark.addons.oddities.block.pipe;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.quark.addons.oddities.client.render.be.PipeRenderer;
import vazkii.quark.addons.oddities.module.PipesModule;
import vazkii.quark.base.module.QuarkModule;

public class PipeBlock extends BasePipeBlock implements SimpleWaterloggedBlock {

	private static final VoxelShape CENTER_SHAPE = Shapes.box(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);

	private static final VoxelShape DOWN_SHAPE = Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final VoxelShape UP_SHAPE = Shapes.box(0.3125, 0.3125, 0.3125, 0.6875, 1, 0.6875);
	private static final VoxelShape NORTH_SHAPE = Shapes.box(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.6875);
	private static final VoxelShape SOUTH_SHAPE = Shapes.box(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 1);
	private static final VoxelShape WEST_SHAPE = Shapes.box(0, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final VoxelShape EAST_SHAPE = Shapes.box(0.3125, 0.3125, 0.3125, 1, 0.6875, 0.6875);
	
	private static final VoxelShape[] SIDE_BOXES = new VoxelShape[] {
			DOWN_SHAPE, UP_SHAPE, NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, EAST_SHAPE
	};

	private static final VoxelShape[] shapeCache = new VoxelShape[64];

	
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public PipeBlock(QuarkModule module) {
		super("pipe", module);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(handIn);
		if(stack.is(Items.GLASS) && PipesModule.enableEncasedPipes) {
			BlockEntity be = worldIn.getBlockEntity(pos);
			CompoundTag cmp = be.saveWithoutMetadata();
			
			if(!worldIn.isClientSide) {
				worldIn.removeBlockEntity(pos);
				BlockState target = ((BasePipeBlock) PipesModule.encasedPipe).getTargetState(worldIn, pos, false);
				worldIn.setBlock(pos, target, 1 | 2);
				worldIn.updateNeighborsAt(pos, PipesModule.encasedPipe);
				
				be = worldIn.getBlockEntity(pos);
				be.load(cmp);
			}
			
			SoundType type = Blocks.GLASS.defaultBlockState().getSoundType(worldIn, pos, player);
			SoundEvent sound = type.getPlaceSound();
			worldIn.playSound(player, pos, sound, SoundSource.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
			
			stack.shrink(1);
			
			return InteractionResult.SUCCESS;	
		}
		
		return super.use(state, worldIn, pos, player, handIn, hit);
	}
	
	@Override
	public BlockState getDefaultPipeState() {
		return super.getDefaultPipeState().setValue(WATERLOGGED, false);
	}
	
	@Override
	boolean isPipeWaterlogged(BlockState state) {
		return state.getValue(WATERLOGGED);
	}
	
	@Override
	protected BlockState getTargetState(Level worldIn, BlockPos pos, boolean waterlog) {
		return super.getTargetState(worldIn, pos, waterlog).setValue(WATERLOGGED, waterlog);
	}
	
	@Nonnull
	@Override
	public FluidState getFluidState(BlockState state) {
		return isPipeWaterlogged(state) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Nonnull
	@Override
	public BlockState updateShape(BlockState state, @Nonnull Direction facing, @Nonnull BlockState neighbor, @Nonnull LevelAccessor world, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
		if (isPipeWaterlogged(state)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(state, facing, neighbor, world, pos, neighborPos);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED);
	}
	
	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		int index = 0;
		for(Direction dir : Direction.values()) {
			int ord = dir.ordinal();
			if(state.getValue(CONNECTIONS[ord]))
				index += (1 << ord);
		}

		VoxelShape cached = shapeCache[index];
		if(cached == null) {
			VoxelShape currShape = CENTER_SHAPE;

			for(Direction dir : Direction.values()) {
				boolean connected = isConnected(state, dir);
				if(connected)
					currShape = Shapes.or(currShape, SIDE_BOXES[dir.ordinal()]);
			}

			shapeCache[index] = currShape;
			cached = currShape;
		}

		return cached;
	}


}
