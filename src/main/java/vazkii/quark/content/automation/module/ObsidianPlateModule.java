package vazkii.quark.content.automation.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.content.automation.block.ObsidianPressurePlateBlock;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

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
