package vazkii.quark.content.world.module;

import com.google.common.base.Functions;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.Tags;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.handler.WoodSetHandler.WoodSet;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.HintManager;
import vazkii.quark.base.world.WorldGenHandler;
import vazkii.quark.base.world.WorldGenWeights;
import vazkii.quark.content.world.block.BlossomLeavesBlock;
import vazkii.quark.content.world.block.BlossomSaplingBlock;
import vazkii.quark.content.world.block.BlossomSaplingBlock.BlossomTree;
import vazkii.quark.content.world.config.BlossomTreeConfig;
import vazkii.quark.content.world.gen.BlossomTreeGenerator;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@LoadModule(category = ModuleCategory.WORLD)
public class BlossomTreesModule extends QuarkModule {

	@Config public BlossomTreeConfig blue = new BlossomTreeConfig(200, Tags.Biomes.IS_SNOWY);
	@Config public BlossomTreeConfig lavender = new BlossomTreeConfig(100, Tags.Biomes.IS_SWAMP);
	@Config public BlossomTreeConfig orange = new BlossomTreeConfig(100, BiomeTags.IS_SAVANNA);
	@Config public BlossomTreeConfig pink = new BlossomTreeConfig(100, BiomeTags.IS_MOUNTAIN);
	@Config public BlossomTreeConfig yellow = new BlossomTreeConfig(200, Tags.Biomes.IS_PLAINS);
	@Config public BlossomTreeConfig red = new BlossomTreeConfig(30, BiomeTags.IS_BADLANDS);

	@Config public static boolean dropLeafParticles = true;

	public static Map<BlossomTree, BlossomTreeConfig> trees = new HashMap<>();

	public static WoodSet woodSet;

	@Override
	public void register() {
		woodSet = WoodSetHandler.addWoodSet(this, "blossom", MaterialColor.COLOR_RED, MaterialColor.COLOR_BROWN, true);

		add("blue", MaterialColor.COLOR_LIGHT_BLUE, blue);
		add("lavender", MaterialColor.COLOR_PINK, lavender);
		add("orange", MaterialColor.TERRACOTTA_ORANGE, orange);
		add("pink", MaterialColor.COLOR_PINK, pink);
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

	@Override
	public void addAdditionalHints(BiConsumer<Item, Component> consumer) {
		for(BlossomTree tree : trees.keySet())
			HintManager.hintItem(consumer, tree.sapling.asItem());
	}

	private void add(String colorName, MaterialColor color, BlossomTreeConfig config) {
		BlossomLeavesBlock leaves = new BlossomLeavesBlock(colorName, this, color);
		BlossomTree tree = new BlossomTree(leaves);
		BlossomSaplingBlock sapling = new BlossomSaplingBlock(colorName, this, tree);
		VariantHandler.addFlowerPot(sapling, Quark.ZETA.registry.getInternalName(sapling).getPath(), Functions.identity());

		trees.put(tree, config);
	}

}
