/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [18/03/2016, 23:01:43 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import com.google.common.collect.ImmutableSet;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import vazkii.quark.base.module.Feature;

public class StackableItems extends Feature {

	//	int potions;
	int minecarts;

	@Override
	public void setupConfig() {
		//		potions = loadPropInt("Potions", "", 8);
		minecarts = loadPropInt("Minecarts", "", 16);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		//		ImmutableSet.<Item>of(Items.potionitem, Items.splash_potion, Items.lingering_potion)
		//			.forEach(item -> item.setMaxStackSize(potions));

		ImmutableSet.<Item>of(Items.minecart, Items.chest_minecart, Items.command_block_minecart, Items.furnace_minecart, Items.hopper_minecart, Items.tnt_minecart)
		.forEach(item -> item.setMaxStackSize(minecarts));
	}

}
