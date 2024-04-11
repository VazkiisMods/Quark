package org.violetmoon.quark.base.config.type;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.base.config.definition.ConvulsionMatrixClientDefinition;
import org.violetmoon.quark.content.client.module.GreenerGrassModule;
import org.violetmoon.zeta.client.config.definition.ClientDefinitionExt;
import org.violetmoon.zeta.client.config.definition.IConfigDefinitionProvider;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.config.type.IConfigType;
import org.violetmoon.zeta.module.ZetaModule;

import com.google.common.base.Preconditions;

public class ConvulsionMatrixConfig implements IConfigType, IConfigDefinitionProvider {

	@Config
	public List<Double> r;
	@Config
	public List<Double> g;
	@Config
	public List<Double> b;

	public final Params params;

	public double[] colorMatrix;

	public ConvulsionMatrixConfig(Params params) {
		this.params = params;

		double[] defaultMatrix = params.defaultMatrix;
		this.colorMatrix = Arrays.copyOf(defaultMatrix, defaultMatrix.length);

		updateRGB();
	}

	@Override
	public void onReload(ZetaModule module, ConfigFlagManager flagManager) {
		try {
			colorMatrix = new double[] {
					r.get(0), r.get(1), r.get(2),
					g.get(0), g.get(1), g.get(2),
					b.get(0), b.get(1), b.get(2)
			};
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			colorMatrix = Arrays.copyOf(params.defaultMatrix, params.defaultMatrix.length);
		}
	}

	private void updateRGB() {
		r = Arrays.asList(colorMatrix[0], colorMatrix[1], colorMatrix[2]);
		g = Arrays.asList(colorMatrix[3], colorMatrix[4], colorMatrix[5]);
		b = Arrays.asList(colorMatrix[6], colorMatrix[7], colorMatrix[8]);
	}

	//made public static for the benefit of the config screen :/
	public static int convolve(double[] colorMatrix, int color) {
		int r = color >> 16 & 0xFF;
		int g = color >> 8 & 0xFF;
		int b = color & 0xFF;

		int outR = clamp((int) ((double) r * colorMatrix[0] + (double) g * colorMatrix[1] + (double) b * colorMatrix[2]));
		int outG = clamp((int) ((double) r * colorMatrix[3] + (double) g * colorMatrix[4] + (double) b * colorMatrix[5]));
		int outB = clamp((int) ((double) r * colorMatrix[6] + (double) g * colorMatrix[7] + (double) b * colorMatrix[8]));

		return 0xFF000000 | (((outR & 0xFF) << 16) + ((outG & 0xFF) << 8) + (outB & 0xFF));
	}

	private static int clamp(int val) {
		return Math.min(0xFF, Math.max(0, val));
	}

	public int convolve(int color) {
		return convolve(this.colorMatrix, color);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof ConvulsionMatrixConfig other && Arrays.equals(other.colorMatrix, colorMatrix));
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(colorMatrix);
	}

	@Override
	public @NotNull ClientDefinitionExt<SectionDefinition> getClientConfigDefinition(SectionDefinition def) {
		return new ConvulsionMatrixClientDefinition(this, def);
	}
	
	public static class Params {

		private static final String IDENTITY_NAME = "Vanilla";
		public static final double[] IDENTITY = {
				1, 0, 0,
				0, 1, 0,
				0, 0, 1
		};

		public final String name;
		public final String[] biomeNames;
		public final double[] defaultMatrix;
		public final int[] testColors;
		@Nullable
		public final int[] folliageTestColors;

		private final String[] presetNames;
		private final double[][] presets;

		public final Map<String, double[]> presetMap;

		public Params(String name, double[] defaultMatrix, String[] biomeNames, int[] testColors, @Nullable int[] folliageTestColors, String[] presetNames, double[][] presets) {
			Preconditions.checkArgument(defaultMatrix.length == 9);
			Preconditions.checkArgument(biomeNames.length == 6);
			Preconditions.checkArgument(testColors.length == 6);
			Preconditions.checkArgument(folliageTestColors == null || folliageTestColors.length == 6);
			Preconditions.checkArgument(presetNames.length == presets.length);

			this.name = name;
			this.defaultMatrix = defaultMatrix;
			this.biomeNames = biomeNames;
			this.testColors = testColors;
			this.folliageTestColors = folliageTestColors;

			this.presetNames = presetNames;
			this.presets = presets;

			presetMap = new LinkedHashMap<>();
			presetMap.put(IDENTITY_NAME, IDENTITY);
			for(int i = 0; i < presetNames.length; i++)
				presetMap.put(presetNames[i], presets[i]);
		}

		public Params cloneWithNewDefault(double[] newDefault) {
			return new Params(name, newDefault, biomeNames, testColors, folliageTestColors, presetNames, presets);
		}

		public boolean shouldDisplayFolliage() {
			return folliageTestColors != null && GreenerGrassModule.affectLeaves;
		}

	}

}
