package vazkii.arl.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

public final class RotationHandler {

	private static final Rotation[] FACING_TO_ROTATION = new Rotation[] {
			Rotation.NONE,
			Rotation.NONE,
			Rotation.NONE,
			Rotation.CLOCKWISE_180,
			Rotation.COUNTERCLOCKWISE_90,
			Rotation.CLOCKWISE_90
	};

	public static Direction rotateFacing(Direction facing, Rotation rot) {
		return rot.rotate(facing);
	}

	public static Direction rotateFacing(Direction facing, Direction rot) {
		return rotateFacing(facing, getRotationFromFacing(rot));
	}

	public static Rotation getRotationFromFacing(Direction facing) {
		return FACING_TO_ROTATION[facing.ordinal()];
	}

	public static int[] applyRotation(Rotation rot, int x, int z) {
		switch(rot) {
			case CLOCKWISE_180: return new int[] { -x, -z }; 
			case CLOCKWISE_90: return new int[] { z, -x };
			case COUNTERCLOCKWISE_90: return new int[] { -z, x };
			default: return new int[] { x, z };
		}
	}

}
