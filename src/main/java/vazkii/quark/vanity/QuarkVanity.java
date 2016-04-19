/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [18/03/2016, 22:36:08 (GMT)]
 */
package vazkii.quark.vanity;

import vazkii.quark.base.module.Module;
import vazkii.quark.vanity.feature.DyableElytra;
import vazkii.quark.vanity.feature.EmoteSystem;
import vazkii.quark.vanity.feature.FireworkCloning;
import vazkii.quark.vanity.feature.SitInStairs;

public class QuarkVanity extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new DyableElytra());
		registerFeature(new FireworkCloning());
		registerFeature(new EmoteSystem());
		registerFeature(new SitInStairs());
	}

}
