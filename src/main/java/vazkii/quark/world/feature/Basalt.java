/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 15:05:14 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.block.BlockMod;
import vazkii.quark.base.block.BlockModSlab;
import vazkii.quark.base.block.BlockModStairs;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockBasalt;
import vazkii.quark.world.block.BlockBasaltSlab;
import vazkii.quark.world.block.BlockBasaltStairs;
import vazkii.quark.world.world.BasaltGenerator;

public class Basalt extends Feature {

	public static BlockMod basalt;

	boolean nether, overworld;
	int clusterSizeNether, clusterSizeOverworld;
	int clusterCountNether, clusterCountOverworld;
	boolean enableStairsAndSlabs;
	boolean enableWalls;

	@Override
	public void setupConfig() {
		nether = loadPropBool("Generate in nether", "", true);
		overworld = loadPropBool("Generate in overworld", "", false);
		clusterSizeNether = loadPropInt("Nether cluster size", "", 80);
		clusterSizeOverworld = loadPropInt("Overworld cluster size", "", 33);
		clusterCountNether = loadPropInt("Nether cluster count", "", 1);
		clusterCountOverworld = loadPropInt("Overworld cluster count", "", 10);
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true);
		enableWalls = loadPropBool("Enable walls", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		basalt = new BlockBasalt();

		if(enableStairsAndSlabs) {
			BlockModSlab.initSlab(basalt, 0, new BlockBasaltSlab(false), new BlockBasaltSlab(true));
			BlockModStairs.initStairs(basalt, 0, new BlockBasaltStairs());
		}
		VanillaWalls.add("basalt", basalt, 0, enableWalls);

		RecipeHandler.addOreDictRecipe(new ItemStack(basalt, 4, 1),
				"BB", "BB",
				'B', new ItemStack(basalt, 1, 0));

		ItemStack blackItem = new ItemStack(Items.coal);
		if(ModuleLoader.isFeatureEnabled(Biotite.class))
			blackItem = new ItemStack(Biotite.biotite); 

		RecipeHandler.addOreDictRecipe(new ItemStack(basalt, 4, 0),
				"BI", "IB",
				'B', new ItemStack(Blocks.cobblestone, 1, 0),
				'I', blackItem);
		RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Blocks.stone, 1, 5), new ItemStack(basalt), new ItemStack(Items.quartz));

		GameRegistry.registerWorldGenerator(new BasaltGenerator(nether, overworld, clusterSizeOverworld, clusterSizeNether, clusterCountOverworld, clusterCountNether), 0);
	}

}
