/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [18/03/2016, 22:32:56 (GMT)]
 */
package vazkii.quark.tweaks;

import vazkii.quark.base.module.Module;
import vazkii.quark.tweaks.feature.AngryCreepers;
import vazkii.quark.tweaks.feature.ArmedArmorStands;
import vazkii.quark.tweaks.feature.ArrowSafeMobs;
import vazkii.quark.tweaks.feature.BabyZombiesBurn;
import vazkii.quark.tweaks.feature.ChickensShedFeathers;
import vazkii.quark.tweaks.feature.DragonsBreathBottleless;
import vazkii.quark.tweaks.feature.EndermenTeleportYou;
import vazkii.quark.tweaks.feature.GlassShards;
import vazkii.quark.tweaks.feature.GreenerGrass;
import vazkii.quark.tweaks.feature.JumpBoostStepAssist;
import vazkii.quark.tweaks.feature.KnockOnDoors;
import vazkii.quark.tweaks.feature.LessIntrusiveShields;
import vazkii.quark.tweaks.feature.LookDownLadders;
import vazkii.quark.tweaks.feature.MinecartInteraction;
import vazkii.quark.tweaks.feature.NoPotionShift;
import vazkii.quark.tweaks.feature.NoteBlocksMobSounds;
import vazkii.quark.tweaks.feature.RightClickSignEdit;
import vazkii.quark.tweaks.feature.ShearableChickens;
import vazkii.quark.tweaks.feature.SheepArmor;
import vazkii.quark.tweaks.feature.SlabsToBlocks;
import vazkii.quark.tweaks.feature.SnowGolemPlayerHeads;
import vazkii.quark.tweaks.feature.StackableItems;
import vazkii.quark.tweaks.feature.StairsMakeMore;

public class QuarkTweaks extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new StackableItems());
		registerFeature(new LookDownLadders(), "Look down on ladders to descend fast");
		registerFeature(new RightClickSignEdit());
		registerFeature(new ChickensShedFeathers());
		registerFeature(new AngryCreepers(), "Creepers turn red when they're exploding");
		registerFeature(new GlassShards());
		registerFeature(new StairsMakeMore(), "Stair crafting makes more");
		registerFeature(new SlabsToBlocks(), "Slabs to blocks recipe");
		registerFeature(new ArrowSafeMobs(), "Ridable mobs are immune to rider's arrows");
		registerFeature(new JumpBoostStepAssist(), "Jump boost allows to step up 1 block");
		registerFeature(new DragonsBreathBottleless(), "Dragon's Breath doesn't leave a bottle behind");
		registerFeature(new KnockOnDoors());
		registerFeature(new SnowGolemPlayerHeads(), "Named snow golems with pumpkins drop player heads if killed by a witch");
		registerFeature(new LessIntrusiveShields());
		registerFeature(new NoteBlocksMobSounds(), "Note blocks play mob sounds if there's a head attached");
		registerFeature(new ArmedArmorStands());
		registerFeature(new BabyZombiesBurn());
		registerFeature(new GreenerGrass());
		registerFeature(new NoPotionShift());
		registerFeature(new ShearableChickens());
		registerFeature(new MinecartInteraction(), "Right click minecarts to add blocks to them");
		registerFeature(new EndermenTeleportYou(), "Endermen teleport you to them if you're in a 2 high area");
		registerFeature(new SheepArmor(), "Sheep have armor while wearing wool");
	}

}
