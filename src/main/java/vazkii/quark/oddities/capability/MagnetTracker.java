package vazkii.quark.oddities.capability;

import java.util.Collection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import vazkii.quark.api.IMagnetTracker;

/**
 * @author WireSegal
 * Created at 4:29 PM on 3/1/20.
 */
public class MagnetTracker implements IMagnetTracker {

    private final Multimap<BlockPos, Force> forcesActing = HashMultimap.create();

    private final World world;

    public MagnetTracker(World world) {
        this.world = world;
    }

    @Override
    public Vec3i getNetForce(BlockPos pos) {
        Vec3i net = Vec3i.NULL_VECTOR;
        for (Force force : forcesActing.get(pos))
            net = force.add(net);
        return net;
    }

    @Override
    public void applyForce(BlockPos pos, int magnitude, boolean pushing, Direction dir, int distance, BlockPos origin) {
        forcesActing.put(pos, new Force(magnitude, pushing, dir, distance, origin));
    }

    @Override
    public void actOnForces(BlockPos pos) {
        Vec3i net = getNetForce(pos);

        if (net.equals(Vec3i.NULL_VECTOR))
            return;

        Direction target = Direction.getFacingFromVector(net.getX(), net.getY(), net.getZ());

        for (Force force : forcesActing.get(pos)) {
            if (force.getDirection() == target) {
                BlockState origin = world.getBlockState(force.getOrigin());
                world.addBlockEvent(force.getOrigin(), origin.getBlock(), force.isPushing() ? 0 : 1, force.getDistance());
            }
        }
    }

    @Override
    public Collection<BlockPos> getTrackedPositions() {
        return forcesActing.keySet();
    }

    @Override
    public void clear() {
        forcesActing.clear();
    }
}
