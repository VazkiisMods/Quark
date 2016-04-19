/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 21:47:12 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockLitLamp;

public class LitLamp extends Feature {

	public static Block lit_lamp;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		lit_lamp = new BlockLitLamp();

		RecipeHandler.addShapelessOreDictRecipe(new ItemStack(lit_lamp), new ItemStack(Blocks.redstone_lamp), new ItemStack(Blocks.redstone_torch));
	}

}
