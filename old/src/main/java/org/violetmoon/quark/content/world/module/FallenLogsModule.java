package org.violetmoon.quark.content.world.module;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.util.QuarkWorldGenWeights;
import org.violetmoon.quark.content.world.gen.FallenLogGenerator;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.config.type.DimensionConfig;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.world.WorldGenHandler;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

@ZetaLoadModule(category = "world")
public class FallenLogsModule extends ZetaModule {

	@Config
	public static DimensionConfig dimensions = new DimensionConfig(false, "minecraft:overworld");
	
	@Config(description = "Requires the Hollow Logs module to be enabled too")
	public static boolean useHollowLogs = true;
	
	@Config
	public static int rarity = 5;
	
	@Config
	public static int sparseBiomeRarity = 12;
	
	@Config(description = "Tags that define which biomes can have which wood types")
	public static List<String> biomeTags = Arrays.asList(new String[] {
			"quark:has_fallen_acacia=minecraft:acacia_log",
			"quark:has_fallen_birch=minecraft:birch_log",
			"quark:has_fallen_cherry=minecraft:cherry_log",
			"quark:has_fallen_dark_oak=minecraft:dark_oak_log",
			"quark:has_fallen_jungle=minecraft:jungle_log",
			"quark:has_fallen_mangrove=minecraft:mangrove_log",
			"quark:has_fallen_oak=minecraft:oak_log",
			"quark:has_fallen_spruce=minecraft:spruce_log"
	});

	public static Map<TagKey<Biome>, Block> blocksPerTag = new HashMap<>();
	
	public static TagKey<Biome> reducedLogsTag;

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		reducedLogsTag = TagKey.create(Registries.BIOME, new ResourceLocation(Quark.MOD_ID, "has_lower_fallen_tree_density"));
		
		WorldGenHandler.addGenerator(this, new FallenLogGenerator(dimensions), Decoration.TOP_LAYER_MODIFICATION, QuarkWorldGenWeights.FALLEN_LOGS);
	}
	
	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		blocksPerTag.clear();
		for(String s : biomeTags) {
			String[] toks = s.split("=");
			
			String k = toks[0];
			String v = toks[1];
			
			TagKey<Biome> tag = TagKey.create(Registries.BIOME, new ResourceLocation(k));
			Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(v));
			
			if(block == null)
				throw new IllegalArgumentException("Block " + v + " doesn't exist");
			blocksPerTag.put(tag, block);
		}
	}

}
