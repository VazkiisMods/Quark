package org.violetmoon.quark.content.tweaks.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;
import org.violetmoon.quark.content.tweaks.module.DragonScalesModule;

import javax.annotation.Nonnull;

public class ElytraDuplicationRecipe extends CustomRecipe {

	public static final SimpleRecipeSerializer<?> SERIALIZER = new SimpleRecipeSerializer<>(ElytraDuplicationRecipe::new);

	public ElytraDuplicationRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(@Nonnull CraftingContainer var1, @Nonnull Level var2) {
		int sources = 0;
		boolean foundTarget = false;

		for(int i = 0; i < var1.getContainerSize(); i++) {
			ItemStack stack = var1.getItem(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof ElytraItem) {
					if(foundTarget)
						return false;
					foundTarget = true;
				} else if(stack.getItem() == DragonScalesModule.dragon_scale) {
					if(sources >= 1)
						return false;
					sources++;
				} else return false;
			}
		}

		return sources == 1 && foundTarget;
	}

	@Nonnull
	@Override
	public ItemStack assemble(@Nonnull CraftingContainer var1) {
		return getResultItem();
	}

	@Nonnull
	@Override
	public ItemStack getResultItem() {
		ItemStack stack = new ItemStack(Items.ELYTRA);
//		if(EnderdragonScales.dyeBlack && ModuleLoader.isFeatureEnabled(DyableElytra.class))
//			ItemNBTHelper.setInt(stack, DyableElytra.TAG_ELYTRA_DYE, 0);

		return stack;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> ret = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

		for(int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if(stack.getItem() == Items.ELYTRA)
				ret.set(i, stack.copy());
		}

		return ret;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return (width * height) >= 2;
	}

	@Override
	@Nonnull
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.withSize(2, Ingredient.EMPTY);
		list.set(0, Ingredient.of(new ItemStack(Items.ELYTRA)));
		list.set(1, Ingredient.of(new ItemStack(DragonScalesModule.dragon_scale)));
		return list;
	}

	@Nonnull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

}
