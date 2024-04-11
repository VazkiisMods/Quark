package org.violetmoon.quark.content.building.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;

@ZetaLoadModule(category = "building")
public class MorePottedPlantsModule extends ZetaModule {

	private static Map<Block, Block> tintedBlocks = new HashMap<>();

	@Hint(key = "pottable_stuff")
	List<Block> pottableBlocks = Lists.newArrayList();

	@LoadEvent
	public final void register(ZRegister event) {
		add(event, Blocks.BEETROOTS, "beetroot");
		add(event, Blocks.SWEET_BERRY_BUSH, "berries");
		add(event, Blocks.CARROTS, "carrot");
		add(event, Blocks.CHORUS_FLOWER, "chorus");
		add(event, Blocks.COCOA, "cocoa_bean");
		Block grass = add(event, Blocks.GRASS, "grass");
		add(event, Blocks.PEONY, "peony");
		Block largeFern = add(event, Blocks.LARGE_FERN, "large_fern");
		add(event, Blocks.LILAC, "lilac");
		add(event, Blocks.MELON_STEM, "melon");
		add(event, Blocks.NETHER_SPROUTS, "nether_sprouts");
		add(event, Blocks.NETHER_WART, "nether_wart");
		add(event, Blocks.POTATOES, "potato");
		add(event, Blocks.PUMPKIN_STEM, "pumpkin");
		add(event, Blocks.ROSE_BUSH, "rose");
		event.getVariantRegistry().addFlowerPot(Blocks.SEA_PICKLE, "sea_pickle", p -> p.lightLevel(b -> 3));
		Block sugarCane = add(event, Blocks.SUGAR_CANE, "sugar_cane");
		add(event, Blocks.SUNFLOWER, "sunflower");
		Block tallGrass = add(event, Blocks.TALL_GRASS, "tall_grass");
		add(event, Blocks.TWISTING_VINES, "twisting_vines");
		Block vine = add(event, Blocks.VINE, "vine");
		add(event, Blocks.WEEPING_VINES, "weeping_vines");
		add(event, Blocks.WHEAT, "wheat");
		event.getVariantRegistry().addFlowerPot(Blocks.CAVE_VINES, "cave_vines", p -> p.lightLevel(b -> 14));
		add(event, Blocks.PITCHER_PLANT, "pitcher_plant");

		tintedBlocks.put(grass, Blocks.GRASS);
		tintedBlocks.put(largeFern, Blocks.LARGE_FERN);
		tintedBlocks.put(sugarCane, Blocks.SUGAR_CANE);
		tintedBlocks.put(tallGrass, Blocks.TALL_GRASS);
		tintedBlocks.put(vine, Blocks.VINE);
	}

	private FlowerPotBlock add(ZRegister event, Block block, String name) {
		pottableBlocks.add(block);
		return event.getVariantRegistry().addFlowerPot(block, name, Functions.identity());
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		for(Block b : tintedBlocks.keySet()) {
			BlockState tState = tintedBlocks.get(b).defaultBlockState();
			BlockColor color = (state, worldIn, pos, tintIndex) -> Minecraft.getInstance().getBlockColors().getColor(tState, worldIn, pos, tintIndex);
			Minecraft.getInstance().getBlockColors().register(color, b);
		}
	}

}
