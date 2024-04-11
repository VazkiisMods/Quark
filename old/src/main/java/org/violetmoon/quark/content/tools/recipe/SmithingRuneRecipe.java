package org.violetmoon.quark.content.tools.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.client.module.ImprovedTooltipsModule;
import org.violetmoon.quark.content.client.tooltip.EnchantedBookTooltips;
import org.violetmoon.quark.content.tools.base.RuneColor;
import org.violetmoon.quark.content.tools.module.ColorRunesModule;
import org.violetmoon.quark.content.tools.module.PickarangModule;
import org.violetmoon.zeta.module.IDisableable;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * @author WireSegal
 * Created at 9:54 AM on 12/23/23.
 */
public final class SmithingRuneRecipe extends SmithingTrimRecipe { // Extends to allow JEI to pick it up

	public static final Serializer SERIALIZER = new Serializer();
	private final ResourceLocation id;
	private final Ingredient template;
	private final Ingredient addition;
	private final RuneColor runeColor;

	private static Ingredient used;
	private static final RandomSource BASE_INGREDIENT_RANDOM = RandomSource.createThreadSafe();

	private static ItemStack makeEnchantedDisplayItem(ItemStack input) {
		ItemStack stack = input.copy();
		stack.hideTooltipPart(ItemStack.TooltipPart.ENCHANTMENTS);
		stack.hideTooltipPart(ItemStack.TooltipPart.MODIFIERS);
		stack.setHoverName(Component.translatable("quark.jei.any_enchanted"));
		if(Quark.ZETA.itemExtensions.get(stack).getEnchantmentValueZeta(stack) <= 0) { // If it can't take anything in ench. tables...
			stack.enchant(Enchantments.UNBREAKING, 3); // it probably accepts unbreaking anyways
			return stack;
		}
		return EnchantmentHelper.enchantItem(BASE_INGREDIENT_RANDOM, stack, 25, false);
	}

	private static Ingredient createBaseIngredient() {
		if (used == null) {
			Stream<ItemStack> displayItems;
			if (Quark.ZETA.modules.isEnabled(ImprovedTooltipsModule.class) && ImprovedTooltipsModule.enchantingTooltips) {
				displayItems = EnchantedBookTooltips.getTestItems().stream();
			} else {
				displayItems = Stream.of(Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE,
					Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE,
					Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS, Items.ELYTRA, Items.SHIELD, Items.BOW, Items.CROSSBOW,
					Items.TRIDENT, Items.FISHING_ROD, Items.SHEARS, PickarangModule.pickarang).map(ItemStack::new);
			}

			used = Ingredient.of(displayItems
				.filter(it -> !(it.getItem() instanceof IDisableable<?> dis) || dis.isEnabled())
				.map(SmithingRuneRecipe::makeEnchantedDisplayItem));
		}

		return used;
	}

	private SmithingRuneRecipe(ResourceLocation id, Ingredient template, Ingredient addition, RuneColor runeColor) {
		super(id, template, createBaseIngredient(), addition);
		this.id = id;
		this.template = template;
		this.addition = addition;
		this.runeColor = runeColor;
	}

	@Override
	public boolean matches(Container container, @Nonnull Level level) {
		return isTemplateIngredient(container.getItem(0)) && isBaseIngredient(container.getItem(1)) && isAdditionIngredient(container.getItem(2));
	}

	@Nonnull
	@Override
	public ItemStack assemble(Container container, @Nonnull RegistryAccess registry) {
		ItemStack baseItem = container.getItem(1);
		if (isBaseIngredient(baseItem)) {
			if (ColorRunesModule.getStackColor(baseItem) == runeColor)
				return ItemStack.EMPTY;

			ItemStack newStack = baseItem.copy();
			newStack.setCount(1);
			return ColorRunesModule.withRune(newStack, runeColor);
		}

		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack getResultItem(@Nonnull RegistryAccess registry) {
		ItemStack displayStack = makeEnchantedDisplayItem(new ItemStack(Items.IRON_CHESTPLATE));
		ColorRunesModule.withRune(displayStack, runeColor);

		return displayStack;
	}

	@Override
	public boolean isTemplateIngredient(@Nonnull ItemStack stack) {
		return this.template.test(stack);
	}

	@Override
	public boolean isBaseIngredient(@Nonnull ItemStack stack) {
		return ColorRunesModule.canHaveRune(stack);
	}

	@Override
	public boolean isAdditionIngredient(@Nonnull ItemStack stack) {
		if (this.addition.isEmpty())
			return stack.isEmpty();
		return this.addition.test(stack);
	}

	@Nonnull
	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Nonnull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<SmithingRuneRecipe> {

		@Nonnull
		@Override
		public SmithingRuneRecipe fromJson(@Nonnull ResourceLocation id, @Nonnull JsonObject serialized) {
			Ingredient template = Ingredient.fromJson(GsonHelper.getNonNull(serialized, "template"));

			JsonElement additionElement = serialized.get("addition");
			Ingredient addition = additionElement != null && !additionElement.isJsonNull() ? Ingredient.fromJson(additionElement) : Ingredient.EMPTY;

			RuneColor runeColor = RuneColor.byName(GsonHelper.getAsString(serialized, "color"));
			if (runeColor == null)
				throw new JsonSyntaxException("Rune color must be a valid dye color, rainbow, or blank");
			return new SmithingRuneRecipe(id, template, addition, runeColor);
		}

		@Override
		public SmithingRuneRecipe fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buf) {
			Ingredient template = Ingredient.fromNetwork(buf);
			Ingredient addition = buf.readBoolean() ? Ingredient.EMPTY : Ingredient.fromNetwork(buf);
			RuneColor runeColor = buf.readEnum(RuneColor.class);
			return new SmithingRuneRecipe(id, template, addition, runeColor);
		}

		@Override
		public void toNetwork(@Nonnull FriendlyByteBuf buf, SmithingRuneRecipe recipe) {
			recipe.template.toNetwork(buf);
			boolean additionIsEmpty = recipe.addition.isEmpty();
			buf.writeBoolean(additionIsEmpty);
			if (!additionIsEmpty)
				recipe.addition.toNetwork(buf);

			buf.writeEnum(recipe.runeColor);
		}
	}
}
