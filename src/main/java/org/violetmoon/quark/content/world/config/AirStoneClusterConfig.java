package org.violetmoon.quark.content.world.config;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.config.type.DimensionConfig;
import org.violetmoon.quark.base.config.type.IBiomeConfig;

public class AirStoneClusterConfig extends BigStoneClusterConfig {

	@Config public boolean generateInAir = true;
	
	public AirStoneClusterConfig(DimensionConfig dimensions, int clusterSize, int sizeVariation, int rarity, int minYLevel, int maxYLevel, IBiomeConfig biomes) {
		super(dimensions, clusterSize, sizeVariation, rarity, minYLevel, maxYLevel, biomes);
	}

	
}
