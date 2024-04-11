package org.violetmoon.quark.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface IMagnetMoveAction {

	void onMagnetMoved(Level world, BlockPos pos, Direction direction, BlockState state, BlockEntity tile);

}
