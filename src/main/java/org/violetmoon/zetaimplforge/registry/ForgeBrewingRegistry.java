package org.violetmoon.zetaimplforge.registry;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.violetmoon.quark.mixin.accessor.AccessorPotionBrewing;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.registry.BrewingRegistry;
import org.violetmoon.zetaimplforge.ForgeZeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ForgeBrewingRegistry extends BrewingRegistry {
	public ForgeBrewingRegistry(ForgeZeta zeta) {
		super(zeta);
	}

	private record DelayedPotion(Potion input, Supplier<Ingredient> reagentSupplier, Potion output) {
		void register() {
			AccessorPotionBrewing.quark$getPotionMixes().add(new PotionBrewing.Mix<>(ForgeRegistries.POTIONS, input, reagentSupplier.get(), output));
		}
	}
	private List<DelayedPotion> delayedPotions = new ArrayList<>();
	private boolean okToRegisterImmediately = false;

	@Override
	public void addBrewingRecipe(Potion input, Supplier<Ingredient> reagentSupplier, Potion output) {
		DelayedPotion d = new DelayedPotion(input, reagentSupplier, output);

		if(okToRegisterImmediately)
			d.register();
		else
			delayedPotions.add(d);
	}

	@LoadEvent
	public void commonSetup(ZCommonSetup event) {
		event.enqueueWork(() -> {
			okToRegisterImmediately = true;
			delayedPotions.forEach(DelayedPotion::register);
			delayedPotions = null;
		});
	}
}
