package org.violetmoon.quark.content.automation.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.violetmoon.quark.content.automation.block.ObsidianPressurePlateBlock;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

/**
 * @author WireSegal
 * Created at 9:51 PM on 10/8/19.
 */
@ZetaLoadModule(category = "automation")
public class ObsidianPlateModule extends ZetaModule {
	@LoadEvent
	public final void register(ZRegister event) {
		new ObsidianPressurePlateBlock("obsidian_pressure_plate", this, CreativeModeTab.TAB_REDSTONE,
				Block.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
						.requiresCorrectToolForDrops()
						.noCollission()
						.strength(2F, 1200.0F));
	}
}
