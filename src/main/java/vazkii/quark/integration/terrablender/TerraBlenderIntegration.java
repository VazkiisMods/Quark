package vazkii.quark.integration.terrablender;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.handler.UndergroundBiomeHandler;
import vazkii.quark.base.handler.UndergroundBiomeHandler.Proxy;
import vazkii.quark.base.handler.UndergroundBiomeHandler.UndergroundBiomeSkeleton;
import vazkii.zeta.event.ZLoadComplete;

public class TerraBlenderIntegration implements Supplier<UndergroundBiomeHandler.Proxy> {

	private TBProxy proxy;

	@Override
	public Proxy get() {
		if(proxy == null)
			proxy = new TBProxy();

		return proxy;
	}

	class QuarkRegion extends Region {

		public QuarkRegion() {
			super(new ResourceLocation(Quark.MOD_ID, "biome_provider"), RegionType.OVERWORLD, GeneralConfig.terrablenderRegionWeight);
		}

		@Override
		public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
			if(GeneralConfig.terrablenderRegionWeight == 0)
				return;
			
			boolean didAnything = false;

			for(UndergroundBiomeSkeleton skeleton : proxy.skeletons)
				if(skeleton.module().enabled) {
					ResourceKey<Biome> resourceKey = ResourceKey.create(Registry.BIOME_REGISTRY, skeleton.biome());
					mapper.accept(Pair.of(skeleton.climate(), resourceKey));
					didAnything = true;
				}

			if(didAnything)
				addModifiedVanillaOverworldBiomes(mapper, b -> {});
		}

	}

	class TBProxy extends UndergroundBiomeHandler.Proxy {

		@Override
		public void init(ZLoadComplete event) {
			event.enqueueWork(() -> {
				for(UndergroundBiomeSkeleton skeleton : skeletons)
					if(skeleton.module().enabled) {
						Regions.register(new QuarkRegion());
						return;
					}
			});
		}

		@Override
		public void addUndergroundBiomes(OverworldBiomeBuilder builder, Consumer<Pair<ParameterPoint, ResourceKey<Biome>>> consumer) {
			// Nothing happens here as we're using TB's methods instead
		}

	}

}
