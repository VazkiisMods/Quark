package vazkii.quark.content.automation.module;

import net.minecraft.world.level.block.Block;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.automation.block.MetalButtonBlock;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "automation")
public class MetalButtonsModule extends QuarkModule {

	@Config(flag = "iron_metal_button")
	public static boolean enableIron = true;
	@Config(flag = "gold_metal_button")
	public static boolean enableGold = true;
	
	@Hint("iron_metal_button") Block iron_button;
	@Hint("gold_metal_button") Block gold_button;

	@LoadEvent
	public final void register(ZRegister event) {
		iron_button = new MetalButtonBlock("iron_button", this, 100).setCondition(() -> enableIron);
		gold_button = new MetalButtonBlock("gold_button", this, 4).setCondition(() -> enableGold);
	}
	
}
