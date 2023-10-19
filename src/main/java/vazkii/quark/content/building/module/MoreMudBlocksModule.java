package vazkii.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkPillarBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.MudBrickLatticeBlock;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building")
public class MoreMudBlocksModule extends QuarkModule {

	@LoadEvent
	public final void register(ZRegister event) {
		BlockBehaviour.Properties props = Properties.copy(Blocks.MUD_BRICKS);
		
		new QuarkBlock("carved_mud_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props);
		new QuarkPillarBlock("mud_pillar", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props);
		new MudBrickLatticeBlock(this, props);
	}
	
}
