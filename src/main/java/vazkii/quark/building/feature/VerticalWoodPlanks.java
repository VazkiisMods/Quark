package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.block.BlockVerticalPlanks;
import vazkii.quark.building.block.BlockVerticalStainedPlanks;

public class VerticalWoodPlanks extends Feature {

	public static Block vertical_planks;
	public static Block vertical_stained_planks;

	boolean enableVerticalStained = true;
	
	@Override
	public void setupConfig() {
		enableVerticalStained = loadPropBool("Enable Vertical Stained Planks", "", enableVerticalStained);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		vertical_planks = new BlockVerticalPlanks();

		for(int i = 0; i < 6; i++) {
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(vertical_planks, 3, i),
					"W", "W", "W",
					'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, i));
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.PLANKS, 3, i),
					"W", "W", "W",
					'W', ProxyRegistry.newStack(vertical_planks, 1, i));		
		}

		if(ModuleLoader.isFeatureEnabled(StainedPlanks.class) && enableVerticalStained)
			vertical_stained_planks = new BlockVerticalStainedPlanks();
	}

	@Override
	public void postPreInit(FMLPreInitializationEvent event) {		
		if(ModuleLoader.isFeatureEnabled(StainedPlanks.class) && enableVerticalStained)
			for(int i = 0; i < 16; i++) {
				RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(vertical_stained_planks, 3, i),
						"W", "W", "W",
						'W', ProxyRegistry.newStack(StainedPlanks.stained_planks, 1, i));
				RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(StainedPlanks.stained_planks, 3, i),
						"W", "W", "W",
						'W', ProxyRegistry.newStack(vertical_stained_planks, 1, i));		
			}
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		OreDictionary.registerOre("plankWood", ProxyRegistry.newStack(vertical_planks, 1, OreDictionary.WILDCARD_VALUE));

		if(ModuleLoader.isFeatureEnabled(StainedPlanks.class) && enableVerticalStained) {
			OreDictionary.registerOre("plankWood", ProxyRegistry.newStack(vertical_stained_planks, 1, OreDictionary.WILDCARD_VALUE));
			OreDictionary.registerOre("plankStained", ProxyRegistry.newStack(vertical_stained_planks, 1, OreDictionary.WILDCARD_VALUE));
		}
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
