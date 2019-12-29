package vazkii.quark.base.handler;

import vazkii.quark.base.module.Config;

import java.util.List;

import com.google.common.collect.Lists;

public class GeneralConfig {

	public static final GeneralConfig INSTANCE = new GeneralConfig();

	@Config(name = "Enable 'q' Button")
	public static boolean enableQButton = true;

	@Config(name = "'q' Button on the Right")
	public static boolean qButtonOnRight = false;

	@Config
	public static boolean useAntiOverlap = true;

	@Config(name = "Use Piston Logic Replacement",
			description = "Quark replaces the Piston logic to allow for its piston features to work. If you're having troubles, try turning this off.")
	public static boolean usePistonLogicRepl = true;

	@Config
	@Config.Min(value = 0, exclusive = true)
	public static int pistonPushLimit = 12;

	@Config(description = "Quark messes with the Stonecutter to allow any item that can be used in it to be shift clicked in. Set this to false to turn it off.")
	public static boolean hackStonecutterShiftClick = true;

	@Config(description = "Blocks that Quark should treat as Shulker Boxes.")
	public static List<String> shulkerBoxes = SimilarBlockTypeHandler.getBasicShulkerBoxes();

	@Config(description = "Should Quark treat anything with 'shulker_box' in its item identifier as a shulker box?")
	public static boolean interpretShulkerBoxLikeBlocks = true;

	@Config(description = "Set to true to enable a system that debugs quark's worldgen features. This should ONLY be used if you're asked to by a dev.")
	public static boolean enableWorldgenWatchdog = false;

	@Config(description = "Set to true if you need to find the class name for a screen that's causing problems")
	public static boolean printScreenClassnames = false;

	@Config(description = "A list of screens that don't play well with quark's buttons. Use \"Print Screen Classnames\" to find the names of any others you'd want to add.")
	public static List<String> ignoredScreens = Lists.newArrayList(
			"com.tfar.craftingstation.client.CraftingStationScreen",
			"com.raoulvdberge.refinedstorage.screen.grid.GridScreen",
			"com.raoulvdberge.refinedstorage.screen.DiskManipulatorScreen",
			"com.raoulvdberge.refinedstorage.screen.InterfaceScreen"
			);

	@Config(description = "Set to true to make the quark big worldgen features such as stone clusters or underground biomes generate as spheres rather than unique shapes. It's faster, but won't look as cool")
	public static boolean useFastWorldgen = false;

	public static List<String> ignoredScreensCode = Lists.newArrayList();

	private GeneralConfig() {
		// NO-OP
	}

}
