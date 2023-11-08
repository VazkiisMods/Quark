package org.violetmoon.quark.content.building.module;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.violetmoon.quark.base.block.IQuarkBlock;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.base.handler.VariantHandler;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building")
public class RawMetalBricksModule extends ZetaModule {

	@LoadEvent
	public final void register(ZRegister event) {
		IQuarkBlock iron = new QuarkBlock("raw_iron_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Properties.copy(Blocks.RAW_IRON_BLOCK));
		IQuarkBlock gold = new QuarkBlock("raw_gold_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Properties.copy(Blocks.RAW_GOLD_BLOCK));
		IQuarkBlock copper = new QuarkBlock("raw_copper_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Properties.copy(Blocks.RAW_COPPER_BLOCK));
		
		ImmutableSet.of(iron, gold, copper).forEach(VariantHandler::addSlabAndStairs);
	}
	
}
