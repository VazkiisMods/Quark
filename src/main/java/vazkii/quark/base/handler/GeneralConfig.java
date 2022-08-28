package vazkii.quark.base.handler;

import java.util.List;

import com.google.common.collect.Lists;

import vazkii.quark.base.module.config.Config;

public class GeneralConfig {

	public static final GeneralConfig INSTANCE = new GeneralConfig();

	private static final List<String> STATIC_IGNORED_SCREENS = Lists.newArrayList(
		"blusunrize.immersiveengineering.client.gui.CraftingTableScreen",
		"com.tfar.craftingstation.client.CraftingStationScreen",
		"com.refinedmods.refinedstorage.screen.grid.GridScreen", 
		"appeng.client.gui.me.items.CraftingTermScreen", 
		"appeng.client.gui.me.items.PatternTermScreen",
		"com.blakebr0.extendedcrafting.client.screen.EliteTableScreen",
		"com.blakebr0.extendedcrafting.client.screen.EliteAutoTableScreen",
		"com.blakebr0.extendedcrafting.client.screen.UltimateTableScreen",
		"com.blakebr0.extendedcrafting.client.screen.UltimateAutoTableScreen",
		"me.desht.modularrouters.client.gui.filter.GuiFilterScreen",
		"com.resourcefulbees.resourcefulbees.client.gui.screen.CentrifugeScreen",
		"com.resourcefulbees.resourcefulbees.client.gui.screen.MechanicalCentrifugeScreen",
		"com.resourcefulbees.resourcefulbees.client.gui.screen.CentrifugeMultiblockScreen",
		"com.refinedmods.refinedstorage.screen.FilterScreen",
		"de.markusbordihn.dailyrewards.client.screen.RewardScreen"
	);
	
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
	
	@Config
	@Config.Min(value = 0, exclusive = true)
	public static int advancementVisibilityDepth = 2;

	@Config(description = "Blocks that Quark should treat as Shulker Boxes.")
	public static List<String> shulkerBoxes = SimilarBlockTypeHandler.getBasicShulkerBoxes();

	@Config(description = "Should Quark treat anything with 'shulker_box' in its item identifier as a shulker box?")
	public static boolean interpretShulkerBoxLikeBlocks = true;

	@Config(description = "Set to true to enable a system that debugs quark's worldgen features. This should ONLY be used if you're asked to by a dev.")
	public static boolean enableWorldgenWatchdog = false;

	@Config(description = "Set to true if you need to find the class name for a screen that's causing problems")
	public static boolean printScreenClassnames = false;

	@Config(description = "A list of screens that don't play well with quark's buttons. Use \"Print Screen Classnames\" to find the names of any others you'd want to add.")
	private static List<String> ignoredScreens = Lists.newArrayList();

	@Config(description = "Set to true to make the quark big worldgen features such as stone clusters or underground biomes generate as spheres rather than unique shapes. It's faster, but won't look as cool")
	public static boolean useFastWorldgen = false;
	
	@Config(description = "Enables quark network profiling features. Do not enable this unless requested to.")
	public static boolean enableNetworkProfiling = false;

	private GeneralConfig() {
		// NO-OP
	}
	
	public static boolean isScreenIgnored(Object screen) {
		String clazz = screen.getClass().getName();
		return STATIC_IGNORED_SCREENS.contains(clazz) || ignoredScreens.contains(clazz);
	}

}
