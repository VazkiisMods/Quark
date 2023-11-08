package org.violetmoon.quark.base.config.type;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.config.ConfigFlagManager;
import org.violetmoon.zeta.module.ZetaModule;

public class BiomeTagConfig implements IBiomeConfig, IConfigType {

	private final Object mutex = new Object();

	@Config(name = "Biome Tags")
	private List<String> biomeTagStrings;

	@Config
	private boolean isBlacklist;

	private List<TagKey<Biome>> tags;

	@SafeVarargs
	protected BiomeTagConfig(boolean isBlacklist, TagKey<Biome>... tagsIn) {
		this.isBlacklist = isBlacklist;

		biomeTagStrings = new LinkedList<>();
		for(TagKey<Biome> t : tagsIn)
			biomeTagStrings.add(t.location().toString());
	}

	private BiomeTagConfig(boolean isBlacklist, String... types) {
		this.isBlacklist = isBlacklist;

		biomeTagStrings = new LinkedList<>();
		biomeTagStrings.addAll(Arrays.asList(types));
	}

	protected static BiomeTagConfig fromStrings(boolean isBlacklist, String... types) {
		return new BiomeTagConfig(isBlacklist, types);
	}

	@Override
	public boolean canSpawn(Holder<Biome> biome) {
		if(biome == null)
			return false;

		synchronized (mutex) {
			if(tags == null)
				updateTypes();

			for(TagKey<Biome> tag : tags) {
				if(biome.is(tag))
					return !isBlacklist;
			}

			return isBlacklist;
		}
	}

	@Override
	public void onReload(ZetaModule module, ConfigFlagManager flagManager) {
		synchronized (mutex) {
			updateTypes();
		}
	}

	public void updateTypes() {
		tags = new LinkedList<>();
		for (String s : biomeTagStrings) {
			TagKey<Biome> tag = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(s));

			if(tag != null)
				tags.add(tag);
		}
	}

}
