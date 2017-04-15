/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/04/2016, 21:45:11 (GMT)]
 */
package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.automation.block.BlockEnderWatcher;
import vazkii.quark.automation.tile.TileEnderWatcher;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.Biotite;

public class EnderWatcher extends Feature {

	public static Block ender_watcher;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		ender_watcher = new BlockEnderWatcher();
		registerTile(TileEnderWatcher.class, "ender_watcher");
		ModIntegrationHandler.addCharsetCarry(ender_watcher);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		RecipeHandler.addOreDictRecipe(new ItemStack(ender_watcher),
				"BRB", "RER", "BRB",
				'R', new ItemStack(Items.REDSTONE),
				'B', ModuleLoader.isFeatureEnabled(Biotite.class) ? new ItemStack(Biotite.biotite_block) : new ItemStack(Blocks.OBSIDIAN),
						'E', new ItemStack(Items.ENDER_EYE));
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "Botania" };
	}

}
