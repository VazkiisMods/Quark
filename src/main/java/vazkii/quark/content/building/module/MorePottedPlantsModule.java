package vazkii.quark.content.building.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

@LoadModule(category = "building")
public class MorePottedPlantsModule extends QuarkModule {

	private static Map<Block, Block> tintedBlocks = new HashMap<>();
	
	@Hint(key = "pottable_stuff")
	List<Block> pottableBlocks = Lists.newArrayList();
	
	@LoadEvent
	public final void register(ZRegister event) {
		add(Blocks.BEETROOTS, "beetroot");
		add(Blocks.SWEET_BERRY_BUSH, "berries");
		add(Blocks.CARROTS, "carrot");
		add(Blocks.CHORUS_FLOWER, "chorus");
		add(Blocks.COCOA, "cocoa_bean");
		Block grass = add(Blocks.GRASS, "grass");
		add(Blocks.PEONY, "peony");
		Block largeFern = add(Blocks.LARGE_FERN, "large_fern");
		add(Blocks.LILAC, "lilac");
		add(Blocks.MELON_STEM, "melon");
		add(Blocks.NETHER_SPROUTS, "nether_sprouts");
		add(Blocks.NETHER_WART, "nether_wart");
		add(Blocks.POTATOES, "potato");
		add(Blocks.PUMPKIN_STEM, "pumpkin");
		add(Blocks.ROSE_BUSH, "rose");
		VariantHandler.addFlowerPot(Blocks.SEA_PICKLE, "sea_pickle", p -> p.lightLevel(b -> 3));
		Block sugarCane = add(Blocks.SUGAR_CANE, "sugar_cane");
		add(Blocks.SUNFLOWER, "sunflower");
		Block tallGrass = add(Blocks.TALL_GRASS, "tall_grass");
		add(Blocks.TWISTING_VINES, "twisting_vines");
		Block vine = add(Blocks.VINE, "vine");
		add(Blocks.WEEPING_VINES, "weeping_vines");
		add(Blocks.WHEAT, "wheat");
		VariantHandler.addFlowerPot(Blocks.CAVE_VINES, "cave_vines", p -> p.lightLevel(b -> 14));
		
		tintedBlocks.put(grass, Blocks.GRASS);
		tintedBlocks.put(largeFern, Blocks.LARGE_FERN);
		tintedBlocks.put(sugarCane, Blocks.SUGAR_CANE);
		tintedBlocks.put(tallGrass, Blocks.TALL_GRASS);
		tintedBlocks.put(vine, Blocks.VINE);
	}
	
	private FlowerPotBlock add(Block block, String name) {
		pottableBlocks.add(block);
		return VariantHandler.addFlowerPot(block, name, Functions.identity());
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
