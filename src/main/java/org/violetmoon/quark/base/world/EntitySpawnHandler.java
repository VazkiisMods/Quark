package org.violetmoon.quark.base.world;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.type.CostSensitiveEntitySpawnConfig;
import org.violetmoon.quark.base.config.type.EntitySpawnConfig;
import org.violetmoon.quark.base.item.QuarkSpawnEggItem;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.module.ZetaModule;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class EntitySpawnHandler {

	private static final List<TrackedSpawnConfig> trackedSpawnConfigs = new LinkedList<>();

	public static <T extends Mob> void registerSpawn(EntityType<T> entityType, MobCategory classification, Type placementType, Heightmap.Types heightMapType, SpawnPredicate<T> placementPredicate, EntitySpawnConfig config) {
		SpawnPlacements.register(entityType, placementType, heightMapType, placementPredicate);

		track(entityType, classification, config, false);
	}

	public static <T extends Mob> void track(EntityType<T> entityType, MobCategory classification, EntitySpawnConfig config, boolean secondary) {
		trackedSpawnConfigs.add(new TrackedSpawnConfig(entityType, classification, config, secondary));
	}

	public static void addEgg(ZetaModule module, EntityType<? extends Mob> entityType, int color1, int color2, EntitySpawnConfig config) {
		addEgg(entityType, color1, color2, module, config::isEnabled);
	}

	public static void addEgg(EntityType<? extends Mob> entityType, int color1, int color2, ZetaModule module, BooleanSupplier enabledSupplier) {
		new QuarkSpawnEggItem(() -> entityType, color1, color2, Quark.ZETA.registry.getRegistryName(entityType, Registry.ENTITY_TYPE) + "_spawn_egg", module,
				new Item.Properties().tab(CreativeModeTab.TAB_MISC))
		.setCondition(enabledSupplier);
	}

	public static void modifyBiome(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder biomeInfoBuilder) {
		MobSpawnSettingsBuilder builder = biomeInfoBuilder.getMobSpawnSettings();

		for(TrackedSpawnConfig c : trackedSpawnConfigs) {
			List<MobSpawnSettings.SpawnerData> l = builder.getSpawner(c.classification);
			if(!c.secondary)
				l.removeIf(e -> e.type.equals(c.entityType));

			if(c.config.isEnabled() && c.config.biomes.canSpawn(biome))
				l.add(c.entry);

			if(c.config instanceof CostSensitiveEntitySpawnConfig csc) {
				builder.addMobCharge(c.entityType, csc.spawnCost, csc.maxCost);
			}
		}
	}

	@LoadEvent
	public static void refresh(ZConfigChanged event) {
		for(TrackedSpawnConfig c : trackedSpawnConfigs)
			c.refresh();
	}

	private static class TrackedSpawnConfig {

		final EntityType<?> entityType;
		final MobCategory classification;
		final EntitySpawnConfig config;
		final boolean secondary;
		MobSpawnSettings.SpawnerData entry;

		TrackedSpawnConfig(EntityType<?> entityType, MobCategory classification, EntitySpawnConfig config, boolean secondary) {
			this.entityType = entityType;
			this.classification = classification;
			this.config = config;
			this.secondary = secondary;
			refresh();
		}

		private void refresh() {
			entry = new MobSpawnSettings.SpawnerData(entityType, config.spawnWeight, Math.min(config.minGroupSize, config.maxGroupSize), Math.max(config.minGroupSize, config.maxGroupSize));
		}

	}

}
