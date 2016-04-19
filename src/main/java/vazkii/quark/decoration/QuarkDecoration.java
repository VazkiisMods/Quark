/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [18/03/2016, 22:35:41 (GMT)]
 */
package vazkii.quark.decoration;

import vazkii.quark.base.module.Module;
import vazkii.quark.decoration.feature.BlazeLantern;
import vazkii.quark.decoration.feature.LitLamp;
import vazkii.quark.decoration.feature.MoreBanners;
import vazkii.quark.decoration.feature.PaperWall;
import vazkii.quark.decoration.feature.VariedTrapdoors;

public class QuarkDecoration extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new LitLamp());
		registerFeature(new BlazeLantern());
		registerFeature(new PaperWall());
		registerFeature(new VariedTrapdoors());
		registerFeature(new MoreBanners());
	}

}
