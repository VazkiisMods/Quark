/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 15:05:14 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockBasalt;
import vazkii.quark.world.block.slab.BlockBasaltSlab;
import vazkii.quark.world.block.stairs.BlockBasaltStairs;
import vazkii.quark.world.world.BasaltGenerator;

public class Basalt extends Feature {

	public static BlockMod basalt;

	DimensionConfig dims;
	int clusterSizeNether, clusterSizeOverworld;
	int clusterCountNether, clusterCountOverworld;
	boolean enableStairsAndSlabs;
	boolean enableWalls;

	@Override
	public void setupConfig() {
		clusterSizeNether = loadPropInt("Nether cluster size", "", 80);
		clusterSizeOverworld = loadPropInt("Overworld cluster size", "", 33);
		clusterCountNether = loadPropInt("Nether cluster count", "", 1);
		clusterCountOverworld = loadPropInt("Overworld cluster count", "", 10);
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
		dims = new DimensionConfig(configCategory, false, "-1");
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		basalt = new BlockBasalt();

		if(enableStairsAndSlabs) {
			BlockModSlab.initSlab(basalt, 0, new BlockBasaltSlab(false), new BlockBasaltSlab(true));
			BlockModStairs.initStairs(basalt, 0, new BlockBasaltStairs());
		}
		VanillaWalls.add("basalt", basalt, 0, enableWalls);

		OreDictionary.registerOre("stoneBasalt", new ItemStack(basalt, 1, 0));
		OreDictionary.registerOre("stoneBasaltPolished", new ItemStack(basalt, 1, 1));

		RecipeHandler.addOreDictRecipe(new ItemStack(basalt, 4, 1),
				"BB", "BB",
				'B', new ItemStack(basalt, 1, 0));

		GameRegistry.registerWorldGenerator(new BasaltGenerator(dims, clusterSizeOverworld, clusterSizeNether, clusterCountOverworld, clusterCountNether), 0);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		ItemStack blackItem = new ItemStack(Items.COAL);
		if(ModuleLoader.isFeatureEnabled(Biotite.class))
			blackItem = new ItemStack(Biotite.biotite);

		RecipeHandler.addOreDictRecipe(new ItemStack(basalt, 4, 0),
				"BI", "IB",
				'B', new ItemStack(Blocks.COBBLESTONE, 1, 0),
				'I', blackItem);
		RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Blocks.STONE, 1, 5), new ItemStack(basalt), new ItemStack(Items.QUARTZ));
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
