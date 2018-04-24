/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 22:32:56 (GMT)]
 */
package vazkii.quark.tweaks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.tweaks.feature.ArmedArmorStands;
import vazkii.quark.tweaks.feature.AutomaticRecipeUnlock;
import vazkii.quark.tweaks.feature.AxesBreakLeaves;
import vazkii.quark.tweaks.feature.BabyZombiesBurn;
import vazkii.quark.tweaks.feature.BlastproofShulkerBoxes;
import vazkii.quark.tweaks.feature.ChickensShedFeathers;
import vazkii.quark.tweaks.feature.CompassesWorkEverywhere;
import vazkii.quark.tweaks.feature.ConvertClay;
import vazkii.quark.tweaks.feature.DeployLaddersDown;
import vazkii.quark.tweaks.feature.DoubleDoors;
import vazkii.quark.tweaks.feature.DragonsBreathBottleless;
import vazkii.quark.tweaks.feature.DyeAnyWool;
import vazkii.quark.tweaks.feature.EndermenAntiCheese;
import vazkii.quark.tweaks.feature.GlassShards;
import vazkii.quark.tweaks.feature.HoeSickle;
import vazkii.quark.tweaks.feature.ImprovedSleeping;
import vazkii.quark.tweaks.feature.ImprovedStoneToolCrafting;
import vazkii.quark.tweaks.feature.JumpBoostStepAssist;
import vazkii.quark.tweaks.feature.KnockOnDoors;
import vazkii.quark.tweaks.feature.LookDownLadders;
import vazkii.quark.tweaks.feature.MinecartInteraction;
import vazkii.quark.tweaks.feature.QuickArmorSwapping;
import vazkii.quark.tweaks.feature.RemoveSnowLayers;
import vazkii.quark.tweaks.feature.RightClickSignEdit;
import vazkii.quark.tweaks.feature.ShearableChickens;
import vazkii.quark.tweaks.feature.SheepArmor;
import vazkii.quark.tweaks.feature.SlabsToBlocks;
import vazkii.quark.tweaks.feature.SpongeDriesInNether;
import vazkii.quark.tweaks.feature.SquidsInkYou;
import vazkii.quark.tweaks.feature.StackableItems;
import vazkii.quark.tweaks.feature.StairsMakeMore;
import vazkii.quark.tweaks.feature.TorchesBurnInFurnaces;
import vazkii.quark.tweaks.feature.VillagerOpenDoubleDoors;
import vazkii.quark.tweaks.feature.VillagerPursueEmeralds;

public class QuarkTweaks extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new StackableItems());
		registerFeature(new LookDownLadders(), "Look down on ladders to descend fast");
		registerFeature(new RightClickSignEdit());
		registerFeature(new ChickensShedFeathers());
		registerFeature(new GlassShards());
		registerFeature(new StairsMakeMore(), "Stair crafting makes more");
		registerFeature(new SlabsToBlocks(), "Slabs to blocks recipe");
		registerFeature(new JumpBoostStepAssist(), "Jump boost allows to step up 1 block");
		registerFeature(new DragonsBreathBottleless(), "Dragon's Breath doesn't leave a bottle behind");
		registerFeature(new KnockOnDoors());
		registerFeature(new ArmedArmorStands());
		registerFeature(new BabyZombiesBurn());
		registerFeature(new ShearableChickens());
		registerFeature(new MinecartInteraction(), "Right click minecarts to add blocks to them");
		registerFeature(new EndermenAntiCheese());
		registerFeature(new SheepArmor(), "Sheep have armor while wearing wool");
		registerFeature(new ImprovedStoneToolCrafting(), "Allow crafting stone tools using flint");
		registerFeature(new ConvertClay(), "Convert Clay Blocks to Clay");
		registerFeature(new RemoveSnowLayers(), "Remove layers of snow by right clicking with a shovel");
		registerFeature(new HoeSickle(), "Hoes break a 5x5 of plants");
		registerFeature(new DeployLaddersDown());
		registerFeature(new DoubleDoors(), "Double doors open together");
		registerFeature(new TorchesBurnInFurnaces());
		registerFeature(new VillagerPursueEmeralds(), "Villagers chase players holding Emerald Blocks");
		registerFeature(new SpongeDriesInNether(), "Wet sponge will dry in the nether");
		registerFeature(new QuickArmorSwapping());
		registerFeature(new AutomaticRecipeUnlock());
		registerFeature(new ImprovedSleeping());
		registerFeature(new AxesBreakLeaves());
		registerFeature(new CompassesWorkEverywhere());
		registerFeature(new BlastproofShulkerBoxes());
		registerFeature(new DyeAnyWool());
		registerFeature(new SquidsInkYou());
		registerFeature(new VillagerOpenDoubleDoors());
	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Items.IRON_PICKAXE);
	}

}
