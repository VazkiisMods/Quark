package org.violetmoon.zeta.registry;

import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.recipe.FlagIngredient;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author WireSegal
 * Created at 3:34 PM on 9/23/19.
 */
public abstract class BrewingRegistry {

	protected final Zeta zeta;

	public BrewingRegistry(Zeta zeta) {
		this.zeta = zeta;
	}

	public void addPotionMix(String flag, Supplier<Ingredient> reagent, MobEffect effect) {
		addPotionMix(flag, reagent, effect, null);
	}

	public void addPotionMix(String flag, Supplier<Ingredient> reagent, MobEffect effect,
									int normalTime, int longTime, int strongTime) {
		addPotionMix(flag, reagent, effect, null, normalTime, longTime, strongTime);
	}

	public void addPotionMix(String flag, Supplier<Ingredient> reagent, MobEffect effect,
									@Nullable MobEffect negation) {
		addPotionMix(flag, reagent, effect, negation, 3600, 9600, 1800);
	}


	public void addPotionMix(String flag, Supplier<Ingredient> reagent, MobEffect effect,
									@Nullable MobEffect negation, int normalTime, int longTime, int strongTime) {
		ResourceLocation loc = zeta.registry.getRegistryName(effect, Registry.MOB_EFFECT);

		if (loc != null) {
			String baseName = loc.getPath();
			boolean hasStrong = strongTime > 0;

			Potion normalType = registerPotion(new MobEffectInstance(effect, normalTime), baseName, baseName);
			Potion longType = registerPotion(new MobEffectInstance(effect, longTime), baseName, "long_" + baseName);
			Potion strongType = !hasStrong ? null : registerPotion(new MobEffectInstance(effect, strongTime, 1), baseName, "strong_" + baseName);

			addPotionMix(flag, reagent, normalType, longType, strongType);

			if (negation != null) {
				ResourceLocation negationLoc = zeta.registry.getRegistryName(negation, Registry.MOB_EFFECT);
				if (negationLoc != null) {
					String negationBaseName = negationLoc.getPath();

					Potion normalNegationType = registerPotion(new MobEffectInstance(negation, normalTime), negationBaseName, negationBaseName);
					Potion longNegationType = registerPotion(new MobEffectInstance(negation, longTime), negationBaseName, "long_" + negationBaseName);
					Potion strongNegationType = !hasStrong ? null : registerPotion(new MobEffectInstance(negation, strongTime, 1), negationBaseName, "strong_" + negationBaseName);

					addNegation(flag, normalType, longType, strongType, normalNegationType, longNegationType, strongNegationType);
				}
			}
		}

	}

	public void addPotionMix(String flag, Supplier<Ingredient> reagent, Potion normalType, Potion longType, @Nullable Potion strongType) {
		boolean hasStrong = strongType != null;

		addFlaggedRecipe(flag, Potions.AWKWARD, reagent, normalType);
		addFlaggedRecipe(flag, Potions.WATER, reagent, Potions.MUNDANE);

		if (hasStrong)
			addFlaggedRecipe(flag, normalType, BrewingRegistry::glowstone, strongType);
		addFlaggedRecipe(flag, normalType, BrewingRegistry::redstone, longType);
	}

	public void addNegation(String flag, Potion normalType, Potion longType, @Nullable Potion strongType,
								   Potion normalNegatedType, Potion longNegatedType, @Nullable Potion strongNegatedType) {
		addFlaggedRecipe(flag, normalType, BrewingRegistry::spiderEye, normalNegatedType);

		boolean hasStrong = strongType != null && strongNegatedType != null;

		if (hasStrong) {
			addFlaggedRecipe(flag, strongType, BrewingRegistry::spiderEye, strongNegatedType);
			addFlaggedRecipe(flag, normalNegatedType, BrewingRegistry::glowstone, strongNegatedType);
		}
		addFlaggedRecipe(flag, longType, BrewingRegistry::spiderEye, longNegatedType);
		addFlaggedRecipe(flag, normalNegatedType, BrewingRegistry::redstone, longNegatedType);

	}

	protected final Map<Potion, String> flaggedPotions = Maps.newHashMap();

	protected void addFlaggedRecipe(String flag, Potion potion, Supplier<Ingredient> reagent, Potion to) {
		flaggedPotions.put(to, flag);
		Supplier<Ingredient> flagIngredientSupplier = () -> new FlagIngredient(
			reagent.get(),
			flag,
			zeta.configManager.getConfigFlagManager(),
			zeta.configManager.getConfigFlagManager().flagIngredientSerializer
		);

		addBrewingRecipe(potion, flagIngredientSupplier, to);
	}

	protected Potion registerPotion(MobEffectInstance eff, String baseName, String name) {
		Potion effect = new Potion(zeta.modid + "." + baseName, eff);
		zeta.registry.register(effect, name, Registry.POTION_REGISTRY);

		return effect;
	}

	public boolean isEnabled(Potion potion) {
		if (!flaggedPotions.containsKey(potion))
			return true;
		return zeta.configManager.getConfigFlagManager().getFlag(flaggedPotions.get(potion));
	}

	public static Ingredient redstone() {
		return Ingredient.of(Items.REDSTONE);
	}

	public static Ingredient glowstone() {
		return Ingredient.of(Items.GLOWSTONE_DUST);
	}

	public static Ingredient spiderEye() {
		return Ingredient.of(Items.FERMENTED_SPIDER_EYE);
	}

	protected abstract void addBrewingRecipe(Potion potion, Supplier<Ingredient> reagent, Potion result);
}
