package org.violetmoon.quark.addons.oddities.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.violetmoon.quark.addons.oddities.block.be.MagnetBlockEntity;
import org.violetmoon.quark.addons.oddities.block.be.MagnetizedBlockBlockEntity;
import org.violetmoon.quark.addons.oddities.magnetsystem.MagnetSystem;
import org.violetmoon.quark.addons.oddities.module.MagnetsModule;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MagnetBlock extends QuarkBlock implements EntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public MagnetBlock(ZetaModule module) {
		super("magnet", module, CreativeModeTab.TAB_REDSTONE, Properties.copy(Blocks.IRON_BLOCK));
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.DOWN).setValue(POWERED, false));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
		if (stack.getHoverName().getString().equals("Q"))
			tooltip.add(Component.literal("haha yes"));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
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

		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof MagnetBlockEntity be))
			return false;

		BlockPos endPos = targetPos.relative(moveDir);
		PushReaction reaction = MagnetSystem.getPushAction(be, targetPos, targetState, moveDir);
		if (reaction != PushReaction.IGNORE && reaction != PushReaction.DESTROY)
			return false;

		BlockEntity tilePresent = world.getBlockEntity(targetPos);
		CompoundTag tileData = new CompoundTag();
		if (tilePresent != null && !(tilePresent instanceof MagnetizedBlockBlockEntity))
			tileData = tilePresent.saveWithFullMetadata();

		BlockState setState = MagnetsModule.magnetized_block.defaultBlockState().setValue(MovingMagnetizedBlock.FACING, moveDir);
		MagnetizedBlockBlockEntity movingTile = new MagnetizedBlockBlockEntity(endPos, setState, targetState, tileData, moveDir);

		if (!world.isClientSide && reaction == PushReaction.DESTROY) {
			BlockState blockstate = world.getBlockState(endPos);
			Block.dropResources(blockstate, world, endPos, tilePresent);
		}

		if (tilePresent != null)
			tilePresent.setRemoved();

		world.setBlock(endPos, setState, 68);
		world.setBlockEntity(movingTile);

		world.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 67);

		return true;
	}

	private boolean isPowered(Level worldIn, BlockPos pos, Direction facing) {
		Direction opp = facing.getOpposite();
		for(Direction direction : Direction.values())
			if(direction != facing && direction != opp && worldIn.hasSignal(pos.relative(direction), direction))
				return true;

		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing = context.getNearestLookingDirection().getOpposite();
		return defaultBlockState().setValue(FACING, facing)
				.setValue(POWERED, isPowered(context.getLevel(), context.getClickedPos(), facing));
	}

	@Nonnull
	@Override
	public BlockState rotate(@Nonnull BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Nonnull
	@Override
	public BlockState mirror(@Nonnull BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new MagnetBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
		return createTickerHelper(type, MagnetsModule.magnetType, MagnetBlockEntity::tick);
	}

}
