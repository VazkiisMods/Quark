package org.violetmoon.quark.base.config.type;

import java.util.Objects;

import org.violetmoon.zeta.config.Config;

public class RGBAColorConfig extends RGBColorConfig {

	@Config
	public double a;

	protected double da;

	private RGBAColorConfig(double r, double g, double b, double a) {
		super(r, g, b, a);
		this.a = a;
	}

	public static RGBAColorConfig forColor(double r, double g, double b, double a) {
		RGBAColorConfig config = new RGBAColorConfig(r, g, b, a);
		config.color = config.calculateColor();
		config.dr = r;
		config.dg = g;
		config.db = b;
		config.da = a;

		return config;
	}

	@Override
	public double getAlphaComponent() {
		return a;
	}

	@Override
	void setAlphaComponent(double c) {
		a = c;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		RGBAColorConfig that = (RGBAColorConfig) o;
		return Double.compare(that.r, r) == 0 && Double.compare(that.g, g) == 0 && Double.compare(that.b, b) == 0 && Double.compare(that.a, a) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(r, g, b, a);
	}

}
