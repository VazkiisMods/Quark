package org.violetmoon.quark.content.world.module;

import org.violetmoon.quark.base.util.QuarkWorldGenWeights;
import org.violetmoon.quark.content.world.gen.ObsidianSpikeGenerator;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.config.type.DimensionConfig;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.world.WorldGenHandler;

import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

@ZetaLoadModule(category = "world")
public class NetherObsidianSpikesModule extends ZetaModule {

	@Config(description = "The chance for a chunk to contain spikes (1 is 100%, 0 is 0%)")
	public static double chancePerChunk = 0.1;

	@Config(description = "The chance for a spike to be big (1 is 100%, 0 is 0%)")
	public static double bigSpikeChance = 0.03;

	@Config(description = "Should a chunk have spikes, how many would the generator try to place")
	public static int triesPerChunk = 4;

	@Config
	public static boolean bigSpikeSpawners = true;

	@Config
	public static DimensionConfig dimensions = DimensionConfig.nether(false);

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		WorldGenHandler.addGenerator(this, new ObsidianSpikeGenerator(dimensions), Decoration.UNDERGROUND_DECORATION, QuarkWorldGenWeights.OBSIDIAN_SPIKES);
	}

}
