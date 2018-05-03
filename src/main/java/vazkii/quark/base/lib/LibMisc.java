/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 21:39:39 (GMT)]
 */
package vazkii.quark.base.lib;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LibMisc {
	// Logger
	public static final Logger LOGGER = LogManager.getLogger("Quark");

	// Mod Constants
	public static final String MOD_ID = "quark";
	public static final String MOD_NAME = MOD_ID;
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;
	public static final String DEPENDENCIES = "required-before:autoreglib;";
	public static final String PREFIX_MOD = MOD_ID + ":";

	// Proxy Constants
	public static final String PROXY_COMMON = "vazkii.quark.base.proxy.CommonProxy";
	public static final String PROXY_CLIENT = "vazkii.quark.base.proxy.ClientProxy";
	public static final String GUI_FACTORY = "vazkii.quark.base.client.gui.GuiFactory";

	public static final List<String> OREDICT_DYES = Arrays.asList(new String[]{
			"dyeBlack",
			"dyeRed",
			"dyeGreen",
			"dyeBrown",
			"dyeBlue",
			"dyePurple",
			"dyeCyan",
			"dyeLightGray",
			"dyeGray",
			"dyePink",
			"dyeLime",
			"dyeYellow",
			"dyeLightBlue",
			"dyeMagenta",
			"dyeOrange",
			"dyeWhite"
	});
	
	public static final String MOD_WEBSITE = "https://quark.vazkii.us";
	
	public static final ResourceLocation GENERAL_ICONS_RESOURCE = new ResourceLocation(MOD_ID, "textures/misc/general_icons.png");
	
}
