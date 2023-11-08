package org.violetmoon.quark.api.event;

import com.google.common.collect.Multimap;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.eventbus.api.Event;

import java.util.Collection;

public abstract class RecipeCrawlEvent extends Event {

	public static class Reset extends RecipeCrawlEvent {}
	public static class CrawlStarting extends RecipeCrawlEvent {}
	
	public static class Digest extends RecipeCrawlEvent {
		
		private final Multimap<Item, ItemStack> digestion;
		private final Multimap<Item, ItemStack> backwardsDigestion;
		
		public Digest(Multimap<Item, ItemStack> digestion, Multimap<Item, ItemStack> backwardsDigestion) {
			this.digestion = digestion;
			this.backwardsDigestion = backwardsDigestion;
		}
		
		public boolean has(Item item, boolean backwards) {
			return (backwards ? backwardsDigestion : digestion).containsKey(item);
		}
		
		public Collection<ItemStack> get(Item item, boolean backwards) {
			return (backwards ? backwardsDigestion : digestion).get(item);
		}
		
	}
	
	public static abstract class Visit<T extends Recipe<?>> extends RecipeCrawlEvent {
		
		public final T recipe;
		public final ResourceLocation recipeID;
		public final ItemStack output;
		public final NonNullList<Ingredient> ingredients;
		
		public Visit(T recipe) {
			this.recipe = recipe;
			this.recipeID = recipe.getId();
			this.output = recipe.getResultItem();
			this.ingredients = recipe.getIngredients();
		}
		
		public static class Shaped extends Visit<ShapedRecipe> {

			public Shaped(ShapedRecipe recipe) {
				super(recipe);
			}
			
		}
		
		public static class Shapeless extends Visit<ShapelessRecipe> {

			public Shapeless(ShapelessRecipe recipe) {
				super(recipe);
			}
			
		}
		
		public static class Custom extends Visit<CustomRecipe> {

			public Custom(CustomRecipe recipe) {
				super(recipe);
			}
			
		}
		
		public static class Cooking extends Visit<AbstractCookingRecipe> {

			public Cooking(AbstractCookingRecipe recipe) {
				super(recipe);
			}
			
		}
		
		public static class Misc extends Visit<Recipe<?>> {

			public Misc(Recipe<?> recipe) {
				super(recipe);
			}
			
		}
		
	}
	
}

