package org.violetmoon.quark.integration.terrablender;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.QuarkGeneralConfig;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;

import java.util.function.Consumer;

import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;

public class TerrablenderUndergroundBiomeHandler extends AbstractUndergroundBiomeHandler {

	public TerrablenderUndergroundBiomeHandler() {
		Quark.LOG.info("Initializing TerraBlender underground biome compat");
		Quark.ZETA.loadBus.subscribe(this);
	}

	@LoadEvent
	public void commonSetup(ZCommonSetup event) {
		event.enqueueWork(() -> {
			if(undergroundBiomeDescs.isEmpty() || !QuarkGeneralConfig.terrablenderAddRegion)
				return; // No need to register a region.

			Regions.register(new Region(Quark.asResource("biome_provider"), RegionType.OVERWORLD, QuarkGeneralConfig.terrablenderRegionWeight) {
				@Override
				public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {
					// Quark's region is the same as vanilla's, but with underground biomes added.
					addModifiedVanillaOverworldBiomes(consumer, noModifications -> {});

					// When we call TerraBlender's addModifiedVanillaOverworldBiomes...
					// -> it calls terrablender ModifiedVanillaOverworldBuilder.build
					// -> it calls vanilla OverworldBiomeBuilder.addBiomes
					// -> it calls vanilla OverworldBiomeBuilder.addUndergroundBiomes
					// -> it gets hooked by Quark's OverworldBiomeBuilderMixin
					// -> it calls quark AbstractUndergroundBiomeHandler.modifyVanillaOverworldPreset.
					// Therefore, if terrablenderModifyVanillaAnyway is enabled, we already called addUndergroundBiomesTo.
					if(!QuarkGeneralConfig.terrablenderModifyVanillaAnyway)
						addUndergroundBiomesTo(consumer);
				}
			});
		});
	}

	@Override
	public void modifyVanillaOverworldPreset(OverworldBiomeBuilder builder, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {
		if(QuarkGeneralConfig.terrablenderModifyVanillaAnyway)
			addUndergroundBiomesTo(consumer);
	}

}
