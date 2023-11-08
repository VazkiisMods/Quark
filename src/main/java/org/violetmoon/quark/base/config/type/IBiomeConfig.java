package org.violetmoon.quark.base.config.type;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public interface IBiomeConfig {

	boolean canSpawn(Holder<Biome> b);

}
