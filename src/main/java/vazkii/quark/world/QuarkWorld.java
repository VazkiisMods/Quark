/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 22:34:52 (GMT)]
 */
package vazkii.quark.world;

import vazkii.quark.base.module.Module;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.Biotite;
import vazkii.quark.world.feature.BuriedTreasure;
import vazkii.quark.world.feature.ClayGeneration;
import vazkii.quark.world.feature.CrystalCaves;
import vazkii.quark.world.feature.DefaultWorldOptions;
import vazkii.quark.world.feature.DepthMobs;
import vazkii.quark.world.feature.MushroomsInSwamps;
import vazkii.quark.world.feature.NaturalBlazesInNether;
import vazkii.quark.world.feature.OceanGuardians;
import vazkii.quark.world.feature.PirateShips;
import vazkii.quark.world.feature.RealisticWorldType;
import vazkii.quark.world.feature.RevampStoneGen;
import vazkii.quark.world.feature.VariedDungeons;
import vazkii.quark.world.feature.Wraiths;

public class QuarkWorld extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new Basalt());
		registerFeature(new ClayGeneration(), "Generate clay underground like dirt");
		registerFeature(new OceanGuardians(), "Guardians spawn in oceans");
		registerFeature(new NaturalBlazesInNether(), "Blazes spawn naturally in the nether");
		registerFeature(new MushroomsInSwamps(), "Big mushrooms generate in swamps");
		registerFeature(new Biotite());
		registerFeature(new BuriedTreasure());
		registerFeature(new DepthMobs());
		registerFeature(new PirateShips());
		registerFeature(new Wraiths());
		registerFeature(new RevampStoneGen());
		registerFeature(new CrystalCaves());
		registerFeature(new VariedDungeons());

		registerFeature(new RealisticWorldType());
		registerFeature(new DefaultWorldOptions());
	}

}
