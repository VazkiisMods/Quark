package vazkii.quark.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.tweaks.recipe.ElytraDuplicationRecipe;

import javax.annotation.Nullable;

public class ElytraDuplicationExtension implements ICraftingCategoryExtension {
	private final ElytraDuplicationRecipe recipe;

	ElytraDuplicationExtension(ElytraDuplicationRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
	}

	@Override
	public void drawInfo(int recipeWidth, int recipeHeight, double mouseX, double mouseY) {
//		Minecraft.getInstance().fontRenderer.drawString(I18n.format("quark.jei.makes_copy"), 60, 46, 0x555555); TODO verify position once this is functional
	}

	@Nullable
	@Override
	public ResourceLocation getRegistryName() {
		return recipe.getId();
	}
}
