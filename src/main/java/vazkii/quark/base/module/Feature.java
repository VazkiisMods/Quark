/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 22:46:32 (GMT)]
 */
package vazkii.quark.base.module;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.lib.LibMisc;

public class Feature {

	public Module module;
	
	public boolean loadtimeDone;
	public boolean enabledAtLoadtime;
	
	public boolean enabledByDefault;
	public boolean enabled;
	public boolean prevEnabled;
	public String configCategory;
	public String configName;
	
	public boolean forceLoad;
	
	public final void setupConstantConfig() {
		String[] incompat = getIncompatibleMods();
		if(incompat != null && incompat.length > 0) {
			String desc = "This feature disables itself if any of the following mods are loaded: \n";
			for(String s : incompat)
				desc += (" - " + s + "\n");
			desc += "This is done to prevent content overlap.\nYou can turn this on to force the feature to be loaded even if the above mods are also loaded.";
				
			ConfigHelper.needsRestart = true;
			forceLoad = loadPropBool("Force Enabled", desc, false);
		}
	}
	
	public void setupConfig() {
		// NO-OP
	}

	public void preInit(FMLPreInitializationEvent event) {
		// NO-OP
	}

	public void init(FMLInitializationEvent event) {
		// NO-OP
	}

	public void postInit(FMLPostInitializationEvent event) {
		// NO-OP
	}
	
	public void finalInit(FMLPostInitializationEvent event) {
		// NO-OP
	}

	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		// NO-OP
	}

	@SideOnly(Side.CLIENT)
	public void initClient(FMLInitializationEvent event) {
		// NO-OP
	}

	@SideOnly(Side.CLIENT)
	public void postInitClient(FMLPostInitializationEvent event) {
		// NO-OP
	}

	public void serverStarting(FMLServerStartingEvent event) {
		// NO-OP
	}
	
	public String[] getIncompatibleMods() {
		return null;
	}

	public boolean hasSubscriptions() {
		return false;
	}

	public boolean hasTerrainSubscriptions() {
		return false;
	}

	public boolean hasOreGenSubscriptions() {
		return false;
	}

	public String getFeatureDescription() {
		return "";
	}
	
	public boolean requiresMinecraftRestartToEnable() {
		return false;
	}

	public static void registerTile(Class<? extends TileEntity> clazz, String key) {
		GameRegistry.registerTileEntity(clazz, LibMisc.PREFIX_MOD + key);
	}

	public final boolean isClient() {
		return FMLCommonHandler.instance().getSide().isClient();
	}

	public final int loadPropInt(String propName, String desc, int default_) {
		return ConfigHelper.loadPropInt(propName, configCategory, desc, default_);
	}

	public final double loadPropDouble(String propName, String desc, double default_) {
		return ConfigHelper.loadPropDouble(propName, configCategory, desc, default_);
	}

	public final boolean loadPropBool(String propName, String desc, boolean default_) {
		return ConfigHelper.loadPropBool(propName, configCategory, desc, default_);
	}

	public final String loadPropString(String propName, String desc, String default_) {
		return ConfigHelper.loadPropString(propName, configCategory, desc, default_);
	}

	public final String[] loadPropStringList(String propName, String desc, String[] default_) {
		return ConfigHelper.loadPropStringList(propName, configCategory, desc, default_);
	}
}
