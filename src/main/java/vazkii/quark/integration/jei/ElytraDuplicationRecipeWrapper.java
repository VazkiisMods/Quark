package vazkii.quark.integration.jei;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import vazkii.quark.misc.feature.EnderdragonScales;
import vazkii.quark.misc.recipe.ElytraDuplicationRecipe;

public class ElytraDuplicationRecipeWrapper implements ICraftingRecipeWrapper {
	private final ElytraDuplicationRecipe recipe;

	public ElytraDuplicationRecipeWrapper(ElytraDuplicationRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		List<ItemStack> inputs = Arrays.asList(
			new ItemStack(Items.ELYTRA),
			new ItemStack(EnderdragonScales.enderdragonScale)
		);
		ingredients.setInputs(VanillaTypes.ITEM, inputs);
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		minecraft.fontRenderer.drawString("Makes copy", 60, 46, Color.gray.getRGB());
	}
}
