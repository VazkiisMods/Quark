/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [18/03/2016, 21:52:14 (GMT)]
 */
package vazkii.quark.base.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Module {

	public final String name = makeName();
	public final Map<String, Feature> features = new HashMap();
	public final List<Feature> enabledFeatures = new ArrayList();
	public boolean enabled;

	public void addFeatures() {
		// NO-OP
	}

	public void registerFeature(Feature feature) {
		registerFeature(feature, convertName(feature.getClass().getSimpleName()));
	}

	public String convertName(String origName) {
		String withSpaces = origName.replaceAll("(?<=.)([A-Z])", " $1").toLowerCase();
		return Character.toUpperCase(withSpaces.charAt(0)) + withSpaces.substring(1);
	}

	public void registerFeature(Feature feature, String name) {
		registerFeature(feature, name, true);
	}

	public void registerFeature(Feature feature, String name, boolean enabledByDefault) {
		ModuleLoader.featureInstances.put(feature.getClass(), feature);
		features.put(name, feature);

		feature.enabled = loadPropBool(name, "", enabledByDefault);
		feature.module = this;
		feature.category = this.name + "." + name;
		if(feature.enabled) {
			enabledFeatures.add(feature);

			if(feature.hasSubscriptions())
				MinecraftForge.EVENT_BUS.register(feature);
		}
	}

	public void setupConfig() {
		if(features.isEmpty())
			addFeatures();
		forEachFeature(feature -> feature.setupConfig());
	}

	public void preInit(FMLPreInitializationEvent event) {
		forEachEnabled(feature -> feature.preInit(event));
	}

	public void init(FMLInitializationEvent event) {
		forEachEnabled(feature -> feature.init(event));
	}

	public void postInit(FMLPostInitializationEvent event) {
		forEachEnabled(feature -> feature.postInit(event));
	}

	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		forEachEnabled(feature -> feature.preInitClient(event));
	}

	@SideOnly(Side.CLIENT)
	public void initClient(FMLInitializationEvent event) {
		forEachEnabled(feature -> feature.initClient(event));
	}

	@SideOnly(Side.CLIENT)
	public void postInitClient(FMLPostInitializationEvent event) {
		forEachEnabled(feature -> feature.postInitClient(event));
	}

	public void serverStarting(FMLServerStartingEvent event) {
		forEachEnabled(feature -> feature.serverStarting(event));
	}

	public boolean canBeDisabled() {
		return true;
	}

	String makeName() {
		return getClass().getSimpleName().replaceAll("Quark", "").toLowerCase();
	}

	public final void forEachFeature(Consumer<Feature> consumer) {
		features.values().forEach(consumer);
	}

	public final void forEachEnabled(Consumer<Feature> consumer) {
		enabledFeatures.forEach(consumer);
	}

	public final int loadPropInt(String propName, String desc, int default_) {
		return ConfigHelper.loadPropInt(propName, name, desc, default_);
	}

	public final double loadPropDouble(String propName, String desc, double default_) {
		return ConfigHelper.loadPropDouble(propName, name, desc, default_);
	}

	public final boolean loadPropBool(String propName, String desc, boolean default_) {
		return ConfigHelper.loadPropBool(propName, name, desc, default_);
	}

	public final String loadPropString(String propName, String desc, String default_) {
		return ConfigHelper.loadPropString(propName, name, desc, default_);
	}

}
