package org.violetmoon.quark.content.world.module;

import java.util.HashMap;
import java.util.Map;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.handler.VariantHandler;
import org.violetmoon.quark.base.handler.WoodSetHandler;
import org.violetmoon.quark.base.handler.WoodSetHandler.WoodSet;
import org.violetmoon.quark.base.world.WorldGenHandler;
import org.violetmoon.quark.base.world.WorldGenWeights;
import org.violetmoon.quark.content.world.block.BlossomLeavesBlock;
import org.violetmoon.quark.content.world.block.BlossomSaplingBlock;
import org.violetmoon.quark.content.world.block.BlossomSaplingBlock.BlossomTree;
import org.violetmoon.quark.content.world.config.BlossomTreeConfig;
import org.violetmoon.quark.content.world.gen.BlossomTreeGenerator;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.event.play.loading.ZGatherHints;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

import com.google.common.base.Functions;

import net.minecraft.core.Registry;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.Tags;

@ZetaLoadModule(category = "world")
public class BlossomTreesModule extends ZetaModule {

	@Config public BlossomTreeConfig blue = new BlossomTreeConfig(200, Tags.Biomes.IS_SNOWY);
	@Config public BlossomTreeConfig lavender = new BlossomTreeConfig(100, Tags.Biomes.IS_SWAMP);
	@Config public BlossomTreeConfig orange = new BlossomTreeConfig(100, BiomeTags.IS_SAVANNA);
	@Config public BlossomTreeConfig yellow = new BlossomTreeConfig(200, Tags.Biomes.IS_PLAINS);
	@Config public BlossomTreeConfig red = new BlossomTreeConfig(30, BiomeTags.IS_BADLANDS);

	@Config public static boolean dropLeafParticles = true;

	public static Map<BlossomTree, BlossomTreeConfig> trees = new HashMap<>();

	public static WoodSet woodSet;

	@LoadEvent
	public final void register(ZRegister event) {
		woodSet = WoodSetHandler.addWoodSet(this, "blossom", MaterialColor.COLOR_RED, MaterialColor.COLOR_BROWN, true);

		add("blue", MaterialColor.COLOR_LIGHT_BLUE, blue);
		add("lavender", MaterialColor.COLOR_PINK, lavender);
		add("orange", MaterialColor.TERRACOTTA_ORANGE, orange);
		add("yellow", MaterialColor.COLOR_YELLOW, yellow);
		add("red", MaterialColor.COLOR_RED, red);
	}

	@LoadEvent
	public void setup(ZCommonSetup e) {
		for(BlossomTree tree : trees.keySet())
			WorldGenHandler.addGenerator(this, new BlossomTreeGenerator(trees.get(tree), tree), Decoration.TOP_LAYER_MODIFICATION, WorldGenWeights.BLOSSOM_TREES);

		e.enqueueWork(() -> {
			for(BlossomTree tree : trees.keySet()) {
				if(tree.leaf.getBlock().asItem() != null)
					ComposterBlock.COMPOSTABLES.put(tree.leaf.getBlock().asItem(), 0.3F);
				if(tree.sapling.asItem() != null)
					ComposterBlock.COMPOSTABLES.put(tree.sapling.asItem(), 0.3F);
			}
		});
	}

	@PlayEvent
	public void addAdditionalHints(ZGatherHints consumer) {
		for(BlossomTree tree : trees.keySet())
			consumer.hintItem(tree.sapling.asItem());
	}

	private void add(String colorName, MaterialColor color, BlossomTreeConfig config) {
		BlossomLeavesBlock leaves = new BlossomLeavesBlock(colorName, this, color);
		BlossomTree tree = new BlossomTree(leaves);
		BlossomSaplingBlock sapling = new BlossomSaplingBlock(colorName, this, tree);
		VariantHandler.addFlowerPot(sapling, zeta.registry.getRegistryName(sapling, Registry.BLOCK).getPath(), Functions.identity());

		trees.put(tree, config);
	}

}
