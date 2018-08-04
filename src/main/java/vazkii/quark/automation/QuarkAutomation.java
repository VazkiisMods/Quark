/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 22:36:24 (GMT)]
 */
package vazkii.quark.automation;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.automation.feature.AnimalsEatFloorFood;
import vazkii.quark.automation.feature.Chute;
import vazkii.quark.automation.feature.DispenserRecords;
import vazkii.quark.automation.feature.DispensersPlaceBlocks;
import vazkii.quark.automation.feature.DispensersPlaceSeeds;
import vazkii.quark.automation.feature.EnderWatcher;
import vazkii.quark.automation.feature.MetalButtons;
import vazkii.quark.automation.feature.ObsidianPressurePlate;
import vazkii.quark.automation.feature.PistonSpikes;
import vazkii.quark.automation.feature.PistonsMoveTEs;
import vazkii.quark.automation.feature.PistonsPushPullItems;
import vazkii.quark.automation.feature.RainDetector;
import vazkii.quark.automation.feature.SugarBlock;
import vazkii.quark.base.module.Module;

public class QuarkAutomation extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new ObsidianPressurePlate());
		registerFeature(new DispensersPlaceSeeds());
		registerFeature(new RainDetector());
		registerFeature(new EnderWatcher());
		registerFeature(new PistonSpikes(), "Piston Block Breakers");
		registerFeature(new AnimalsEatFloorFood());
		registerFeature(new PistonsMoveTEs());
		registerFeature(new DispensersPlaceBlocks());
		registerFeature(new DispenserRecords());
		registerFeature(new PistonsPushPullItems(), "Pistons Push and Pull Items");
		registerFeature(new Chute());
		registerFeature(new MetalButtons());
		registerFeature(new SugarBlock());
	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Items.REDSTONE);
	}

}
