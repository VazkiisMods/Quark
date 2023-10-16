package vazkii.quark.base.recipe;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.zeta.util.ItemNBTHelper;

/**
 * @author WireSegal
 * Created at 2:08 PM on 8/24/19.
 */
public class DataMaintainingRecipe implements CraftingRecipe {
	public static final Serializer SERIALIZER = new Serializer();

	private final CraftingRecipe parent;
	private final Ingredient pullDataFrom;

	public DataMaintainingRecipe(CraftingRecipe parent, Ingredient pullDataFrom) {
		this.parent = parent;
		this.pullDataFrom = pullDataFrom;
	}

	@Override
	public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level worldIn) {
		return parent.matches(inv, worldIn);
	}

	@Nonnull
	@Override
	public ItemStack assemble(@Nonnull CraftingContainer inv) {
		ItemStack stack = parent.assemble(inv);
		for(int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack inInv = inv.getItem(i);
			if (pullDataFrom.test(inInv)) {
				CompoundTag tag = ItemNBTHelper.getNBT(inInv);
				if (!tag.isEmpty())
					stack.getOrCreateTag().merge(tag);
				break;
			}
		}

		return stack;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return parent.canCraftInDimensions(width, height);
	}

	@Nonnull
	@Override
	public ItemStack getResultItem() {
		return parent.getResultItem();
	}

	@Nonnull
	@Override
	public ResourceLocation getId() {
		return parent.getId();
	}

	@Nonnull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Nonnull
	@Override
	public RecipeType<?> getType() {
		return parent.getType();
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingContainer inv) {
		return parent.getRemainingItems(inv);
	}

	@Nonnull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return parent.getIngredients();
	}

	@Override
	public boolean isSpecial() {
		return parent.isSpecial();
	}

	@Nonnull
	@Override
	public String getGroup() {
		return parent.getGroup();
	}

	@Nonnull
	@Override
	public ItemStack getToastSymbol() {
		return parent.getToastSymbol();
	}

	private static class ShapedDataMaintainingRecipe extends DataMaintainingRecipe implements IShapedRecipe<CraftingContainer> {
		private final IShapedRecipe<CraftingContainer> parent;

		@SuppressWarnings("unchecked")
		public ShapedDataMaintainingRecipe(CraftingRecipe parent, Ingredient pullDataFrom) {
			super(parent, pullDataFrom);
			this.parent = (IShapedRecipe<CraftingContainer>) parent;
		}

		@Override
		public int getRecipeWidth() {
			return parent.getRecipeWidth();
		}

		@Override
		public int getRecipeHeight() {
			return parent.getRecipeHeight();
		}
	}

	public static class Serializer implements RecipeSerializer<DataMaintainingRecipe> {

		@Nonnull
		@Override
		public DataMaintainingRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
			String trueType = GsonHelper.getAsString(json, "true_type");
			if (trueType.equals("quark:maintaining"))
				throw new JsonSyntaxException("Recipe type circularity");

			Ingredient pullFrom = Ingredient.fromJson(json.get("copy_data_from"));

			RecipeSerializer<?> serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(trueType));
			if (serializer == null)
				throw new JsonSyntaxException("Invalid or unsupported recipe type '" + trueType + "'");
			Recipe<?> parent = serializer.fromJson(recipeId, json);
			if (!(parent instanceof CraftingRecipe craftingRecipe))
				throw new JsonSyntaxException("Type '" + trueType + "' is not a crafting recipe");

			if (parent instanceof IShapedRecipe)
				return new ShapedDataMaintainingRecipe(craftingRecipe, pullFrom);
			return new DataMaintainingRecipe(craftingRecipe, pullFrom);
		}

		@Nonnull
		@Override
		public DataMaintainingRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
			Ingredient pullFrom = Ingredient.fromNetwork(buffer);

			String trueType = buffer.readUtf(32767);

			RecipeSerializer<?> serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(trueType));
			if (serializer == null)
				throw new IllegalArgumentException("Invalid or unsupported recipe type '" + trueType + "'");
			Recipe<?> parent = serializer.fromNetwork(recipeId, buffer);
			if (!(parent instanceof CraftingRecipe craftingRecipe))
				throw new IllegalArgumentException("Type '" + trueType + "' is not a crafting recipe");

			if (parent instanceof IShapedRecipe)
				return new ShapedDataMaintainingRecipe(craftingRecipe, pullFrom);
			return new DataMaintainingRecipe(craftingRecipe, pullFrom);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull DataMaintainingRecipe recipe) {
			recipe.pullDataFrom.toNetwork(buffer);
			buffer.writeUtf(Objects.toString(Registry.RECIPE_SERIALIZER.getKey(recipe.parent.getSerializer())), 32767);
			((RecipeSerializer<Recipe<?>>) recipe.parent.getSerializer()).toNetwork(buffer, recipe.parent);
		}
	}
}
