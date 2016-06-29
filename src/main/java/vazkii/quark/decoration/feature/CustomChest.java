/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package vazkii.quark.decoration.feature;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockBlazeLantern;
import vazkii.quark.decoration.block.BlockCustomChest;

public class CustomChest extends Feature
{
    public static BlockCustomChest custom_chest;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        custom_chest = new BlockCustomChest();
//        RecipeHandler.addOreDictRecipe(new ItemStack(custom_chest),
//                "BPB", "PPP", "BPB",
//                'B', new ItemStack(Items.BLAZE_ROD),
//                'P', new ItemStack(Items.BLAZE_POWDER));
    }
}
