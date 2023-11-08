package org.violetmoon.quark.content.world.config;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.config.type.ClusterSizeConfig;
import org.violetmoon.quark.base.config.type.CompoundBiomeConfig;
import org.violetmoon.quark.base.config.type.DimensionConfig;
import org.violetmoon.quark.base.config.type.IBiomeConfig;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BigStoneClusterConfig extends ClusterSizeConfig {

	@Config
	public boolean enabled = true;

	@SafeVarargs
	public BigStoneClusterConfig(TagKey<Biome>... tags) {
		this(DimensionConfig.overworld(false), 14, 9, 4, 20, 80, CompoundBiomeConfig.fromBiomeTags(false, tags));
	}

	public BigStoneClusterConfig(DimensionConfig dimensions, int clusterSize, int sizeVariation, int rarity, int minYLevel, int maxYLevel, IBiomeConfig biomes) {
		super(rarity, clusterSize, clusterSize, sizeVariation, sizeVariation, biomes);
		this.dimensions = dimensions;
		
		this.minYLevel = minYLevel;
		this.maxYLevel = maxYLevel;
	}
	
	public BigStoneClusterConfig setVertical(int vertical, int verticalVariation) {
		this.verticalSize = vertical;
		this.verticalVariation = verticalVariation;
		return this;
	}
	

}
