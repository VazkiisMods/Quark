package vazkii.quark.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.tools.item.AncientTomeItem;
import vazkii.quark.tools.module.AncientTomesModule;
import vazkii.quark.tools.module.PickarangModule;
import vazkii.quark.tweaks.recipe.ElytraDuplicationRecipe;
import vazkii.quark.vanity.module.ColorRunesModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class QuarkJeiPlugin implements IModPlugin {
	private static final ResourceLocation UID = new ResourceLocation(Quark.MOD_ID, Quark.MOD_ID);

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.useNbtForSubtypes(AncientTomesModule.ancient_tome);
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) { //TODO check if this works properly once JEI is fixed
		registration.getCraftingCategory().addCategoryExtension(ElytraDuplicationRecipe.class, ElytraDuplicationExtension::new);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

		if (ModuleLoader.INSTANCE.isModuleEnabled(AncientTomesModule.class))
			registerAncientTomeAnvilRecipes(registration, factory);

		if (ModuleLoader.INSTANCE.isModuleEnabled(ColorRunesModule.class))
			registerRuneAnvilRecipes(registration, factory);

		if (ModuleLoader.INSTANCE.isModuleEnabled(PickarangModule.class))
			registerPickarangAnvilRepairs(registration, factory);
	}

	private void registerAncientTomeAnvilRecipes(IRecipeRegistration registration, IVanillaRecipeFactory factory) {
		List<Object> recipes = new ArrayList<>();
		for (Enchantment enchant : AncientTomesModule.validEnchants) {
			EnchantmentData data = new EnchantmentData(enchant, enchant.getMaxLevel());
			recipes.add(factory.createAnvilRecipe(EnchantedBookItem.getEnchantedItemStack(data),
					Collections.singletonList(AncientTomeItem.getEnchantedItemStack(data)),
					Collections.singletonList(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(data.enchantment, data.enchantmentLevel + 1)))));
		}
		registration.addRecipes(recipes, VanillaRecipeCategoryUid.ANVIL);
	}

	private void registerRuneAnvilRecipes(IRecipeRegistration registration, IVanillaRecipeFactory factory) {
		Random random = new Random();
		List<ItemStack> used = Stream.of(Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE,
				Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE,
				Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS, Items.ELYTRA, Items.SHIELD, Items.BOW, Items.CROSSBOW,
				Items.CARROT_ON_A_STICK, Items.FISHING_ROD, Items.SHEARS)
				.map(item -> makeEnchantedDisplayItem(item, random))
				.collect(Collectors.toList());

		if (ModuleLoader.INSTANCE.isModuleEnabled(PickarangModule.class)) {
			used.add(makeEnchantedDisplayItem(PickarangModule.pickarang, random));
		}

		List<Object> recipes = new ArrayList<>();
		for (Item rune : ColorRunesModule.runesTag.getAllElements()) {
			ItemStack runeStack = new ItemStack(rune);
			recipes.add(factory.createAnvilRecipe(used, Collections.singletonList(runeStack),
					used.stream().map(stack -> {
						ItemStack output = stack.copy();
						ItemNBTHelper.setBoolean(output, ColorRunesModule.TAG_RUNE_ATTACHED, true);
						ItemNBTHelper.setCompound(output, ColorRunesModule.TAG_RUNE_COLOR, runeStack.serializeNBT());
						return output;
					}).collect(Collectors.toList())));
		}
		registration.addRecipes(recipes, VanillaRecipeCategoryUid.ANVIL);
	}

	private void registerPickarangAnvilRepairs(IRecipeRegistration registration, IVanillaRecipeFactory factory) {
		//Repair ratios taken from JEI anvil maker
		ItemStack nearlyBroken = new ItemStack(PickarangModule.pickarang);
		nearlyBroken.setDamage(nearlyBroken.getMaxDamage());
		ItemStack veryDamaged = nearlyBroken.copy();
		veryDamaged.setDamage(veryDamaged.getMaxDamage() * 3 / 4);
		ItemStack damaged = nearlyBroken.copy();
		damaged.setDamage(damaged.getMaxDamage() * 2 / 4);

		Object materialRepair = factory.createAnvilRecipe(nearlyBroken,
				Collections.singletonList(new ItemStack(Items.DIAMOND)), Collections.singletonList(veryDamaged));
		Object toolRepair = factory.createAnvilRecipe(veryDamaged,
				Collections.singletonList(veryDamaged), Collections.singletonList(damaged));

		registration.addRecipes(Arrays.asList(materialRepair, toolRepair), VanillaRecipeCategoryUid.ANVIL);
	}

	// Runes only show up and can be only anvilled on enchanted items, so make some random enchanted items
	private static ItemStack makeEnchantedDisplayItem(Item input, Random random) {
		ItemStack stack = new ItemStack(input);
		if (input.getItemEnchantability() <= 0) { // If it can't take anything in ench. tables... 
			stack.addEnchantment(Enchantments.UNBREAKING, 3); // it probably accepts unbreaking anyways
			return stack;
		}
		return EnchantmentHelper.addRandomEnchantment(random, stack, 25, false);
	}
}
