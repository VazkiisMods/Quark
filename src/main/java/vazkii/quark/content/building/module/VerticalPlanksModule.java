package vazkii.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building", antiOverlap = { "woodworks" })
public class VerticalPlanksModule extends QuarkModule {

	@LoadEvent
	public final void register(ZRegister event) {
		for(Wood type : VanillaWoods.ALL)
			add(type.name(), type.planks(), this);
	}
	
	public static QuarkBlock add(String name, Block base, QuarkModule module) {
		return new QuarkBlock("vertical_" + name + "_planks", module, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(base));
	}
	
}
