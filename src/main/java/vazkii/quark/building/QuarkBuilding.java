/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [18/03/2016, 22:37:10 (GMT)]
 */
package vazkii.quark.building;

import vazkii.quark.base.module.Module;
import vazkii.quark.building.feature.BarkBlocks;
import vazkii.quark.building.feature.CarvedWood;
import vazkii.quark.building.feature.HardenedClayTiles;
import vazkii.quark.building.feature.PolishedStone;
import vazkii.quark.building.feature.ReedBlock;
import vazkii.quark.building.feature.SandyBricks;
import vazkii.quark.building.feature.SnowBricks;
import vazkii.quark.building.feature.Thatch;
import vazkii.quark.building.feature.VanillaStairsAndSlabs;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.building.feature.WorldStoneBricks;

public class QuarkBuilding extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new HardenedClayTiles());
		registerFeature(new VanillaStairsAndSlabs());
		registerFeature(new WorldStoneBricks());
		registerFeature(new Thatch());
		registerFeature(new SandyBricks());
		registerFeature(new ReedBlock(), "Sugar cane blocks");
		registerFeature(new BarkBlocks());
		registerFeature(new VanillaWalls());
		registerFeature(new PolishedStone());
		registerFeature(new CarvedWood());
		registerFeature(new SnowBricks());
	}

}
