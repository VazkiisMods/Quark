package vazkii.quark.content.automation.module;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.content.automation.block.EnderWatcherBlock;
import vazkii.quark.content.automation.block.be.EnderWatcherBlockEntity;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

@ZetaLoadModule(category = "automation")
public class EnderWatcherModule extends ZetaModule {

	public static BlockEntityType<EnderWatcherBlockEntity> blockEntityType;
	
	public static QuarkGenericTrigger watcherCenterTrigger;
	@Hint Block ender_watcher;

	@LoadEvent
	public final void register(ZRegister event) {
		ender_watcher = new EnderWatcherBlock(this);
		blockEntityType = BlockEntityType.Builder.of(EnderWatcherBlockEntity::new, ender_watcher).build(null);
		Quark.ZETA.registry.register(blockEntityType, "ender_watcher", Registry.BLOCK_ENTITY_TYPE_REGISTRY);

		watcherCenterTrigger = QuarkAdvancementHandler.registerGenericTrigger("watcher_center");
	}
	
}
