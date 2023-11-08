package org.violetmoon.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.violetmoon.quark.base.block.QuarkFenceGateBlock;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

/**
 * @author WireSegal
 * Created at 10:51 AM on 10/9/19.
 */
@ZetaLoadModule(category = "building")
public class NetherBrickFenceGateModule extends ZetaModule {
	@LoadEvent
	public final void register(ZRegister event) {
		new QuarkFenceGateBlock("nether_brick_fence_gate", this, CreativeModeTab.TAB_REDSTONE,
				Block.Properties.of(Material.STONE, MaterialColor.NETHER)
				.requiresCorrectToolForDrops()
				.sound(SoundType.NETHER_BRICKS)
				.strength(2.0F, 6.0F));
	}
}
