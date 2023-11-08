package org.violetmoon.quark.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.function.BooleanSupplier;

public interface IAdvancementModifierDelegate {
	IAdvancementModifier createAdventuringTimeMod(Set<ResourceKey<Biome>> locations);
	IAdvancementModifier createBalancedDietMod(Set<ItemLike> items);
	IAdvancementModifier createFuriousCocktailMod(BooleanSupplier isPotion, Set<MobEffect> effects);
	IAdvancementModifier createMonsterHunterMod(Set<EntityType<?>> types);
	IAdvancementModifier createTwoByTwoMod(Set<EntityType<?>> types);
	IAdvancementModifier createWaxOnWaxOffMod(Set<Block> unwaxed, Set<Block> waxed);
	IAdvancementModifier createFishyBusinessMod(Set<ItemLike> fishes);
	IAdvancementModifier createTacticalFishingMod(Set<BucketItem> buckets);
	IAdvancementModifier createASeedyPlaceMod(Set<Block> seeds);
	IAdvancementModifier createGlowAndBeholdMod(Set<Block> seeds);
}
