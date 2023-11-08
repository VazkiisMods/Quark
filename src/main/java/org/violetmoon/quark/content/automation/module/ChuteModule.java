package org.violetmoon.quark.content.automation.module;

import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.automation.block.ChuteBlock;
import org.violetmoon.quark.content.automation.block.be.ChuteBlockEntity;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

/**
 * @author WireSegal
 * Created at 10:25 AM on 9/29/19.
 */
@ZetaLoadModule(category = "automation")
public class ChuteModule extends ZetaModule {

	public static BlockEntityType<ChuteBlockEntity> blockEntityType;
	@Hint Block chute;

	@LoadEvent
	public final void register(ZRegister event) {
		chute = new ChuteBlock("chute", this, CreativeModeTab.TAB_REDSTONE,
				Block.Properties.of(Material.WOOD)
						.strength(2.5F)
						.sound(SoundType.WOOD));

		blockEntityType = BlockEntityType.Builder.of(ChuteBlockEntity::new, chute).build(null);
		Quark.ZETA.registry.register(blockEntityType, "chute", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
	}
}
