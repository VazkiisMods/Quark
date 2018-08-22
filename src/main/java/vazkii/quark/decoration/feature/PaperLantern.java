package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockPaperLantern;

public class PaperLantern extends Feature {

	public static Block paper_lantern;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		paper_lantern = new BlockPaperLantern();
		
		RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(paper_lantern), 
				"PSP", "PGP", "PSP",
				'P', "paper",
				'S', "stickWood",
				'G', "glowstone");
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
