package vazkii.quark.base.capability.dummy;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import vazkii.quark.api.IMagnetTracker;

/**
 * @author WireSegal
 * Created at 4:50 PM on 3/1/20.
 */
public class DummyMagnetTracker implements IMagnetTracker {
    @Override
    public Vec3i getNetForce(BlockPos pos) {
        return Vec3i.NULL_VECTOR;
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
