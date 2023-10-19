package vazkii.quark.content.automation.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.automation.block.RedstoneRandomizerBlock;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

/**
 * @author WireSegal
 * Created at 10:34 AM on 8/26/19.
 */
@LoadModule(category = "automation")
public class RedstoneRandomizerModule extends QuarkModule {

	@Hint Block redstone_randomizer;
	
	@LoadEvent
	public final void register(ZRegister event) {
		redstone_randomizer = new RedstoneRandomizerBlock("redstone_randomizer", this, CreativeModeTab.TAB_REDSTONE, Block.Properties.of(Material.DECORATION).strength(0).sound(SoundType.WOOD));
	}
}
