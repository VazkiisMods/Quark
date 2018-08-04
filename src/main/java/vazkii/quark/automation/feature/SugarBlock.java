package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockSugar;
import vazkii.quark.base.module.Feature;

public class SugarBlock extends Feature {

	public static Block sugar_block;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		sugar_block = new BlockSugar();
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(sugar_block), 
				"SSS", "SSS", "SSS",
				'S', new ItemStack(Items.SUGAR));
		RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Items.SUGAR, 9), ProxyRegistry.newStack(sugar_block));
	}
	
}
