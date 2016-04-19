/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [05/04/2016, 20:01:35 (GMT)]
 */
package vazkii.quark.world.feature;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;

public class DefaultWorldOptions extends Feature {

	private static Map<String, Integer> intProps = new HashMap();
	private static Map<String, Double> doubleProps = new HashMap();
	private static Map<String, Boolean> boolProps = new HashMap();

	String config;

	@Override
	public void setupConfig() {
		config = "{";

		for(String s : intProps.keySet()) {
			int i = loadPropInt(s, "", intProps.get(s));
			config += "\"" + s + "\":" + i + ",";
		}

		for(String s : doubleProps.keySet()) {
			double d = loadPropDouble(s, "", doubleProps.get(s));
			config += "\"" + s + "\":" + d + ",";
		}

		for(String s : boolProps.keySet()) {
			boolean b = loadPropBool(s, "", boolProps.get(s));
			config += "\"" + s + "\":" + b + ",";
		}

		config = config.replaceAll(",$", "}");
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void guiOpenEvent(InitGuiEvent.Post event) {
		if(event.getGui() instanceof GuiCreateWorld) {
			GuiCreateWorld create = (GuiCreateWorld) event.getGui();
			if(create.chunkProviderSettingsJson == null || create.chunkProviderSettingsJson.isEmpty())
				create.chunkProviderSettingsJson = config;
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

	static {
		intProps.put("seaLevel", 63);
		intProps.put("dungeonChance", 8);
		intProps.put("waterLakeChance", 4);
		intProps.put("lavaLakeChance", 80);
		intProps.put("fixedBiome", -1);
		intProps.put("biomeSize", 4);
		intProps.put("riverSize", 4);
		intProps.put("dirtSize", 33);
		intProps.put("dirtCount", 10);
		intProps.put("dirtMinHeight", 0);
		intProps.put("dirtMaxHeight", 256);
		intProps.put("gravelSize", 33);
		intProps.put("gravelCount", 8);
		intProps.put("gravelMinHeight", 0);
		intProps.put("gravelMaxHeight", 256);
		intProps.put("graniteSize", 33);
		intProps.put("graniteCount", 10);
		intProps.put("graniteMinHeight", 0);
		intProps.put("graniteMaxHeight", 80);
		intProps.put("dioriteSize", 33);
		intProps.put("dioriteCount", 10);
		intProps.put("dioriteMinHeight", 0);
		intProps.put("dioriteMaxHeight", 80);
		intProps.put("andesiteSize", 33);
		intProps.put("andesiteCount", 10);
		intProps.put("andesiteMinHeight", 0);
		intProps.put("andesiteMaxHeight", 80);
		intProps.put("coalSize", 17);
		intProps.put("coalCount", 20);
		intProps.put("coalMinHeight", 0);
		intProps.put("coalMaxHeight", 128);
		intProps.put("ironSize", 9);
		intProps.put("ironCount", 20);
		intProps.put("ironMinHeight", 0);
		intProps.put("ironMaxHeight", 64);
		intProps.put("goldSize", 9);
		intProps.put("goldCount", 2);
		intProps.put("goldMinHeight", 0);
		intProps.put("goldMaxHeight", 32);
		intProps.put("redstoneSize", 8);
		intProps.put("redstoneCount", 8);
		intProps.put("redstoneMinHeight", 0);
		intProps.put("redstoneMaxHeight", 16);
		intProps.put("diamondSize", 8);
		intProps.put("diamondCount", 1);
		intProps.put("diamondMinHeight", 0);
		intProps.put("diamondMaxHeight", 16);
		intProps.put("lapisSize", 7);
		intProps.put("lapisCount", 1);
		intProps.put("lapisCenterHeight", 16);
		intProps.put("lapisSpread", 16);

		doubleProps.put("coordinateScale", 684.412);
		doubleProps.put("heightScale", 684.412);
		doubleProps.put("lowerLimitScale", 512.0);
		doubleProps.put("upperLimitScale", 512.0);
		doubleProps.put("depthNoiseScaleX", 200.0);
		doubleProps.put("depthNoiseScaleZ", 200.0);
		doubleProps.put("depthNoiseScaleExponent", 0.5);
		doubleProps.put("mainNoiseScaleX", 80.0);
		doubleProps.put("mainNoiseScaleY", 160.0);
		doubleProps.put("mainNoiseScaleZ", 80.0);
		doubleProps.put("baseSize", 8.5);
		doubleProps.put("stretchY", 12.0);
		doubleProps.put("biomeDepthWeight", 1.0);
		doubleProps.put("biomeDepthOffset", 0.0);
		doubleProps.put("biomeScaleWeight", 1.0);
		doubleProps.put("biomeScaleOffset", 0.0);

		boolProps.put("useCaves", true);
		boolProps.put("useDungeons", true);
		boolProps.put("useStrongholds", true);
		boolProps.put("useVillages", true);
		boolProps.put("useMineShafts", true);
		boolProps.put("useTemples", true);
		boolProps.put("useMonuments", true);
		boolProps.put("useRavines", true);
		boolProps.put("useWaterLakes", true);
		boolProps.put("useLavaLakes", true);
		boolProps.put("useLavaOceans", false);
	}

}
