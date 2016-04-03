/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/03/2016, 21:39:39 (GMT)]
 */
package vazkii.quark.base.lib;

public final class LibMisc {

	// Mod Constants
	public static final String MOD_ID = "Quark";
	public static final String MOD_NAME = MOD_ID;
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;
	public static final String DEPENDENCIES = "required-after:Forge@[12.16.0.1826,);";
	public static final String PREFIX_MOD = MOD_ID.toLowerCase() + ":";

	// Proxy Constants
	public static final String PROXY_COMMON = "vazkii.quark.base.proxy.CommonProxy";
	public static final String PROXY_CLIENT = "vazkii.quark.base.proxy.ClientProxy";
	public static final String GUI_FACTORY = "vazkii.quark.base.handler.GuiFactory";
	
}
