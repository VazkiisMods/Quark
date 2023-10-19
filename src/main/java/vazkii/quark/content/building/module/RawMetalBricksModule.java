package vazkii.quark.content.building.module;

import com.google.common.collect.ImmutableSet;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building")
public class RawMetalBricksModule extends QuarkModule {

	@LoadEvent
	public final void register(ZRegister event) {
		IQuarkBlock iron = new QuarkBlock("raw_iron_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Properties.copy(Blocks.RAW_IRON_BLOCK));
		IQuarkBlock gold = new QuarkBlock("raw_gold_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Properties.copy(Blocks.RAW_GOLD_BLOCK));
		IQuarkBlock copper = new QuarkBlock("raw_copper_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Properties.copy(Blocks.RAW_COPPER_BLOCK));
		
		ImmutableSet.of(iron, gold, copper).forEach(VariantHandler::addSlabAndStairs);
	}
	
}
