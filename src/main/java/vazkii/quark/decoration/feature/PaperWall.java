/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [24/03/2016, 03:18:35 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockPaperWall;

public class PaperWall extends Feature {

	public static Block paper_wall, paper_wall_big;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		paper_wall = new BlockPaperWall("paper_wall");
		paper_wall_big = new BlockPaperWall("paper_wall_big");

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(paper_wall, 6),
				"SSS", "PPP", "SSS",
				'S', ProxyRegistry.newStack(Items.STICK),
				'P', ProxyRegistry.newStack(Items.PAPER));
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(paper_wall_big, 4),
				"PP", "PP",
				'P', ProxyRegistry.newStack(paper_wall));
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}

