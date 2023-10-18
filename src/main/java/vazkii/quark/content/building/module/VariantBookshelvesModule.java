package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.Blocks;
import vazkii.quark.base.handler.ItemOverrideHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.quark.content.building.block.VariantBookshelfBlock;

@LoadModule(category = "building", antiOverlap = { "woodworks" })
public class VariantBookshelvesModule extends QuarkModule {

	@Config public static boolean changeNames = true;

	@Override
	public void register() {
		for(Wood type : VanillaWoods.NON_OAK)
			new VariantBookshelfBlock(type.name(), this, !type.nether());
	}

	@Override
	public void configChanged() {
		ItemOverrideHandler.changeBlockLocalizationKey(Blocks.BOOKSHELF, "block.quark.oak_bookshelf", changeNames && enabled);
	}
}
