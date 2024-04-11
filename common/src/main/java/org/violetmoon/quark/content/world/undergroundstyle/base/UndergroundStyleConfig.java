package org.violetmoon.quark.content.world.undergroundstyle.base;

import org.violetmoon.zeta.config.type.ClusterSizeConfig;

public class UndergroundStyleConfig extends ClusterSizeConfig {

	public final UndergroundStyle style;

	public UndergroundStyleConfig(Builder<?> builder) {
		super(builder);
		this.style = builder.style;
	}

	public static <B extends Builder<B>> Builder<B> styleBuilder() {
		return new Builder<>();
	}

	public static class Builder<B extends Builder<B>> extends ClusterSizeConfig.Builder<B> {
		protected UndergroundStyle style;

		public Builder<B> style(UndergroundStyle style) {
			this.style = style;
			return this;
		}

		@Override //covariant override
		public UndergroundStyleConfig build() {
			return new UndergroundStyleConfig(this);
		}
	}

}
