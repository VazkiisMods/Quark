package vazkii.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.handler.WoodSetHandler.WoodSet;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "building")
public class BambooBackportModule extends QuarkModule {

	public static WoodSet woodSet;

	@LoadEvent
	public final void register(ZRegister event) {
		woodSet = WoodSetHandler.addWoodSet(this, "bamboo", MaterialColor.COLOR_YELLOW, MaterialColor.COLOR_YELLOW, false, false, true);

		new QuarkBlock("bamboo_mosaic", this, CreativeModeTab.TAB_BUILDING_BLOCKS, BlockBehaviour.Properties.copy(woodSet.planks));
	}

}
