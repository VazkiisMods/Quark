package org.violetmoon.quark.content.world.config;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.config.type.DimensionConfig;
import org.violetmoon.quark.base.config.type.IConfigType;
import org.violetmoon.quark.base.config.type.OrePocketConfig;

public class StoneTypeConfig implements IConfigType {

	@Config public DimensionConfig dimensions;
	@Config public OrePocketConfig oregenLower = new OrePocketConfig(0, 60, 64, 2.0);
	@Config public OrePocketConfig oregenUpper = new OrePocketConfig(64, 128, 64, 0.1666666);

	public StoneTypeConfig(DimensionConfig config) {
		dimensions = config;
	}
	
	public StoneTypeConfig() {
		this(DimensionConfig.overworld(false));
	}

}
