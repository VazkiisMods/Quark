/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 22:37:10 (GMT)]
 */
package vazkii.quark.building;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.building.feature.BarkBlocks;
import vazkii.quark.building.feature.CarvedWood;
import vazkii.quark.building.feature.CharredNetherBricks;
import vazkii.quark.building.feature.DuskboundBlocks;
import vazkii.quark.building.feature.FramedGlass;
import vazkii.quark.building.feature.HardenedClayTiles;
import vazkii.quark.building.feature.IronPlates;
import vazkii.quark.building.feature.MagmaBricks;
import vazkii.quark.building.feature.MidoriBlocks;
import vazkii.quark.building.feature.MoreSandstone;
import vazkii.quark.building.feature.PolishedNetherrack;
import vazkii.quark.building.feature.PolishedStone;
import vazkii.quark.building.feature.QuiltedWool;
import vazkii.quark.building.feature.ReedBlock;
import vazkii.quark.building.feature.SandyBricks;
import vazkii.quark.building.feature.SnowBricks;
import vazkii.quark.building.feature.SoulSandstone;
import vazkii.quark.building.feature.StainedPlanks;
import vazkii.quark.building.feature.SturdyStone;
import vazkii.quark.building.feature.Thatch;
import vazkii.quark.building.feature.Trowel;
import vazkii.quark.building.feature.VanillaStairsAndSlabs;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.building.feature.VerticalWoodPlanks;
import vazkii.quark.building.feature.WorldStoneBricks;
import vazkii.quark.building.feature.WorldStonePavement;

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
		registerFeature(new CharredNetherBricks());
		registerFeature(new MoreSandstone());
		registerFeature(new MidoriBlocks());
		registerFeature(new IronPlates());
		registerFeature(new VerticalWoodPlanks());
		registerFeature(new SoulSandstone());
		registerFeature(new StainedPlanks());
		registerFeature(new PolishedNetherrack());
		registerFeature(new DuskboundBlocks());
		registerFeature(new SturdyStone());
		registerFeature(new QuiltedWool());
		registerFeature(new Trowel());
		registerFeature(new MagmaBricks());
		registerFeature(new WorldStonePavement());
		registerFeature(new FramedGlass());
	}

	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Blocks.BRICK_BLOCK);
	}
	
}
