package org.violetmoon.quark.base.util;

import net.minecraft.world.phys.Vec3;

/**
 * @author WireSegal
 *         Created at 11:48 AM on 9/2/19.
 */
public class MutableVectorHolder {
	public double x, y, z;

	public void importFrom(Vec3 vec) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
	}
}
