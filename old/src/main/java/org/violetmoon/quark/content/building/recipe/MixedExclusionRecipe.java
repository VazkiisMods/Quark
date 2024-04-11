package org.violetmoon.quark.content.building.recipe;

import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;

import org.jetbrains.annotations.NotNull;

public class MixedExclusionRecipe implements CraftingRecipe, IShapedRecipe<CraftingContainer> {

	public static final RecipeSerializer<MixedExclusionRecipe> SERIALIZER = new Serializer();

	private final ResourceLocation res;
	private NonNullList<Ingredient> ingredients;

	private final String type;
	private final ItemStack output;
	private final TagKey<Item> tag;
	private final ItemStack placeholder;

	public MixedExclusionRecipe(ResourceLocation res, String type, ItemStack output, TagKey<Item> tag, ItemStack placeholder) {
		this.res = res;

		this.type = type;
		this.output = output;
		this.tag = tag;
		this.placeholder = placeholder;
	}

	public static MixedExclusionRecipe forChest(String type, ResourceLocation res, boolean log) {
		ItemStack output = new ItemStack(Items.CHEST, (log ? 4 : 1));
		TagKey<Item> tag = (log ? ItemTags.LOGS : ItemTags.PLANKS);
		ItemStack placeholder = new ItemStack(log ? Items.OAK_LOG : Items.OAK_PLANKS);
		return new MixedExclusionRecipe(res, type, output, tag, placeholder);
	}

	public static MixedExclusionRecipe forFurnace(String type, ResourceLocation res) {
		ItemStack output = new ItemStack(Items.FURNACE);
		TagKey<Item> tag = ItemTags.STONE_CRAFTING_MATERIALS;
		ItemStack placeholder = new ItemStack(Items.COBBLESTONE);
		return new MixedExclusionRecipe(res, type, output, tag, placeholder);
	}

	@Override
	public boolean canCraftInDimensions(int x, int y) {
		return x == 3 && y == 3;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer arg0, RegistryAccess acc) {
		return output.copy();
	}

	@NotNull
	@Override
	public ResourceLocation getId() {
		return res;
	}

	@NotNull
	@Override
	public ItemStack getResultItem(RegistryAccess acc) {
		return output.copy();
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	@Override
	public boolean matches(CraftingContainer inv, @NotNull Level world) {
		if(inv.getItem(4).isEmpty()) {
			ItemStack first = null;
			boolean foundDifference = false;

			for(int i = 0; i < 9; i++)
				if(i != 4) { // ignore center
					ItemStack stack = inv.getItem(i);
					if(!stack.isEmpty() && stack.is(tag)) {
						if(first == null)
							first = stack;
						else if(!ItemStack.isSameItem(first, stack))
							foundDifference = true;
					} else
						return false;
				}

			return foundDifference;
		}

		return false;
	}

	@Override
	public int getRecipeWidth() {
		return 3;
	}

	@Override
	public int getRecipeHeight() {
		return 3;
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		if(ingredients == null) {
			NonNullList<Ingredient> list = NonNullList.withSize(9, Ingredient.EMPTY);
			Ingredient ingr = Ingredient.of(placeholder);
			for(int i = 0; i < 8; i++)
				list.set(i < 4 ? i : i + 1, ingr);
			ingredients = list;
		}

		return ingredients;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	private static class Serializer implements RecipeSerializer<MixedExclusionRecipe> {

		@NotNull
		@Override
		public MixedExclusionRecipe fromJson(@NotNull ResourceLocation arg0, JsonObject arg1) {
			String type = arg1.get("subtype").getAsString();
			return forType(arg0, type);
		}

		@Override
		public MixedExclusionRecipe fromNetwork(@NotNull ResourceLocation arg0, FriendlyByteBuf arg1) {
			return forType(arg0, arg1.readUtf());
		}

		@Override
		public void toNetwork(FriendlyByteBuf arg0, MixedExclusionRecipe arg1) {
			arg0.writeUtf(arg1.type);
		}

		private MixedExclusionRecipe forType(ResourceLocation res, String type) {
			return switch(type) {
			case "chest" -> MixedExclusionRecipe.forChest(type, res, false);
			case "chest4" -> MixedExclusionRecipe.forChest(type, res, true);
			case "furnace" -> MixedExclusionRecipe.forFurnace(type, res);
			default -> null;
			};
		}

	}

}
