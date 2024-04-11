package org.violetmoon.quark.api;

/**
 * Implement or provide from a TileEntity to add a callback to when it's moved by a piston.
 *
 * You should not check for TileEntities implementing this.
 * Instead, check if they provide this as a capability.
 */
public interface IPistonCallback {

	void onPistonMovementStarted();

	default void onPistonMovementFinished() {
		// NO-OP
	}

}
