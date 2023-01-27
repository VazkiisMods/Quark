package vazkii.quark.addons.oddities.block.pipe;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import vazkii.quark.addons.oddities.block.be.PipeBlockEntity;
import vazkii.quark.addons.oddities.module.PipesModule;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

public abstract class BasePipeBlock extends QuarkBlock implements EntityBlock {

	protected static final BooleanProperty DOWN = BooleanProperty.create("down");
	protected static final BooleanProperty UP = BooleanProperty.create("up");
	protected static final BooleanProperty NORTH = BooleanProperty.create("north");
	protected static final BooleanProperty SOUTH = BooleanProperty.create("south");
	protected static final BooleanProperty WEST = BooleanProperty.create("west");
	protected static final BooleanProperty EAST = BooleanProperty.create("east");

	protected static final BooleanProperty[] CONNECTIONS = new BooleanProperty[] {
			DOWN, UP, NORTH, SOUTH, WEST, EAST
	};

	public BasePipeBlock(String name, QuarkModule module) {
		super(name, module, CreativeModeTab.TAB_REDSTONE,
				Block.Properties.of(Material.GLASS)
				.strength(3F, 10F)
				.sound(SoundType.GLASS)
				.noOcclusion());

		registerDefaultState(getDefaultPipeState());
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
	}

	public BlockState getDefaultPipeState() {
		return defaultBlockState()
				.setValue(DOWN, false).setValue(UP, false)
				.setValue(NORTH, false).setValue(SOUTH, false)
				.setValue(WEST, false).setValue(EAST, false);		
	}
	
	boolean isPipeWaterlogged(BlockState state) {
		return false;
	}

	public boolean allowsFullConnection(PipeBlockEntity.ConnectionType conn) {
		return conn.isSolid;
	}
	
	@Nonnull
	@Override
	public InteractionResult use(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand handIn, @Nonnull BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(handIn);

		// fix pipes if they're ruined
		if(stack.getItem() == Items.STICK) {
			Set<BlockPos> found = new HashSet<>();
			boolean fixedAny = false;

			Set<BlockPos> candidates = new HashSet<>();
			Set<BlockPos> newCandidates = new HashSet<>();

			candidates.add(pos);
			do {
				for(BlockPos cand : candidates) {
					for(Direction d : Direction.values()) {
						BlockPos offPos = cand.relative(d);
						BlockState offState = worldIn.getBlockState(offPos);
						if(offState.getBlock() == this && !candidates.contains(offPos) && !found.contains(offPos))
							newCandidates.add(offPos);
					}

					BlockState curr = worldIn.getBlockState(cand);
					BlockState target = getTargetState(worldIn, cand, isPipeWaterlogged(curr));
					if(!target.equals(curr)) {
						fixedAny = true;
						worldIn.setBlock(cand, target, 2 | 4);
					}
				}

				found.addAll(candidates);
				candidates = newCandidates;
				newCandidates = new HashSet<>();
			} while(!candidates.isEmpty());

			if(fixedAny)
				return InteractionResult.SUCCESS;
		}

		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState neighbor, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		if(world.getBlockEntity(pos) instanceof PipeBlockEntity tile){
			var type = tile.updateConnection(facing);
			BooleanProperty prop = CONNECTIONS[facing.ordinal()];
			return state.setValue(prop, allowsFullConnection(type));
		}
		return super.updateShape(state, facing, neighbor, world, pos, neighborPos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return getTargetState(context.getLevel(), context.getClickedPos(), context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		if(world.getBlockEntity(pos) instanceof PipeBlockEntity tile){
			Arrays.stream(Direction.values()).forEach(tile::updateConnection);
		}
		super.onPlace(state, world, pos, oldState, moving);
	}

	protected BlockState getTargetState(Level worldIn, BlockPos pos, boolean waterlog) {
		BlockState newState = defaultBlockState();

		for(Direction facing : Direction.values()) {
			BooleanProperty prop = CONNECTIONS[facing.ordinal()];
			PipeBlockEntity.ConnectionType type = PipeBlockEntity.computeConnectionTo(worldIn, pos, facing);

			newState = newState.setValue(prop, allowsFullConnection(type));
		}

		return newState;
	}

	public static boolean isConnected(BlockState state, Direction side) {
		BooleanProperty prop = CONNECTIONS[side.ordinal()];
		return state.getValue(prop);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, SOUTH, WEST, EAST);
	}

	@Override
	public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(@Nonnull BlockState blockState, Level worldIn, @Nonnull BlockPos pos) {
		BlockEntity tile = worldIn.getBlockEntity(pos);
		if(tile instanceof PipeBlockEntity pipe)
			return pipe.getComparatorOutput();
		return 0;
	}

	@Override
	public void onRemove(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		BlockEntity be = worldIn.getBlockEntity(pos);

		if(be instanceof PipeBlockEntity pipe)
			pipe.dropAllItems();

		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new PipeBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
		return createTickerHelper(type, PipesModule.blockEntityType, PipeBlockEntity::tick);
	}
}
