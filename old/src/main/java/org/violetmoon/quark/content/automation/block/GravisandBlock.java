package org.violetmoon.quark.content.automation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.violetmoon.zeta.util.BlockUtils;
import org.violetmoon.quark.content.automation.entity.Gravisand;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.module.ZetaModule;

public class GravisandBlock extends ZetaBlock {

	public GravisandBlock(String regname, @Nullable ZetaModule module, Properties properties) {
		super(regname, module, properties);

		if(module == null) //auto registration below this line
			return;
		setCreativeTab(CreativeModeTabs.REDSTONE_BLOCKS);
	}

	@Override
	public void onPlace(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
		checkRedstone(world, pos);
	}

	@Override
	public void neighborChanged(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
		checkRedstone(worldIn, pos);
	}

	private void checkRedstone(Level worldIn, BlockPos pos) {
		boolean powered = worldIn.hasNeighborSignal(pos);

		if(powered)
			worldIn.scheduleTick(pos, this, 2);
	}

	@Override
	public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level worldIn, @NotNull BlockPos pos) {
		return 15;
	}

	@Override
	public void tick(@NotNull BlockState state, ServerLevel worldIn, @NotNull BlockPos pos, @NotNull RandomSource rand) {
		if(!worldIn.isClientSide) {
			if(checkFallable(state, worldIn, pos))
				for(Direction face : Direction.values()) {
					BlockPos offPos = pos.relative(face);
					BlockState offState = worldIn.getBlockState(offPos);

					if(offState.getBlock() == this)
						worldIn.scheduleTick(offPos, this, 2);
				}
		}
	}

	private boolean checkFallable(BlockState state, Level worldIn, BlockPos pos) {
		if(!worldIn.isClientSide) {
			if(tryFall(state, worldIn, pos, Direction.DOWN))
				return true;
			else
				return tryFall(state, worldIn, pos, Direction.UP);
		}

		return false;
	}

	private boolean tryFall(BlockState state, Level worldIn, BlockPos pos, Direction facing) {
		BlockPos target = pos.relative(facing);
		if((worldIn.isEmptyBlock(target) || BlockUtils.canFallThrough(worldIn.getBlockState(target))) && worldIn.isInWorldBounds(pos)) {
			Gravisand entity = new Gravisand(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, facing.getStepY());
			worldIn.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
			worldIn.addFreshEntity(entity);
			return true;
		}

		return false;
	}
}
