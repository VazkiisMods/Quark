package org.violetmoon.quark.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import java.util.Collection;

/**
 * @author WireSegal
 *         Created at 4:27 PM on 3/1/20.
 */
public interface IMagnetTracker {
	Vec3i getNetForce(BlockPos pos);

	void applyForce(BlockPos pos, int magnitude, boolean pushing, Direction dir, int distance, BlockPos origin);

	void actOnForces(BlockPos pos);

	Collection<BlockPos> getTrackedPositions();

	void clear();
}
