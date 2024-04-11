package org.violetmoon.quark.content.building.module;

import org.violetmoon.zeta.block.IZetaBlock;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

import com.google.common.collect.ImmutableSet;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

@ZetaLoadModule(category = "building")
public class RawMetalBricksModule extends ZetaModule {

	@LoadEvent
	public final void register(ZRegister event) {
		IZetaBlock iron = (IZetaBlock) new ZetaBlock("raw_iron_bricks", this, Properties.copy(Blocks.RAW_IRON_BLOCK)).setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, Blocks.IRON_BLOCK, false);
		IZetaBlock gold = (IZetaBlock) new ZetaBlock("raw_gold_bricks", this, Properties.copy(Blocks.RAW_GOLD_BLOCK)).setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, Blocks.GOLD_BLOCK, false);
		IZetaBlock copper = (IZetaBlock) new ZetaBlock("raw_copper_bricks", this, Properties.copy(Blocks.RAW_COPPER_BLOCK)).setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, Blocks.COPPER_BLOCK, false);

		ImmutableSet.of(iron, gold, copper).forEach(what -> event.getVariantRegistry().addSlabStairsWall(what, null));
	}

}
