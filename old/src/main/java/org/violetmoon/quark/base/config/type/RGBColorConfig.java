package org.violetmoon.quark.base.config.type;

import net.minecraft.util.Mth;

import org.jetbrains.annotations.NotNull;
import org.violetmoon.quark.base.config.definition.RGBClientDefinition;
import org.violetmoon.zeta.client.config.definition.ClientDefinitionExt;
import org.violetmoon.zeta.client.config.definition.IConfigDefinitionProvider;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.config.type.IConfigType;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.Objects;

public class RGBColorConfig implements IConfigType, IConfigDefinitionProvider {

	@Config
	public double r;
	@Config
	public double g;
	@Config
	public double b;

	protected double dr, dg, db;
	protected int color;

	private RGBColorConfig(double r, double g, double b) {
		this(r, g, b, 1);
	}

	RGBColorConfig(double r, double g, double b, double a) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public static RGBColorConfig forColor(double r, double g, double b) {
		RGBColorConfig config = new RGBColorConfig(r, g, b);
		config.color = config.calculateColor();
		config.dr = r;
		config.dg = g;
		config.db = b;

		return config;
	}

	public int getColor() {
		return color;
	}

	public double getElement(int idx) {
		return switch(idx) {
		case 0 -> r;
		case 1 -> g;
		case 2 -> b;
		case 3 -> getAlphaComponent();
		default -> 0f;
		};
	}

	public void setElement(int idx, double c) {
		switch(idx) {
		case 0 -> r = c;
		case 1 -> g = c;
		case 2 -> b = c;
		case 3 -> setAlphaComponent(c);
		};

		color = calculateColor();
	}

	@Override
	public void onReload(ZetaModule module, ConfigFlagManager flagManager) {
		color = calculateColor();
	}

	int calculateColor() {
		int rComponent = clamp(r * 255) << 16;
		int gComponent = clamp(g * 255) << 8;
		int bComponent = clamp(b * 255);
		int aComponent = clamp(getAlphaComponent() * 255) << 24;
		return aComponent | bComponent | gComponent | rComponent;
	}

	double getAlphaComponent() {
		return 1.0;
	}

	void setAlphaComponent(double c) {
		// NO-OP
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		RGBColorConfig that = (RGBColorConfig) o;
		return Double.compare(that.r, r) == 0 && Double.compare(that.g, g) == 0 && Double.compare(that.b, b) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(r, g, b);
	}

	private static int clamp(double val) {
		return clamp((int) val);
	}

	private static int clamp(int val) {
		return Mth.clamp(val, 0, 0xFF);
	}

	@Override
	public @NotNull ClientDefinitionExt<SectionDefinition> getClientConfigDefinition(SectionDefinition parent) {
		return new RGBClientDefinition(parent);
	}

}
