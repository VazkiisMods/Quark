package org.violetmoon.quark.content.building.module;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.handler.ItemOverrideHandler;
import org.violetmoon.quark.base.util.VanillaWoods;
import org.violetmoon.quark.base.util.VanillaWoods.Wood;
import org.violetmoon.quark.content.building.block.VariantBookshelfBlock;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

import net.minecraft.world.level.block.Blocks;

@ZetaLoadModule(category = "building", antiOverlap = { "woodworks" })
public class VariantBookshelvesModule extends ZetaModule {

	@Config public static boolean changeNames = true;

	@LoadEvent
	public final void register(ZRegister event) {
		for(Wood type : VanillaWoods.NON_OAK)
			new VariantBookshelfBlock(type.name(), this, !type.nether());
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		ItemOverrideHandler.changeBlockLocalizationKey(Blocks.BOOKSHELF, "block.quark.oak_bookshelf", changeNames && enabled);
	}
}
