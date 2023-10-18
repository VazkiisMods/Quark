package vazkii.quark.content.tweaks.module;

import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = "tweaks")
public class SpongesBetterModule extends QuarkModule {

	@Config(description = "The maximum number of water tiles that a sponge can soak up. Vanilla default is 64.")
	@Config.Min(64)
	public static int maximumWaterDrain = 256;

	@Config(description = "The maximum number of water tiles that a sponge can 'crawl along' for draining. Vanilla default is 6.")
	@Config.Min(6)
	public static int maximumCrawlDistance = 10;

	public static int drainLimit(int previous) {
		if (ModuleLoader.INSTANCE.isModuleEnabled(SpongesBetterModule.class)) {
			// Additive to not directly conflict with other mods
			return maximumWaterDrain - 64 + previous;
		}
		return previous;
	}

	public static int crawlLimit(int previous) {
		if (ModuleLoader.INSTANCE.isModuleEnabled(SpongesBetterModule.class)) {
			// Additive to not directly conflict with other mods
			return maximumCrawlDistance - 6 + previous;
		}
		return previous;
	}

}
