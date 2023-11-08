package org.violetmoon.quark.content.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

import java.awt.*;
import java.util.List;

public class MyaliteColorLogic {
	protected static final float s = 0.7f;
	protected static final float b = 0.8f;
	protected static final PerlinSimplexNoise noise = new PerlinSimplexNoise(new LegacyRandomSource(4543543), List.of(-4, -3, -2, -1, 0, 1, 2, 3, 4));

	public static int getColor(BlockPos pos) {
		final float sp = 0.15f;
		final double range = 0.3;
		final double shift = 0.05;

		if(pos == null)
			pos = BlockPos.ZERO;

		float x = pos.getX() * sp;
		float y = pos.getY() * sp;
		float z = pos.getZ() * sp;

		double xv = x + Mth.sin(z) * 2;
		double zv = z + Mth.cos(x) * 2;
		double yv = y + Mth.sin(y + Mth.PI / 4) * 2;

		double noiseVal = noise.getValue(xv + yv, zv + (yv * 2), false);

		double h = noiseVal * (range / 2) - range + shift;

		return Color.HSBtoRGB((float) h, s, b);
	}

}
