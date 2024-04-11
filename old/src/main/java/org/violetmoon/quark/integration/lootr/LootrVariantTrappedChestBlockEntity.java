package org.violetmoon.quark.integration.lootr;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.base.Quark;

/**
 * @author WireSegal
 *         Created at 11:34 AM on 7/3/23.
 */
public class LootrVariantTrappedChestBlockEntity extends LootrVariantChestBlockEntity {

	protected LootrVariantTrappedChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public LootrVariantTrappedChestBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		this(Quark.LOOTR_INTEGRATION.trappedChestTE(), pWorldPosition, pBlockState);
	}

	@Override
	protected void signalOpenCount(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, int prevOpenCount, int openCount) {
		super.signalOpenCount(world, pos, state, prevOpenCount, openCount);
		if(prevOpenCount != openCount) {
			Block block = state.getBlock();
			world.updateNeighborsAt(pos, block);
			world.updateNeighborsAt(pos.below(), block);
		}
	}
}
