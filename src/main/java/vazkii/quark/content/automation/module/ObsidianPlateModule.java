package vazkii.quark.content.automation.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.automation.block.ObsidianPressurePlateBlock;

/**
 * @author WireSegal
 * Created at 9:51 PM on 10/8/19.
 */
@LoadModule(category = "automation")
public class ObsidianPlateModule extends QuarkModule {
	@Override
	public void register() {
		new ObsidianPressurePlateBlock("obsidian_pressure_plate", this, CreativeModeTab.TAB_REDSTONE,
				Block.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
						.requiresCorrectToolForDrops()
						.noCollission()
						.strength(2F, 1200.0F));
	}
}
