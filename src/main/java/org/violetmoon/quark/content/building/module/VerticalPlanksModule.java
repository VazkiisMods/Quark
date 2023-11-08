package org.violetmoon.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.base.util.VanillaWoods;
import org.violetmoon.quark.base.util.VanillaWoods.Wood;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building", antiOverlap = { "woodworks" })
public class VerticalPlanksModule extends ZetaModule {

	@LoadEvent
	public final void register(ZRegister event) {
		for(Wood type : VanillaWoods.ALL)
			add(type.name(), type.planks(), this);
	}
	
	public static QuarkBlock add(String name, Block base, ZetaModule module) {
		return new QuarkBlock("vertical_" + name + "_planks", module, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(base));
	}
	
}
