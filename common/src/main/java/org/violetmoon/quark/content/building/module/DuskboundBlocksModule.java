package org.violetmoon.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.violetmoon.zeta.block.IZetaBlock;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;

@ZetaLoadModule(category = "building")
public class DuskboundBlocksModule extends ZetaModule {

	@LoadEvent
	public final void register(ZRegister event) {
		CreativeTabManager.daisyChain();
		Block duskbound = new ZetaBlock("duskbound_block", this, Block.Properties.copy(Blocks.PURPUR_BLOCK)).setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, Blocks.COAL_BLOCK, true);

		new ZetaBlock("duskbound_lantern", this,
				Block.Properties.copy(Blocks.PURPUR_BLOCK)
						.lightLevel(b -> 15))
				.setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS);

		event.getVariantRegistry().addSlabAndStairs((IZetaBlock) duskbound, null);
		CreativeTabManager.endDaisyChain();
	}

}
