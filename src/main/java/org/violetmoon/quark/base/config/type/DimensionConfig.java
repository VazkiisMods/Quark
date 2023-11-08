package org.violetmoon.quark.base.config.type;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.violetmoon.quark.base.config.Config;

public class DimensionConfig implements IConfigType {

	@Config
	private boolean isBlacklist;
	@Config
	private List<String> dimensions;

	public DimensionConfig(boolean blacklist, String... dims) {
		isBlacklist = blacklist;

		dimensions = new LinkedList<>();
		Collections.addAll(dimensions, dims);
	}

	public static DimensionConfig overworld(boolean blacklist) {
		return new DimensionConfig(blacklist, "minecraft:overworld");
	}

	public static DimensionConfig nether(boolean blacklist) {
		return new DimensionConfig(blacklist, "minecraft:the_nether");
	}

	public static DimensionConfig end(boolean blacklist) {
		return new DimensionConfig(blacklist, "minecraft:the_end");
	}

	public static DimensionConfig all() {
		return new DimensionConfig(true);
	}

	public boolean canSpawnHere(LevelAccessor world) {
		if (!(world instanceof Level level))
			return false;

		return canSpawnHere(level.dimension().location());
	}

	public boolean canSpawnHere(ResourceLocation resloc) {
		 return dimensions.contains(resloc.toString()) != isBlacklist;
	}

}
