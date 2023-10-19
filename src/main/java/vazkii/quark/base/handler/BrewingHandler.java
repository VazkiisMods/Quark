package vazkii.quark.base.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import vazkii.quark.base.Quark;
import vazkii.quark.base.recipe.ingredient.FlagIngredient;
import vazkii.quark.mixin.accessor.AccessorPotionBrewing;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.bus.LoadEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author WireSegal
 * Created at 3:34 PM on 9/23/19.
 */
public class BrewingHandler {

	public static void addPotionMix(String flag, Supplier<Ingredient> reagent, MobEffect effect) {
		addPotionMix(flag, reagent, effect, null);
	}

	public static void addPotionMix(String flag, Supplier<Ingredient> reagent, MobEffect effect,
									int normalTime, int longTime, int strongTime) {
		addPotionMix(flag, reagent, effect, null, normalTime, longTime, strongTime);
	}

	public static void addPotionMix(String flag, Supplier<Ingredient> reagent, MobEffect effect,
									@Nullable MobEffect negation) {
		addPotionMix(flag, reagent, effect, negation, 3600, 9600, 1800);
	}


	public static void addPotionMix(String flag, Supplier<Ingredient> reagent, MobEffect effect,
									@Nullable MobEffect negation, int normalTime, int longTime, int strongTime) {
		ResourceLocation loc = Quark.ZETA.registry.getRegistryName(effect, Registry.MOB_EFFECT);

		if (loc != null) {
			String baseName = loc.getPath();
			boolean hasStrong = strongTime > 0;

			Potion normalType = addPotion(new MobEffectInstance(effect, normalTime), baseName, baseName);
			Potion longType = addPotion(new MobEffectInstance(effect, longTime), baseName, "long_" + baseName);
			Potion strongType = !hasStrong ? null : addPotion(new MobEffectInstance(effect, strongTime, 1), baseName, "strong_" + baseName);

			addPotionMix(flag, reagent, normalType, longType, strongType);

			if (negation != null) {
				ResourceLocation negationLoc = Quark.ZETA.registry.getRegistryName(negation, Registry.MOB_EFFECT);
				if (negationLoc != null) {
					String negationBaseName = negationLoc.getPath();

					Potion normalNegationType = addPotion(new MobEffectInstance(negation, normalTime), negationBaseName, negationBaseName);
					Potion longNegationType = addPotion(new MobEffectInstance(negation, longTime), negationBaseName, "long_" + negationBaseName);
					Potion strongNegationType = !hasStrong ? null : addPotion(new MobEffectInstance(negation, strongTime, 1), negationBaseName, "strong_" + negationBaseName);

					addNegation(flag, normalType, longType, strongType, normalNegationType, longNegationType, strongNegationType);
				}
			}
		}

	}

	public static void addPotionMix(String flag, Supplier<Ingredient> reagent, Potion normalType, Potion longType, @Nullable Potion strongType) {
		boolean hasStrong = strongType != null;

		add(flag, Potions.AWKWARD, reagent, normalType);
		add(flag, Potions.WATER, reagent, Potions.MUNDANE);

		if (hasStrong)
			add(flag, normalType, BrewingHandler::glowstone, strongType);
		add(flag, normalType, BrewingHandler::redstone, longType);
	}

	public static void addNegation(String flag, Potion normalType, Potion longType, @Nullable Potion strongType,
								   Potion normalNegatedType, Potion longNegatedType, @Nullable Potion strongNegatedType) {
		add(flag, normalType, BrewingHandler::spiderEye, normalNegatedType);

		boolean hasStrong = strongType != null && strongNegatedType != null;

		if (hasStrong) {
			add(flag, strongType, BrewingHandler::spiderEye, strongNegatedType);
			add(flag, normalNegatedType, BrewingHandler::glowstone, strongNegatedType);
		}
		add(flag, longType, BrewingHandler::spiderEye, longNegatedType);
		add(flag, normalNegatedType, BrewingHandler::redstone, longNegatedType);

	}

	public static ItemStack of(Item potionType, Potion potion) {
		ItemStack stack = new ItemStack(potionType);
		PotionUtils.setPotion(stack, potion);
		return stack;
	}

	private static final Map<Potion, String> flags = Maps.newHashMap();

	private static boolean isInjectionPrepared = false;
	private static final List<Triple<Potion, Supplier<Ingredient>, Potion>> toRegister = Lists.newArrayList();

	@LoadEvent
	public static void setup(ZCommonSetup event) {
		isInjectionPrepared = true;
		for (Triple<Potion, Supplier<Ingredient>, Potion> triple : toRegister)
			addBrewingRecipe(triple.getLeft(), triple.getMiddle().get(), triple.getRight());

		toRegister.clear();
	}

	private static void add(String flag, Potion potion, Supplier<Ingredient> reagent, Potion to) {
		flags.put(to, flag);
		if (isInjectionPrepared)
			addBrewingRecipe(potion, new FlagIngredient(reagent.get(), flag), to);
		else
			toRegister.add(new ImmutableTriple<>(potion, () -> new FlagIngredient(reagent.get(), flag), to));
	}

	private static void addBrewingRecipe(Potion input, Ingredient reagent, Potion output) {
		AccessorPotionBrewing.quark$getPotionMixes().add(new PotionBrewing.Mix<>(ForgeRegistries.POTIONS, input, reagent, output));
	}

	private static Potion addPotion(MobEffectInstance eff, String baseName, String name) {
		Potion effect = new Potion(Quark.MOD_ID + "." + baseName, eff);
		Quark.ZETA.registry.register(effect, name, Registry.POTION_REGISTRY);

		return effect;
	}

	private static Ingredient redstone() {
		return Ingredient.of(Items.REDSTONE);
	}

	private static Ingredient glowstone() {
		return Ingredient.of(Items.GLOWSTONE_DUST);
	}

	private static Ingredient spiderEye() {
		return Ingredient.of(Items.FERMENTED_SPIDER_EYE);
	}

	public static boolean isEnabled(Potion potion) {
		if (!flags.containsKey(potion))
			return true;
		return FlagIngredient.Serializer.INSTANCE.flagManager().getFlag(flags.get(potion));
	}
}
