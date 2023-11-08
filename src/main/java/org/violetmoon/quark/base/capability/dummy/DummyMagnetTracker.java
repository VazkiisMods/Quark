package org.violetmoon.quark.base.capability.dummy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.violetmoon.quark.api.IMagnetTracker;

import java.util.Collection;
import java.util.Collections;

/**
 * @author WireSegal
 * Created at 4:50 PM on 3/1/20.
 */
public class DummyMagnetTracker implements IMagnetTracker {
	@Override
	public Vec3i getNetForce(BlockPos pos) {
		return Vec3i.ZERO;
	}

	@Override
	public void actOnForces(BlockPos pos) {
		// NO-OP
	}

	@Override
	public void applyForce(BlockPos pos, int magnitude, boolean pushing, Direction dir, int distance, BlockPos origin) {
		// NO-OP
	}

	@Override
	public Collection<BlockPos> getTrackedPositions() {
		return Collections.emptyList();
	}

	@Override
	public void clear() {
		// NO-OP
	}
}
