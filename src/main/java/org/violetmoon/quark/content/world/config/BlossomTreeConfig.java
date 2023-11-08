package org.violetmoon.quark.content.world.config;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.config.type.CompoundBiomeConfig;
import org.violetmoon.quark.base.config.type.DimensionConfig;
import org.violetmoon.quark.base.config.type.IConfigType;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BlossomTreeConfig implements IConfigType {

	@Config
	public DimensionConfig dimensions = DimensionConfig.overworld(false);
	
	@Config
	public CompoundBiomeConfig biomeConfig;
	
	@Config
	public int rarity;
	
	public BlossomTreeConfig(int rarity, TagKey<Biome> tag) {
		this.rarity = rarity;
		biomeConfig = CompoundBiomeConfig.fromBiomeTags(false, tag);
	}
	
}
