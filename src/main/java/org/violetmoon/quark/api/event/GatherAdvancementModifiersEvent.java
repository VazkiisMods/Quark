package org.violetmoon.quark.api.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.Event;
import org.violetmoon.quark.api.IAdvancementModifier;
import org.violetmoon.quark.api.IAdvancementModifierDelegate;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class GatherAdvancementModifiersEvent extends Event implements IAdvancementModifierDelegate{

	private final IAdvancementModifierDelegate delegate;
	private final Set<IAdvancementModifier> modifiers = new HashSet<>();
	
	public GatherAdvancementModifiersEvent(IAdvancementModifierDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Registers the given modifier.
	 * Use to add your own or to register the one that you make with "modifyX()" here below
	 */
	public void register(IAdvancementModifier modifier){
		modifiers.add(modifier);
	}

	public Set<IAdvancementModifier> modifiers() {
		return modifiers;
	}

	@Override
	public IAdvancementModifier createAdventuringTimeMod(Set<ResourceKey<Biome>> locations) {
		return delegate.createAdventuringTimeMod(locations);
	}

	@Override
	public IAdvancementModifier createBalancedDietMod(Set<ItemLike> items) {
		return delegate.createBalancedDietMod(items);
	}

	@Override
	public IAdvancementModifier createFuriousCocktailMod(BooleanSupplier isPotion, Set<MobEffect> effects) {
		return delegate.createFuriousCocktailMod(isPotion, effects);
	}

	@Override
	public IAdvancementModifier createMonsterHunterMod(Set<EntityType<?>> types) {
		return delegate.createMonsterHunterMod(types);
	}

	@Override
	public IAdvancementModifier createTwoByTwoMod(Set<EntityType<?>> types) {
		return delegate.createTwoByTwoMod(types);
	}

	@Override
	public IAdvancementModifier createWaxOnWaxOffMod(Set<Block> unwaxed, Set<Block> waxed) {
		return delegate.createWaxOnWaxOffMod(unwaxed,waxed);
	}

	@Override
	public IAdvancementModifier createFishyBusinessMod(Set<ItemLike> fishes) {
		return delegate.createFishyBusinessMod(fishes);
	}

	@Override
	public IAdvancementModifier createTacticalFishingMod(Set<BucketItem> buckets) {
		return delegate.createTacticalFishingMod(buckets);
	}

	@Override
	public IAdvancementModifier createASeedyPlaceMod(Set<Block> seeds) {
		return delegate.createASeedyPlaceMod(seeds);
	}

	@Override
	public IAdvancementModifier createGlowAndBeholdMod(Set<Block> blocks) {
		return delegate.createGlowAndBeholdMod(blocks);
	}
}
