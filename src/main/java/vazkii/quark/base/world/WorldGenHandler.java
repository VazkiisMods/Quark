package vazkii.quark.base.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.world.generator.IGenerator;

public class WorldGenHandler {

	private static Map<GenerationStage.Decoration, SortedSet<WeightedGenerator>> generators = new HashMap<>();

	public static void loadComplete() {
		for(GenerationStage.Decoration stage : GenerationStage.Decoration.values()) {
			ConfiguredFeature<?, ?> feature = new DeferedFeature(stage).withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(new ChunkCornerPlacement().configure(NoPlacementConfig.NO_PLACEMENT_CONFIG));
			ForgeRegistries.BIOMES.forEach(biome -> biome.addFeature(stage, feature));
		}
	}
	
	public static void addGenerator(Module module, IGenerator generator, GenerationStage.Decoration stage, int weight) {
		WeightedGenerator weighted = new WeightedGenerator(module, generator, weight);
		if(!generators.containsKey(stage))
			generators.put(stage, new TreeSet<>());

		generators.get(stage).add(weighted);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void conditionalizeFeatures(GenerationStage.Decoration stage, BiPredicate<Feature<? extends IFeatureConfig>, IFeatureConfig> pred, BooleanSupplier condition) {
		ForgeRegistries.BIOMES.forEach(b -> {
			List<ConfiguredFeature<?, ?>> features = b.getFeatures(stage);

			for(int i = 0; i < features.size(); i++) {
				ConfiguredFeature<?, ?> configuredFeature = features.get(i);

				if(!(configuredFeature instanceof ConditionalConfiguredFeature)) {
					Feature<?> feature = configuredFeature.feature;
					IFeatureConfig config = configuredFeature.config;

					if(config instanceof DecoratedFeatureConfig) {
						DecoratedFeatureConfig dconfig = (DecoratedFeatureConfig) config;
						feature = dconfig.feature.feature;
						config = dconfig.feature.config;
					}

					if(pred.test(feature, config)) {
						ConditionalConfiguredFeature conditional = new ConditionalConfiguredFeature(configuredFeature, condition);
						features.set(i, conditional);
					}
				}
			}
		});
	}

	public static void generateChunk(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, BlockPos pos, GenerationStage.Decoration stage) {
		if(!(worldIn instanceof WorldGenRegion))
			return;

		WorldGenRegion region = (WorldGenRegion) worldIn;
		SharedSeedRandom random = new SharedSeedRandom();
		long seed = random.setDecorationSeed(region.getSeed(), region.getMainChunkX() * 16, region.getMainChunkZ() * 16);
		int stageNum = stage.ordinal() * 10000;

		if(generators.containsKey(stage)) {
			SortedSet<WeightedGenerator> set = generators.get(stage);

			for(WeightedGenerator wgen : set) {
				IGenerator gen = wgen.generator;

				if(wgen.module.enabled && gen.canGenerate(worldIn)) {
					if(GeneralConfig.enableWorldgenWatchdog) {
						final int finalStageNum = stageNum;
						stageNum = watchdogRun(gen, () -> gen.generate(finalStageNum, seed, stage, worldIn, generator, random, pos), 1, TimeUnit.MINUTES);
					} else stageNum = gen.generate(stageNum, seed, stage, worldIn, generator, random, pos);
				}
			}
		}
	}
	
	private static int watchdogRun(IGenerator gen, Callable<Integer> run, int time, TimeUnit unit) {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		Future<Integer> future = exec.submit(run);
		exec.shutdown();
		
		try {
			return future.get(time, unit);
		} catch(Exception e) {
			throw new RuntimeException("Error generating " + gen, e);
		} 
	}

}
