package vazkii.quark.content.automation.module;

import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import vazkii.quark.base.Quark;
import vazkii.quark.content.automation.block.ChuteBlock;
import vazkii.quark.content.automation.block.be.ChuteBlockEntity;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

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
