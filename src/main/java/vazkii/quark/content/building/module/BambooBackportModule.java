package vazkii.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.handler.WoodSetHandler.WoodSet;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building")
public class BambooBackportModule extends ZetaModule {

	public static WoodSet woodSet;

	@LoadEvent
	public final void register(ZRegister event) {
		woodSet = WoodSetHandler.addWoodSet(this, "bamboo", MaterialColor.COLOR_YELLOW, MaterialColor.COLOR_YELLOW, false, false, true);

		new QuarkBlock("bamboo_mosaic", this, CreativeModeTab.TAB_BUILDING_BLOCKS, BlockBehaviour.Properties.copy(woodSet.planks));
	}

}
