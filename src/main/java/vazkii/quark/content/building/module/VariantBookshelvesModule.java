package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.Blocks;
import vazkii.quark.base.handler.ItemOverrideHandler;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.quark.content.building.block.VariantBookshelfBlock;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

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
