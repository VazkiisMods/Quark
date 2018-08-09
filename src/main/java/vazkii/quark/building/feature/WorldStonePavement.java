package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockWorldStoneBricks;
import vazkii.quark.building.block.BlockWorldStonePavement;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen;

public class WorldStonePavement extends Feature {

	public static Block world_stone_pavement;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		world_stone_pavement = new BlockWorldStonePavement();
	}
	
	@Override
	public void postPreInit(FMLPreInitializationEvent event) {		
		for(int i = 0; i < 3; i++)
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(world_stone_pavement, 9, i),
					"SSS", "SSS", "SSS",
					'S', ProxyRegistry.newStack(Blocks.STONE, 1, i * 2 + 1));

		if(BlockWorldStoneBricks.Variants.STONE_BASALT_BRICKS.isEnabled()) {
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(world_stone_pavement, 9, 3),
					"SSS", "SSS", "SSS",
					'S', ProxyRegistry.newStack(Basalt.basalt, 1, 0));
		}
		
		if(BlockWorldStoneBricks.Variants.STONE_MARBLE_BRICKS.isEnabled()) {
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(world_stone_pavement, 9, 4),
					"SSS", "SSS", "SSS",
					'S', ProxyRegistry.newStack(RevampStoneGen.marble, 1, 0));
		}
		
		if(BlockWorldStoneBricks.Variants.STONE_LIMESTONE_BRICKS.isEnabled()) {
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(world_stone_pavement, 9, 5),
					"SSS", "SSS", "SSS",
					'S', ProxyRegistry.newStack(RevampStoneGen.limestone, 1, 1));
		}
	}
	
}
