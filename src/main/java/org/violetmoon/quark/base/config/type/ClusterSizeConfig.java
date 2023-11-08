package org.violetmoon.quark.base.config.type;

import org.violetmoon.quark.base.config.Config;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class ClusterSizeConfig implements IConfigType {

	@Config
	public DimensionConfig dimensions = DimensionConfig.overworld(false);

	@Config
	public IBiomeConfig biomes;

	@Config
	@Config.Min(0)
	public int rarity;

	@Config
	public int minYLevel = 0;

	@Config
	public int maxYLevel = 64;

	@Config
	@Config.Min(0)
	public int horizontalSize;

	@Config
	@Config.Min(0)
	public int verticalSize;

	@Config
	@Config.Min(0)
	public int horizontalVariation;

	@Config
	@Config.Min(0)
	public int verticalVariation;
	
	@SafeVarargs
	public ClusterSizeConfig(int rarity, int horizontal, int vertical, int horizontalVariation, int verticalVariation, boolean isBlacklist, TagKey<Biome>... tags) {
		this(rarity, horizontal, vertical, horizontalVariation, verticalVariation, new BiomeTagConfig(isBlacklist, tags));
	}

	public ClusterSizeConfig(int rarity, int horizontal, int vertical, int horizontalVariation, int verticalVariation, IBiomeConfig biomes) {
		this.rarity = rarity;
		this.horizontalSize = horizontal;
		this.verticalSize = vertical;
		this.horizontalVariation = horizontalVariation;
		this.verticalVariation = verticalVariation;
		this.biomes = biomes;
	}
	
	public ClusterSizeConfig setYLevels(int min, int max) {
		this.minYLevel = min;
		this.maxYLevel = max;
		return this;
	}
	
}
