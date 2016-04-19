/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [18/03/2016, 22:34:52 (GMT)]
 */
package vazkii.quark.world;

import vazkii.quark.base.module.Module;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.Biotite;
import vazkii.quark.world.feature.ClayGeneration;
import vazkii.quark.world.feature.DefaultWorldOptions;
import vazkii.quark.world.feature.MushroomsInSwamps;
import vazkii.quark.world.feature.OceanGuardians;

public class QuarkWorld extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new Basalt());
		registerFeature(new ClayGeneration(), "Generate clay underground like dirt");
		registerFeature(new OceanGuardians(), "Guardians spawn in oceans");
		registerFeature(new MushroomsInSwamps(), "Big mushrooms generate in swamps");
		registerFeature(new Biotite());
		registerFeature(new DefaultWorldOptions());
	}

}
