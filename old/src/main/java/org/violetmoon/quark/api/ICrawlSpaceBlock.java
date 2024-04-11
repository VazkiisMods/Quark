package org.violetmoon.quark.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Implement on a Block to make it able to be crawled in like hollow logs.
 * Your block should be hollow internally, except on faces you
 * {@link ICrawlSpaceBlock#canCrawl(Level, BlockState, BlockPos, Direction)}.
 */
public interface ICrawlSpaceBlock {
	boolean canCrawl(Level level, BlockState state, BlockPos pos, Direction direction);

	default double crawlHeight(Level level, BlockState state, BlockPos pos, Direction direction) {
		return 0.13;
	}

	default boolean isLog(ServerPlayer sp, BlockState state, BlockPos pos, Direction direction) {
		return true;
	}
}
