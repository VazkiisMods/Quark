package vazkii.quark.content.building.module;

import net.minecraft.world.level.block.ComposterBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.building.block.ThatchBlock;
import vazkii.zeta.event.ZLoadComplete;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building", antiOverlap = {"goated", "environmental"})
public class ThatchModule extends ZetaModule {

	@Config.Min(0)
	@Config.Max(1)
	@Config public static double fallDamageMultiplier = 0.5;
	
	public static ThatchBlock thatch;
	
	@LoadEvent
	public final void register(ZRegister event) {
		thatch = new ThatchBlock(this);
		VariantHandler.addSlabAndStairs(thatch);
	}

	@LoadEvent
	public void loadComplete(ZLoadComplete event) {
		event.enqueueWork(() -> ComposterBlock.COMPOSTABLES.put(thatch.asItem(), 0.65F));
	}
	
}
