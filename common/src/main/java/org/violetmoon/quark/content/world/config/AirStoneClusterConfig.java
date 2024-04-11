package org.violetmoon.quark.content.world.config;

import org.violetmoon.zeta.config.Config;

public class AirStoneClusterConfig extends BigStoneClusterConfig {

	@Config
	public boolean generateInAir;

	public AirStoneClusterConfig(Builder<? extends Builder<?>> builder) {
		super(builder);
		this.generateInAir = builder.generateInAir;
	}

	public static <B extends Builder<B>> Builder<B> airStoneBuilder() {
		return new Builder<>();
	}

	public static class Builder<B extends Builder<B>> extends BigStoneClusterConfig.Builder<B> {
		boolean generateInAir = true;

		public B generateInAir(boolean generateInAir) {
			this.generateInAir = generateInAir;
			return downcast();
		}

		@Override //covariant override
		public AirStoneClusterConfig build() {
			return new AirStoneClusterConfig(this);
		}

	}

}
