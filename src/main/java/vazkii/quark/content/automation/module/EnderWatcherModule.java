package vazkii.quark.content.automation.module;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.automation.block.EnderWatcherBlock;
import vazkii.quark.content.automation.block.be.EnderWatcherBlockEntity;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "automation")
public class EnderWatcherModule extends QuarkModule {

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
