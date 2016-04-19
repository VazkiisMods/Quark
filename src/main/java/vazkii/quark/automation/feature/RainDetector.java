/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 19:56:53 (GMT)]
 */
package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.automation.block.BlockRainDetector;
import vazkii.quark.automation.tile.TileRainDetector;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.Biotite;

public class RainDetector extends Feature {

	public static Block rain_detector;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		rain_detector = new BlockRainDetector();
		registerTile(TileRainDetector.class, "rain_detector");
		
		RecipeHandler.addOreDictRecipe(new ItemStack(rain_detector), 
				"GGG", "BBB", "PPP",
				'G', new ItemStack(Blocks.glass),
				'B', (ModuleLoader.isFeatureEnabled(Biotite.class) ? "gemEnderBiotite" : new ItemStack(Blocks.obsidian)),
				'P', new ItemStack(Blocks.purpur_slab));
	}
	
}
