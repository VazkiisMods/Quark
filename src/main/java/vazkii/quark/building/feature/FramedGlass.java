package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockFramedGlass;
import vazkii.quark.building.block.BlockFramedGlassPane;

public class FramedGlass extends Feature {

	public static Block framed_glass;
	public static Block framed_glass_pane;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		framed_glass = new BlockFramedGlass();
		framed_glass_pane = new BlockFramedGlassPane();
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(framed_glass, 4), 
				"GIG", "IGI", "GIG",
				'G', ProxyRegistry.newStack(Blocks.GLASS),
				'I', "ingotIron");
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(framed_glass_pane, 16), 
				"GGG", "GGG",
				'G', ProxyRegistry.newStack(framed_glass));
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
