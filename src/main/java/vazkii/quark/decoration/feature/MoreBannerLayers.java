package vazkii.quark.decoration.feature;

import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class MoreBannerLayers extends Feature {

	public static int layers;
	
	@Override
	public void setupConfig() {
		layers = loadPropInt("Survival Layer Count", "", 16);
	}
	
	public static int getLayerCount() {
		return ModuleLoader.isFeatureEnabled(MoreBannerLayers.class) ? layers : 6;
	}
	

}
