package org.violetmoon.quark.content.world.config;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.config.type.CompoundBiomeConfig;
import org.violetmoon.zeta.config.type.DimensionConfig;
import org.violetmoon.zeta.config.type.IConfigType;

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
