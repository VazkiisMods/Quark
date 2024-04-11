package org.violetmoon.quark.addons.oddities.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.addons.oddities.block.be.MagnetBlockEntity;
import org.violetmoon.quark.addons.oddities.block.be.MagnetizedBlockBlockEntity;
import org.violetmoon.quark.addons.oddities.magnetsystem.MagnetSystem;
import org.violetmoon.quark.addons.oddities.module.MagnetsModule;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.List;

public class MagnetBlock extends ZetaBlock implements EntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty WAXED = BooleanProperty.create("waxed");
	public boolean waxed = false;

	public MagnetBlock(@Nullable ZetaModule module) {
		super("magnet", module, Properties.copy(Blocks.IRON_BLOCK));
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.DOWN).setValue(POWERED, false).setValue(WAXED, false));

		if(module == null) //auto registration below this line
			return;
		setCreativeTab(CreativeModeTabs.REDSTONE_BLOCKS);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
		if(stack.getHoverName().getString().equals("Q"))
			tooltip.add(Component.literal("haha yes"));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, WAXED);
	}

	@Override
	public void neighborChanged(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		boolean wasPowered = state.getValue(POWERED);
		boolean isPowered = isPowered(worldIn, pos, state.getValue(FACING));
		if(isPowered != wasPowered)
			worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, isPowered));
	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int action, int data) {
		boolean push = action == 0;
		Direction moveDir = state.getValue(FACING);
		Direction dir = push ? moveDir : moveDir.getOpposite();

		BlockPos targetPos = pos.relative(dir, data);
		BlockState targetState = world.getBlockState(targetPos);

		if(!(world.getBlockEntity(pos) instanceof MagnetBlockEntity be))
			return false;

		BlockPos endPos = targetPos.relative(moveDir);
		PushReaction reaction = MagnetSystem.getPushAction(be, targetPos, targetState, moveDir);
		if(reaction != PushReaction.IGNORE && reaction != PushReaction.DESTROY)
			return false;

		BlockEntity tilePresent = world.getBlockEntity(targetPos);
		CompoundTag tileData = new CompoundTag();
		if(tilePresent != null && !(tilePresent instanceof MagnetizedBlockBlockEntity))
			tileData = tilePresent.saveWithFullMetadata();

		BlockState setState = MagnetsModule.magnetized_block.defaultBlockState().setValue(MovingMagnetizedBlock.FACING, moveDir);
		MagnetizedBlockBlockEntity movingTile = new MagnetizedBlockBlockEntity(endPos, setState, targetState, tileData, moveDir);

		if(!world.isClientSide && reaction == PushReaction.DESTROY) {
			BlockState blockstate = world.getBlockState(endPos);
			Block.dropResources(blockstate, world, endPos, tilePresent);
		}

		if(tilePresent != null)
			tilePresent.setRemoved();

		world.setBlock(endPos, setState, 68);
		world.setBlockEntity(movingTile);

		world.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 67);

		return true;
	}

	private boolean isPowered(Level worldIn, BlockPos pos, Direction facing) {
		return worldIn.hasNeighborSignal(pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing = context.getNearestLookingDirection().getOpposite();
		return defaultBlockState().setValue(FACING, facing)
				.setValue(POWERED, isPowered(context.getLevel(), context.getClickedPos(), facing));
	}

	@NotNull
	@Override
	public BlockState rotate(@NotNull BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@NotNull
	@Override
	public BlockState mirror(@NotNull BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new MagnetBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		return createTickerHelper(type, MagnetsModule.magnetType, MagnetBlockEntity::tick);
	}

}
