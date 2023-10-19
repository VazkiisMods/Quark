package vazkii.quark.base.world;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.world.generator.IGenerator;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

public class WorldGenHandler {

	private static final Map<GenerationStep.Decoration, Holder<PlacedFeature>> defers = new HashMap<>();
	private static final Map<GenerationStep.Decoration, SortedSet<WeightedGenerator>> generators = new HashMap<>();

	@LoadEvent
	public static void register(ZRegister event) {
		for(GenerationStep.Decoration stage : GenerationStep.Decoration.values()) {
			Feature<NoneFeatureConfiguration> deferredFeature = new DeferredFeature(stage);

			// Always do .toLowerCase(Locale.ENGLISH) with that locale. If you leave it off, computers in
			// countries like Turkey will use a special character instead of i and well, crash the ResourceLocation.
			String name = "deferred_feature_" + stage.name().toLowerCase(Locale.ENGLISH);
			Quark.ZETA.registry.register(deferredFeature, name, Registry.FEATURE_REGISTRY);

			ConfiguredFeature<?, ?> feature = new ConfiguredFeature<>(deferredFeature, FeatureConfiguration.NONE);

			ResourceLocation resloc = new ResourceLocation(Quark.MOD_ID, "deferred_feature_" + stage.name().toLowerCase(Locale.ROOT));
			Holder<ConfiguredFeature<?, ?>> featureHolder = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, resloc, feature);

			PlacedFeature placed = new PlacedFeature(featureHolder, List.of());
			Holder<PlacedFeature> placedHolder = BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, resloc, placed);

			defers.put(stage, placedHolder);
		}
	}

	public static void modifyBiome(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder biomeInfoBuilder) {
		BiomeGenerationSettingsBuilder settings = biomeInfoBuilder.getGenerationSettings();

		for(GenerationStep.Decoration stage : GenerationStep.Decoration.values()) {
			List<Holder<PlacedFeature>> features = settings.getFeatures(stage);
			features.add(defers.get(stage));
		}
	}

	public static void addGenerator(QuarkModule module, IGenerator generator, GenerationStep.Decoration stage, int weight) {
		WeightedGenerator weighted = new WeightedGenerator(module, generator, weight);
		if(!generators.containsKey(stage))
			generators.put(stage, new TreeSet<>());

		generators.get(stage).add(weighted);
	}

	public static void generateChunk(FeaturePlaceContext<NoneFeatureConfiguration> context, GenerationStep.Decoration stage) {
		WorldGenLevel level = context.level();
		if(!(level instanceof WorldGenRegion region))
			return;

		ChunkGenerator generator = context.chunkGenerator();
		BlockPos origin = context.origin();
		BlockPos pos = new BlockPos(origin.getX(), 0, origin.getZ());
		WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(region.getSeed()));
		ChunkPos center = region.getCenter();
		long seed = random.setDecorationSeed(region.getSeed(), center.x * 16, center.z * 16);
		int stageNum = stage.ordinal() * 10000;

		if(generators.containsKey(stage)) {
			SortedSet<WeightedGenerator> set = generators.get(stage);

			for(WeightedGenerator wgen : set) {
				IGenerator gen = wgen.generator();

				if(wgen.module().enabled && gen.canGenerate(region)) {
					if(GeneralConfig.enableWorldgenWatchdog) {
						final int finalStageNum = stageNum;
						stageNum = watchdogRun(gen, () -> gen.generate(finalStageNum, seed, stage, region, generator, random, pos), 1, TimeUnit.MINUTES);
					} else stageNum = gen.generate(stageNum, seed, stage, region, generator, random, pos);
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
	
	public static void registerBiomeModifier(IEventBus bus) {
        DeferredRegister<Codec<? extends BiomeModifier>> biomeModifiers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Quark.MOD_ID);
        biomeModifiers.register(bus);
        biomeModifiers.register(QuarkBiomeModifier.RESOURCE.getPath(), QuarkBiomeModifier::makeCodec);
	}
	
	private static class QuarkBiomeModifier implements BiomeModifier {

		public static final ResourceLocation RESOURCE = new ResourceLocation(Quark.MOD_ID, "biome_modifier");
	    private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(RESOURCE, ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Quark.MOD_ID);
		
		@Override
		public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
			if(phase == Phase.ADD) {
				WorldGenHandler.modifyBiome(biome, builder);
				EntitySpawnHandler.modifyBiome(biome, builder);
			}
		}

		@Override
	    public Codec<? extends BiomeModifier> codec() {
	        return (Codec<? extends BiomeModifier>) SERIALIZER.get();
	    }

	    public static Codec<QuarkBiomeModifier> makeCodec() {
	        return Codec.unit(QuarkBiomeModifier::new);
	    }
	}

}
