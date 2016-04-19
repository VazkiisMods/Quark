/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 22:33:44 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockMod;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockBlazeLantern;

public class BlazeLantern extends Feature {

	public static BlockMod blaze_lantern;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		blaze_lantern = new BlockBlazeLantern();

		RecipeHandler.addOreDictRecipe(new ItemStack(blaze_lantern),
				"BPB", "PPP", "BPB",
				'B', new ItemStack(Items.blaze_rod),
				'P', new ItemStack(Items.blaze_powder));
	}

}
