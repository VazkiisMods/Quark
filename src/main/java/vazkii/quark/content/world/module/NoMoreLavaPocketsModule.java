package vazkii.quark.content.world.module;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "world")
public class NoMoreLavaPocketsModule extends QuarkModule {

	private static boolean staticEnabled;

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}

	public static boolean shouldDisable(SpringConfiguration configuration) {
		return staticEnabled && !configuration.requiresBlockBelow && configuration.state.is(FluidTags.LAVA);
	}
}
