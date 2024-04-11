package org.violetmoon.quark.content.world.config;

import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.config.type.ClusterSizeConfig;

public class BigStoneClusterConfig extends ClusterSizeConfig {

	@Config
	public boolean enabled;

	public BigStoneClusterConfig(Builder<?> builder) {
		super(builder);
		this.enabled = builder.enabled;
	}

	public static <B extends Builder<B>> Builder<B> stoneBuilder() {
		return new Builder<>();
	}

	public static class Builder<B extends Builder<B>> extends ClusterSizeConfig.Builder<B> {
		protected boolean enabled = true;

		public B enabled(boolean enabled) {
			this.enabled = enabled;
			return downcast();
		}

		//covariant override
		@Override
		public BigStoneClusterConfig build() {
			return new BigStoneClusterConfig(this);
		}
	}

}
