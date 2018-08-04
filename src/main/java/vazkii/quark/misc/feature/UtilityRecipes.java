package vazkii.quark.misc.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;

public class UtilityRecipes extends Feature {

	boolean enableDispenser, enableRepeater, enableMinecarts;
	
	@Override
	public void setupConfig() {
		enableDispenser = loadPropBool("Dispenser Recipe", "", true);
		enableRepeater = loadPropBool("Repeater Recipe", "", true);
		enableMinecarts = loadPropBool("Enable Minecarts", "", true);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(enableDispenser)
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.DISPENSER), 
					"ST ", "SDT", "ST ",
					'S', "string",
					'D', ProxyRegistry.newStack(Blocks.DROPPER),
					'T', "stickWood");
		
		if(enableRepeater)
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Items.REPEATER), 
					"R R", "TRT", "SSS",
					'S', "stone",
					'T', "stickWood",
					'R', "dustRedstone");
		
		if(enableMinecarts) {
			addMinecart(Blocks.CHEST, Items.CHEST_MINECART);
			addMinecart(Blocks.FURNACE, Items.FURNACE_MINECART);
			addMinecart(Blocks.HOPPER, Items.HOPPER_MINECART);
			addMinecart(Blocks.TNT, Items.TNT_MINECART);
		}
	}
	
	private void addMinecart(Block block, Item cart) {
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(cart), 
				"IBI", "III",
				'I', "ingotIron",
				'B', ProxyRegistry.newStack(block));
	}
	
}
