package vazkii.quark.building.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.block.BlockQuarkSlab;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockSoulSandstone;
import vazkii.quark.building.block.slab.BlockSoulSandstoneSlab;
import vazkii.quark.building.block.stairs.BlockSoulSandstoneStairs;

public class SoulSandstone extends Feature {

	public static BlockMod soul_sandstone;

	boolean enableStairs;
	boolean enableWalls;

	@Override
	public void setupConfig() {
		enableStairs = loadPropBool("Enable stairs", "", true);
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		soul_sandstone = new BlockSoulSandstone();

		RecipeHandler.addOreDictRecipe(new ItemStack(soul_sandstone),
				"SS", "SS",
				'S', new ItemStack(Blocks.SOUL_SAND));
		RecipeHandler.addOreDictRecipe(new ItemStack(soul_sandstone, 4, 2),
				"SS", "SS",
				'S', new ItemStack(soul_sandstone, 1, 0));

		IBlockState defaultState = soul_sandstone.getDefaultState();

		String slabName = "_slab";
		BlockQuarkSlab halfSlab = new BlockSoulSandstoneSlab(false);
		BlockModSlab.initSlab(soul_sandstone, OreDictionary.WILDCARD_VALUE, halfSlab, new BlockSoulSandstoneSlab(true));
		
		RecipeHandler.addOreDictRecipe(new ItemStack(soul_sandstone, 1, 1),
				"S", "S",
				'S', new ItemStack(halfSlab, 1, 0));
		
		if(enableStairs)
			BlockModStairs.initStairs(soul_sandstone, 0, new BlockSoulSandstoneStairs());
		
		VanillaWalls.add("soul_sandstone", soul_sandstone, 0, enableWalls);

	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}


}
