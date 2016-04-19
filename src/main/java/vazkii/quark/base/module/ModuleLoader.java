/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [18/03/2016, 21:52:08 (GMT)]
 */
package vazkii.quark.base.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.automation.QuarkAutomation;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.building.QuarkBuilding;
import vazkii.quark.decoration.QuarkDecoration;
import vazkii.quark.management.QuarkManagement;
import vazkii.quark.tweaks.QuarkTweaks;
import vazkii.quark.vanity.QuarkVanity;
import vazkii.quark.world.QuarkWorld;

public final class ModuleLoader {

	static {
		moduleClasses = new ArrayList();

		registerModule(QuarkTweaks.class);
		registerModule(QuarkWorld.class);
		//		registerModule(QuarkExploration.class);
		//		registerModule(QuarkCombat.class);
		registerModule(QuarkVanity.class);
		registerModule(QuarkDecoration.class);
		registerModule(QuarkBuilding.class);
		registerModule(QuarkAutomation.class);
		registerModule(QuarkManagement.class);
	}

	private static List<Class<? extends Module>> moduleClasses;
	public static Map<Class<? extends Module>, Module> moduleInstances = new HashMap();
	public static Map<Class<? extends Feature>, Feature> featureInstances = new HashMap();
	public static List<Module> enabledModules;

	public static Configuration config;

	public static void preInit(FMLPreInitializationEvent event) {
		moduleClasses.forEach(clazz -> {
			try {
				moduleInstances.put(clazz, clazz.newInstance());
			} catch (Exception e) {
				throw new RuntimeException("Can't initialize module " + clazz, e);
			}
		});

		setupConfig(event);

		forEachModule(module -> FMLLog.info("[Quark] Module " + module.name + " is " + (module.enabled ? "enabled" : "disabled")));

		forEachEnabled(module -> module.preInit(event));
	}

	public static void init(FMLInitializationEvent event) {
		forEachEnabled(module -> module.init(event));
	}

	public static void postInit(FMLPostInitializationEvent event) {
		forEachEnabled(module -> module.postInit(event));
	}

	@SideOnly(Side.CLIENT)
	public static void preInitClient(FMLPreInitializationEvent event) {
		forEachEnabled(module -> module.preInitClient(event));
	}

	@SideOnly(Side.CLIENT)
	public static void initClient(FMLInitializationEvent event) {
		forEachEnabled(module -> module.initClient(event));
	}

	@SideOnly(Side.CLIENT)
	public static void postInitClient(FMLPostInitializationEvent event) {
		forEachEnabled(module -> module.postInitClient(event));
	}

	public static void serverStarting(FMLServerStartingEvent event) {
		forEachEnabled(module -> module.serverStarting(event));
	}

	public static void setupConfig(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		forEachModule(module -> module.enabled = !module.canBeDisabled() || ConfigHelper.loadPropBool(module.name, "_modules", "", true));

		enabledModules = new ArrayList(moduleInstances.values());
		enabledModules.removeIf(module -> !module.enabled);

		loadModuleConfigs();

		MinecraftForge.EVENT_BUS.register(new ChangeListener());
	}

	private static void loadModuleConfigs() {
		forEachEnabled(module -> module.setupConfig());

		if(config.hasChanged())
			config.save();
	}

	public static boolean isModuleEnabled(Class<? extends Module> clazz) {
		return moduleInstances.get(clazz).enabled;
	}

	public static boolean isFeatureEnabled(Class<? extends Feature> clazz) {
		return featureInstances.get(clazz).enabled;
	}

	public static void forEachModule(Consumer<Module> consumer) {
		moduleInstances.values().forEach(consumer);
	}

	public static void forEachEnabled(Consumer<Module> consumer) {
		enabledModules.forEach(consumer);
	}

	private static void registerModule(Class<? extends Module> clazz) {
		if(!moduleClasses.contains(clazz))
			moduleClasses.add(clazz);
	}

	public static class ChangeListener {

		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
			if(eventArgs.getModID().equals(LibMisc.MOD_ID))
				loadModuleConfigs();
		}

	}

}
