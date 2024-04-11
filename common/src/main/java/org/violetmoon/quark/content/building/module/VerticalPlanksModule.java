package org.violetmoon.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;

import org.violetmoon.zeta.util.VanillaWoods;
import org.violetmoon.zeta.util.VanillaWoods.Wood;
import org.violetmoon.zeta.block.ZetaBlock;
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

	public static ZetaBlock add(String name, Block base, ZetaModule module) {
		return (ZetaBlock) new ZetaBlock("vertical_" + name + "_planks", module, Block.Properties.copy(base)).setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, base, false);
	}

}
