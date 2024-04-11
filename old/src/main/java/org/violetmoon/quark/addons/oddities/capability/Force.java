package org.violetmoon.quark.addons.oddities.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

/**
 * @author WireSegal
 *         Created at 4:30 PM on 3/1/20.
 */
public record Force(int magnitude, boolean pushing, Direction direction, int distance,
		BlockPos origin) {

	public Vec3i add(Vec3i force) {
		return new Vec3i(force.getX() + direction.getStepX() * magnitude,
				force.getY() + direction.getStepY() * magnitude,
				force.getZ() + direction.getStepZ() * magnitude);
	}
}
