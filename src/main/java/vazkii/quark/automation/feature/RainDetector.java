/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/04/2016, 19:56:53 (GMT)]
 */
package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.automation.block.BlockRainDetector;
import vazkii.quark.automation.tile.TileRainDetector;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.Biotite;

public class RainDetector extends Feature {

	public static Block rain_detector;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		rain_detector = new BlockRainDetector();
		registerTile(TileRainDetector.class, "rain_detector");
		ModIntegrationHandler.addCharsetCarry(rain_detector);

		RecipeHandler.addOreDictRecipe(new ItemStack(rain_detector),
				"GGG", "BBB", "PPP",
				'G', new ItemStack(Blocks.GLASS),
				'B', ModuleLoader.isFeatureEnabled(Biotite.class) ? "gemEnderBiotite" : new ItemStack(Blocks.OBSIDIAN),
						'P', new ItemStack(Blocks.PURPUR_SLAB));
	}

}
