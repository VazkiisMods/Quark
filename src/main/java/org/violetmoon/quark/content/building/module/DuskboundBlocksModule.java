package org.violetmoon.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.base.handler.VariantHandler;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building")
public class DuskboundBlocksModule extends ZetaModule {

	@LoadEvent
	public final void register(ZRegister event) {
		VariantHandler.addSlabAndStairs(new QuarkBlock("duskbound_block", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.PURPUR_BLOCK)));
		
		new QuarkBlock("duskbound_lantern", this, CreativeModeTab.TAB_BUILDING_BLOCKS, 
				Block.Properties.copy(Blocks.PURPUR_BLOCK)
				.lightLevel(b -> 15));
	}
	
}
