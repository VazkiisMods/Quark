package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.WoodVariantReplacer;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockCustomButton;
import vazkii.quark.decoration.block.BlockCustomPressurePlate;

public class VariedButtonsAndPressurePlates extends Feature {

	public static Block spruce_pressure_plate, spruce_button;
	public static Block birch_pressure_plate, birch_button;
	public static Block jungle_pressure_plate, jungle_button;
	public static Block acacia_pressure_plate, acacia_button;
	public static Block dark_oak_pressure_plate, dark_oak_button;

	boolean renameVanillaBlocks;
	boolean enablePressurePlates, enableButtons;

	@Override
	public void setupConfig() {
		renameVanillaBlocks = loadPropBool("Prefix vanilla blocks with Oak", "", true);
		enablePressurePlates = loadPropBool("Enable Pressure Plates", "", true);
		enableButtons = loadPropBool("Enable Buttons", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(enablePressurePlates) {
	 		spruce_pressure_plate = new BlockCustomPressurePlate("spruce");
			birch_pressure_plate = new BlockCustomPressurePlate("birch");
			jungle_pressure_plate = new BlockCustomPressurePlate("jungle");
			acacia_pressure_plate = new BlockCustomPressurePlate("acacia");
			dark_oak_pressure_plate = new BlockCustomPressurePlate("dark_oak");
		}
		
		if(enableButtons) {
	 		spruce_button = new BlockCustomButton("spruce");
			birch_button = new BlockCustomButton("birch");
			jungle_button = new BlockCustomButton("jungle");
			acacia_button = new BlockCustomButton("acacia");
			dark_oak_button = new BlockCustomButton("dark_oak");
		}
	}

	@Override
	public void postPreInit(FMLPreInitializationEvent event) {
		if(enablePressurePlates) {
	 		WoodVariantReplacer.addReplacements(1, Blocks.WOODEN_PRESSURE_PLATE);
			
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(spruce_pressure_plate, 1),
					"WW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 1));
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(birch_pressure_plate, 1),
					"WW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 2));
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(jungle_pressure_plate, 1),
					"WW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 3));
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(acacia_pressure_plate, 1),
					"WW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 4));
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(dark_oak_pressure_plate, 1),
					"WW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 5));

			if(renameVanillaBlocks)
				Blocks.WOODEN_PRESSURE_PLATE.setUnlocalizedName("oak_pressure_plate");
		}
		
		if(enableButtons) {
	 		WoodVariantReplacer.addReplacements(1, Blocks.WOODEN_BUTTON);
			
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(spruce_button, 1), ProxyRegistry.newStack(Blocks.PLANKS, 1, 1));
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(birch_button, 1), ProxyRegistry.newStack(Blocks.PLANKS, 1, 2));
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(jungle_button, 1), ProxyRegistry.newStack(Blocks.PLANKS, 1, 3));
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(acacia_button, 1), ProxyRegistry.newStack(Blocks.PLANKS, 1, 4));
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(dark_oak_button, 1), ProxyRegistry.newStack(Blocks.PLANKS, 1, 5));

			if(renameVanillaBlocks)
				Blocks.WOODEN_BUTTON.setUnlocalizedName("oak_button");
		}
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		if(enablePressurePlates) {
			OreDictionary.registerOre("pressurePlateWood", Blocks.WOODEN_PRESSURE_PLATE);
			OreDictionary.registerOre("pressurePlateWood", spruce_pressure_plate);
			OreDictionary.registerOre("pressurePlateWood", birch_pressure_plate);
			OreDictionary.registerOre("pressurePlateWood", jungle_pressure_plate);
			OreDictionary.registerOre("pressurePlateWood", acacia_pressure_plate);
			OreDictionary.registerOre("pressurePlateWood", dark_oak_pressure_plate);
		}

		if(enableButtons) {
			OreDictionary.registerOre("buttonWood", Blocks.WOODEN_BUTTON);
			OreDictionary.registerOre("buttonWood", spruce_button);
			OreDictionary.registerOre("buttonWood", birch_button);
			OreDictionary.registerOre("buttonWood", jungle_button);
			OreDictionary.registerOre("buttonWood", acacia_button);
			OreDictionary.registerOre("buttonWood", dark_oak_button);
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
