package org.violetmoon.quark.base.handler;

import java.util.List;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.piston.ZetaPistonStructureResolver;

import com.google.common.collect.Lists;

public class GeneralConfig {

	public static final GeneralConfig INSTANCE = new GeneralConfig();

	private static final List<String> STATIC_ALLOWED_SCREENS = Lists.newArrayList(
			"appeng.client.gui.implementations.SkyChestScreen",
			"com.progwml6.ironchest.client.screen.IronChestScreen",
			"net.mehvahdjukaar.supplementaries.client.screens.SackScreen",
			"vazkii.quark.addons.oddities.client.screen.CrateScreen",
			"vazkii.quark.addons.oddities.client.screen.BackpackInventoryScreen"
			);

	private static final List<String> STATIC_DENIED_SCREENS = Lists.newArrayList(
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
	public static boolean disableQMenuEffects = false;

	@Config(description = "Disable this to turn off the quark system that makes features turn off when specified mods with the same content are loaded")
	public static boolean useAntiOverlap = true;

	@Config(name = "Use Piston Logic Replacement",
			description = "Enable Zeta's piston structure resolver, needed for some Quark features. If you're having troubles, try turning this off, but be aware other Zeta-using mods can enable it too.")
	public static boolean usePistonLogicRepl = true;

	@Config(description = "Ask Zeta to set the piston push limit. Only has an effect if Zeta's piston structure resolver is in use.")
	@Config.Min(value = 0, exclusive = true)
	public static int pistonPushLimit = 12;

	@Config(description = "How many advancements deep you can see in the advancement screen. Vanilla is 2.")
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

	@Config(description = "A list of screens that can accept quark's buttons. Use \"Print Screen Classnames\" to find the names of any others you'd want to add.")
	private static List<String> allowedScreens = Lists.newArrayList();

	@Config(description = "If set to true, the 'Allowed Screens' option will work as a Blacklist rather than a Whitelist. WARNING: Use at your own risk as some mods may not support this.")
	private static boolean useScreenListBlacklist = false;

	@Config(description = "Set to true to make the quark big worldgen features such as stone clusters generate as spheres rather than unique shapes. It's faster, but won't look as cool")
	public static boolean useFastWorldgen = false;

	@Config(description = "Enables quark network profiling features. Do not enable this unless requested to.")
	public static boolean enableNetworkProfiling = false;
	
	@Config(description = "Used for terrablender integration")
	public static int terrablenderRegionWeight = 1;

	@Config(description = "Set to false to stop quark from adding its own items to multi-requirement vanilla advancements")
	public static boolean enableAdvancementModification = true;
	
	@Config(description = "Set to false to stop quark from adding its own advancements")
	public static boolean enableQuarkAdvancements = true;
	
	@Config(description = "Set to false to disable the popup message telling you that you can config quark in the q menu")
	public static boolean enableOnboarding = true;
	
	@Config(description = "Set to false to disable the behavior where quark will automatically hide any disabled items")
	public static boolean hideDisabledContent = true;
	
	@Config(description = "Set to false to disable Quark's item info when viewing recipe/uses for an item in JEI")
	public static boolean enableJeiItemInfo = true;
	
	@Config(description = "For JEI info purposes, add any items here to specifically disable their JEI info from Quark. Note that Quark already only shows info that's relevant to which features are enabled")
	public static List<String> suppressedInfo = Lists.newArrayList();
	
	private GeneralConfig() {
		// NO-OP
	}

	public static boolean isScreenAllowed(Object screen) {
		String clazz = screen.getClass().getName();
		if(clazz.startsWith("net.minecraft."))
			return true;

		if(STATIC_ALLOWED_SCREENS.contains(clazz))
			return true;
		if(STATIC_DENIED_SCREENS.contains(clazz))
			return false;

		return allowedScreens.contains(clazz) != useScreenListBlacklist;
	}

	@LoadEvent
	public static void configChanged(ZConfigChanged e) {
		ZetaPistonStructureResolver.GlobalSettings.requestEnabled(Quark.MOD_ID, usePistonLogicRepl);
		ZetaPistonStructureResolver.GlobalSettings.requestPushLimit(Quark.MOD_ID, pistonPushLimit);
	}

}
