package org.violetmoon.quark.base.config.type;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.violetmoon.quark.base.config.Config;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public class StrictBiomeConfig implements IBiomeConfig, IConfigType {

	@Config(name = "Biomes")
	private List<String> biomeStrings;

	@Config
	private boolean isBlacklist;

	protected StrictBiomeConfig(boolean isBlacklist, String... biomes) {
		this.isBlacklist = isBlacklist;

		biomeStrings = new LinkedList<>();
		biomeStrings.addAll(Arrays.asList(biomes));
	}

	@Override
	public boolean canSpawn(Holder<Biome> res) {
		return res.unwrap().map(
				key -> biomeStrings.contains(key.location().toString()) != isBlacklist,
				unbound -> false
				);
	}

}
