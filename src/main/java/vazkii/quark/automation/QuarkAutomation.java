/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [18/03/2016, 22:36:24 (GMT)]
 */
package vazkii.quark.automation;

import vazkii.quark.automation.feature.DispensersPlaceSeeds;
import vazkii.quark.automation.feature.EnderWatcher;
import vazkii.quark.automation.feature.ObsidianPressurePlate;
import vazkii.quark.automation.feature.RainDetector;
import vazkii.quark.base.module.Module;

public class QuarkAutomation extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new ObsidianPressurePlate());
		registerFeature(new DispensersPlaceSeeds());
		registerFeature(new RainDetector());
		registerFeature(new EnderWatcher());
	}

}
