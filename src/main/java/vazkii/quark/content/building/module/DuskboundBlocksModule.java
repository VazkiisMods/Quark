package vazkii.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building")
public class DuskboundBlocksModule extends QuarkModule {

	@LoadEvent
	public final void register(ZRegister event) {
		VariantHandler.addSlabAndStairs(new QuarkBlock("duskbound_block", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.PURPUR_BLOCK)));
		
		new QuarkBlock("duskbound_lantern", this, CreativeModeTab.TAB_BUILDING_BLOCKS, 
				Block.Properties.copy(Blocks.PURPUR_BLOCK)
				.lightLevel(b -> 15));
	}
	
}
