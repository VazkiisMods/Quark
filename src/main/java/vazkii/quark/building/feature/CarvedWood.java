/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [30/03/2016, 18:42:41 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockMod;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockCarvedWood;

public class CarvedWood extends Feature {

	public static BlockMod carvedWood;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		carvedWood = new BlockCarvedWood();

		for(int i = 0; i < 6; i++)
			RecipeHandler.addOreDictRecipe(new ItemStack(carvedWood, 2, i),
					"WW", "WW",
					'W', new ItemStack(Blocks.wooden_slab, 1, i));
	}

}
