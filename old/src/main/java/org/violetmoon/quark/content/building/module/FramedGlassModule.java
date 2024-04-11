package org.violetmoon.quark.content.building.module;

import java.util.HashMap;
import java.util.Map;

import org.violetmoon.zeta.block.IZetaBlock;
import org.violetmoon.zeta.block.ZetaGlassBlock;
import org.violetmoon.zeta.block.ZetaInheritedPaneBlock;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.MiscUtil;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

@ZetaLoadModule(category = "building")
public class FramedGlassModule extends ZetaModule {

	@LoadEvent
	public final void register(ZRegister event) {
		Block.Properties props = Block.Properties.of()
				.strength(3F, 10F)
				.sound(SoundType.GLASS);

		new ZetaInheritedPaneBlock((IZetaBlock) new ZetaGlassBlock("framed_glass", this, false, props).setCreativeTab(CreativeModeTabs.COLORED_BLOCKS, Blocks.GLASS, false))
				.setCreativeTab(CreativeModeTabs.COLORED_BLOCKS, Blocks.GLASS_PANE, false);

		Map<DyeColor, IZetaBlock> blocks = new HashMap<>();
		CreativeTabManager.daisyChain();
		for(DyeColor dye : MiscUtil.CREATIVE_COLOR_ORDER)
			blocks.put(dye, (IZetaBlock) new ZetaGlassBlock(dye.getName() + "_framed_glass", this, true, props).setCreativeTab(CreativeModeTabs.COLORED_BLOCKS, Blocks.PINK_STAINED_GLASS, false));
		CreativeTabManager.endDaisyChain();

		CreativeTabManager.daisyChain();
		for(DyeColor dye : MiscUtil.CREATIVE_COLOR_ORDER)
			new ZetaInheritedPaneBlock(blocks.get(dye)).setCreativeTab(CreativeModeTabs.COLORED_BLOCKS, Blocks.PINK_STAINED_GLASS_PANE, false);
		CreativeTabManager.endDaisyChain();
	}

}
