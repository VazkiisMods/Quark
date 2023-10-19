package vazkii.quark.content.tools.module;

import net.minecraft.world.item.Item;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.tools.item.TrowelItem;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "tools")
public class TrowelModule extends QuarkModule {

	@Config(name = "Trowel Max Durability",
			description = "Amount of blocks placed is this value + 1.\nSet to 0 to make the Trowel unbreakable")
	@Config.Min(0)
	public static int maxDamage = 0;

	@Hint Item trowel;
	
	@LoadEvent
	public final void register(ZRegister event) {
		trowel = new TrowelItem(this);
	}
	
	
}
