package vazkii.quark.base.recipe;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.zeta.util.ItemNBTHelper;

/**
 * @author WireSegal
 * Created at 2:08 PM on 8/24/19.
 */
public class DataMaintainingCampfireRecipe extends CampfireCookingRecipe {
	public static final Serializer SERIALIZER = new Serializer();

	private final AbstractCookingRecipe parent;
	private final Ingredient pullDataFrom;

	public DataMaintainingCampfireRecipe(AbstractCookingRecipe parent, Ingredient pullDataFrom) {
		super(parent.getId(), parent.getGroup(), parent.getIngredients().get(0), parent.getResultItem(), parent.getExperience(), parent.getCookingTime());
		this.parent = parent;
		this.pullDataFrom = pullDataFrom;
	}

	@Override
	public boolean matches(@Nonnull Container inv, @Nonnull Level level) {
		return parent.matches(inv, level);
	}

	@Override
	public float getExperience() {
		return parent.getExperience();
	}

	@Override
	public int getCookingTime() {
		return parent.getCookingTime();
	}

	@Nonnull
	@Override
	public ItemStack assemble(@Nonnull Container inv) {
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
	public NonNullList<ItemStack> getRemainingItems(@Nonnull Container inv) {
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

	public static class Serializer implements RecipeSerializer<DataMaintainingCampfireRecipe> {

		@Nonnull
		@Override
		public DataMaintainingCampfireRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
			String trueType = "minecraft:campfire_cooking";

			Ingredient pullFrom = Ingredient.fromJson(json.get("copy_data_from"));

			RecipeSerializer<?> serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(trueType));
			if (serializer == null)
				throw new JsonSyntaxException("Invalid or unsupported recipe type '" + trueType + "'");
			Recipe<?> parent = serializer.fromJson(recipeId, json);
			if (!(parent instanceof AbstractCookingRecipe cookingRecipe))
				throw new JsonSyntaxException("Type '" + trueType + "' is not a cooking recipe");

			return new DataMaintainingCampfireRecipe(cookingRecipe, pullFrom);
		}

		@Nonnull
		@Override
		public DataMaintainingCampfireRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
			Ingredient pullFrom = Ingredient.fromNetwork(buffer);

			String trueType = "minecraft:campfire_cooking";

			RecipeSerializer<?> serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(trueType));
			if (serializer == null)
				throw new IllegalArgumentException("Invalid or unsupported recipe type '" + trueType + "'");
			Recipe<?> parent = serializer.fromNetwork(recipeId, buffer);
			if (!(parent instanceof AbstractCookingRecipe cookingRecipe))
				throw new IllegalArgumentException("Type '" + trueType + "' is not a cooking recipe");

			return new DataMaintainingCampfireRecipe(cookingRecipe, pullFrom);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull DataMaintainingCampfireRecipe recipe) {
			recipe.pullDataFrom.toNetwork(buffer);
			((RecipeSerializer<Recipe<?>>) recipe.parent.getSerializer()).toNetwork(buffer, recipe.parent);
		}
	}
}
