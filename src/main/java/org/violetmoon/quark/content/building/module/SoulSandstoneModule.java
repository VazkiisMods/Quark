package org.violetmoon.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.base.handler.VariantHandler;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building")
public class SoulSandstoneModule extends ZetaModule {

	@LoadEvent
	public final void register(ZRegister event) {
		Block.Properties props = Block.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
				.requiresCorrectToolForDrops()
				.strength(0.8F);
		
		VariantHandler.addSlabStairsWall(new QuarkBlock("soul_sandstone", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props));
		new QuarkBlock("chiseled_soul_sandstone", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props);
		VariantHandler.addSlab(new QuarkBlock("cut_soul_sandstone", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props));
		VariantHandler.addSlabAndStairs(new QuarkBlock("smooth_soul_sandstone", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props));
	}
	
}
