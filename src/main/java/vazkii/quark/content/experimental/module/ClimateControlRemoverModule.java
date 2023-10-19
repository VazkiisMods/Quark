package vazkii.quark.content.experimental.module;

import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "experimental", enabledByDefault = false)
public class ClimateControlRemoverModule extends QuarkModule {

	public static boolean staticEnabled;

	@Config(description = "Disables the temperature comparison when choosing biomes to generate.")
	public static boolean disableTemperature = false;

	@Config(description = "Disables the humidity comparison when choosing biomes to generate.")
	public static boolean disableHumidity = false;

	@Config(description = "Disables the 'continentalness' comparison when choosing biomes to generate.\n" +
		"WARNING: Enabling this will probably make oceans act a lot more like rivers.")
	public static boolean disableContinentalness = false;

	@Config(description = "Disables the 'erosion' comparison when choosing biomes to generate.\n" +
		"WARNING: Enabling this will probably create very extreme height differences, and will make the End more chaotic.")
	public static boolean disableErosion = false;

	@Config(description = "Disables the 'depth' comparison when choosing biomes to generate.\n" +
		"WARNING: Enabling this will probably make cave biomes appear at unusual heights.")
	public static boolean disableDepth = false;

	@Config(description = "Disables the 'weirdness' comparison when choosing biomes to generate.\n" +
		"WARNING: Enabling this will... well, probably make things weird.")
	public static boolean disableWeirdness = false;

	@Config(description = "Disables the 'offset' parameter when choosing biomes to generate.\n" +
		"WARNING: Enabling this will make rarer nether biomes more common.")
	public static boolean disableOffset = false;

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}

}
