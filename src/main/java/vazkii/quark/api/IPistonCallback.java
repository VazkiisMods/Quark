package vazkii.quark.api;

import net.minecraft.util.math.BlockPos;

/**
 * Implement on a TileEntity to add a callback to when it's moved by a piston.
 */
public interface IPistonCallback {

	public void onPistonMovementStarted();
	public void onPistonMovementFinished();
	
}
