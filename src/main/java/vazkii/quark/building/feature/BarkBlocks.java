/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [24/03/2016, 15:24:42 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockMod;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockBark;

public class BarkBlocks extends Feature {

	public static BlockMod bark;

	boolean enableWalls;

	@Override
	public void setupConfig() {
		enableWalls = loadPropBool("Enable walls", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		bark = new BlockBark();

		for(int i = 0; i < 6; i++) {
			ItemStack log = new ItemStack(i > 3 ? Blocks.log2 : Blocks.log, 1, i % 4);

			RecipeHandler.addOreDictRecipe(new ItemStack(bark, 4, i),
					"WW", "WW",
					'W', log);
			RecipeHandler.addShapelessOreDictRecipe(log, new ItemStack(bark, 1, i));
		}

		for(BlockBark.Variants variant : BlockBark.Variants.class.getEnumConstants()) {
			bark.getDefaultState().withProperty(bark.getVariantProp(), variant);
			String name = variant.getName();
			VanillaWalls.add(name, bark, variant.ordinal(), enableWalls);
		}
	}

}
