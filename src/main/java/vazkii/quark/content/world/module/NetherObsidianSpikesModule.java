package vazkii.quark.content.world.module;

import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.world.WorldGenHandler;
import vazkii.quark.base.world.WorldGenWeights;
import vazkii.quark.content.world.gen.ObsidianSpikeGenerator;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "world")
public class NetherObsidianSpikesModule extends QuarkModule {

	@Config(description = "The chance for a chunk to contain spikes (1 is 100%, 0 is 0%)")
	public static double chancePerChunk = 0.1;
	
	@Config(description = "The chance for a spike to be big (1 is 100%, 0 is 0%)")
	public static double bigSpikeChance = 0.03;

	@Config(description = "Should a chunk have spikes, how many would the generator try to place")
	public static int triesPerChunk = 4;
	
	@Config public static boolean bigSpikeSpawners = true;
	
	@Config public static DimensionConfig dimensions = DimensionConfig.nether(false);
	
	@LoadEvent
	public final void setup(ZCommonSetup event) {
		WorldGenHandler.addGenerator(this, new ObsidianSpikeGenerator(dimensions), Decoration.UNDERGROUND_DECORATION, WorldGenWeights.OBSIDIAN_SPIKES);
	}
	
}
