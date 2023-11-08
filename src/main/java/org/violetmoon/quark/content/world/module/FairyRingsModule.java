package org.violetmoon.quark.content.world.module;

import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

import java.util.ArrayList;
import java.util.List;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.config.type.DimensionConfig;
import org.violetmoon.quark.base.world.WorldGenHandler;
import org.violetmoon.quark.base.world.WorldGenWeights;
import org.violetmoon.quark.content.world.gen.FairyRingGenerator;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "world")
public class FairyRingsModule extends ZetaModule {

	@Config public static double forestChance = 0.00625;
	@Config public static double plainsChance = 0.0025;
	@Config public static DimensionConfig dimensions = new DimensionConfig(false, "minecraft:overworld");

	@Config(name = "Ores")
	public static List<String> oresRaw = Lists.newArrayList("minecraft:emerald_ore", "minecraft:diamond_ore");

	public static List<BlockState> ores;

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		WorldGenHandler.addGenerator(this, new FairyRingGenerator(dimensions), Decoration.TOP_LAYER_MODIFICATION, WorldGenWeights.FAIRY_RINGS);
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		ores = new ArrayList<>();
		for(String s : oresRaw) {
			Block b = Registry.BLOCK.get(new ResourceLocation(s));
			if (b != Blocks.AIR) {
				ores.add(b.defaultBlockState());
			}
			else {
				new IllegalArgumentException("Block " + s + " does not exist!").printStackTrace();
			}
		}
	}

}
