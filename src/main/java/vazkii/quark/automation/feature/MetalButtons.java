package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockMetalButton;
import vazkii.quark.base.module.Feature;

public class MetalButtons extends Feature {

	public static Block iron_button, gold_button;
	
	boolean enableIron, enableGold;
	
	@Override
	public void setupConfig() {
		enableIron = loadPropBool("Enable Iron Button", "", true);
		enableGold = loadPropBool("Enable Gold Button", "", true);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(enableIron) {
			iron_button = new BlockMetalButton("iron", 100);
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(iron_button, 1), ProxyRegistry.newStack(Blocks.WOODEN_BUTTON), "ingotIron");
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(iron_button, 1), "buttonWood", "ingotIron");
		}
		
		if(enableIron) {
			gold_button = new BlockMetalButton("gold", 4);
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(gold_button, 1), ProxyRegistry.newStack(Blocks.WOODEN_BUTTON), "ingotGold");
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(gold_button, 1), "buttonWood", "ingotGold");
		}
	}
	
}
