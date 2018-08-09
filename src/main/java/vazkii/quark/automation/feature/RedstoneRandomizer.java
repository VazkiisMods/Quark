package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockRedstoneRandomizer;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.Biotite;

public class RedstoneRandomizer extends Feature {

	public static Block redstone_randomizer;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		redstone_randomizer = new BlockRedstoneRandomizer();
		
		RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(redstone_randomizer), 
				" T ", "TBT", "SSS",
				'T', ProxyRegistry.newStack(Blocks.REDSTONE_TORCH),
				'B', ProxyRegistry.newStack(ModuleLoader.isFeatureEnabled(Biotite.class) ? Biotite.biotite : Items.ENDER_EYE),
				'S', "stone");
	}
	
	
}
