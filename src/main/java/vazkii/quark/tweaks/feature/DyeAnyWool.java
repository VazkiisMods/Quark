package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.QuiltedWool;

public class DyeAnyWool extends Feature {

	boolean add8WoolRecipe;

	@Override
	public void setupConfig() {
		add8WoolRecipe = loadPropBool("Add 8 Dyed Wool Recipe", "", true);
	}

	@Override
	public void postPreInit(FMLPreInitializationEvent event) {
		for(int i = 0; i < 16; i++) {
			String dye = LibMisc.OREDICT_DYES.get(15 - i);

			addRecipe(Blocks.WOOL, i, dye);
			if(ModuleLoader.isFeatureEnabled(QuiltedWool.class))
				addRecipe(QuiltedWool.quilted_wool, i, dye);
		}
	}

	private void addRecipe(Block block, int meta, String dye) {
		ItemStack in = ProxyRegistry.newStack(block, 1, OreDictionary.WILDCARD_VALUE);
		RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(block, 1, meta), in, dye);

		if(add8WoolRecipe)
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(block, 8, meta), 
					dye, in, in, in, in, in, in, in, in);
	}

}
