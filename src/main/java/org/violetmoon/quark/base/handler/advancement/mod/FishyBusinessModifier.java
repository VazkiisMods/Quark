package org.violetmoon.quark.base.handler.advancement.mod;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.violetmoon.quark.api.IMutableAdvancement;
import org.violetmoon.quark.base.handler.advancement.AdvancementModifier;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.Set;

public class FishyBusinessModifier extends AdvancementModifier {

    private static final ResourceLocation TARGET = new ResourceLocation("husbandry/fishy_business");

    final Set<ItemLike> fishes;

    public FishyBusinessModifier(ZetaModule module, Set<ItemLike> fishes) {
        super(module);
        this.fishes = fishes;
        Preconditions.checkArgument(!fishes.isEmpty(), "Advancement modifier list cant be empty");
    }

    @Override
    public Set<ResourceLocation> getTargets() {
        return ImmutableSet.of(TARGET);
    }

    @Override
    public boolean apply(ResourceLocation res, IMutableAdvancement adv) {

        ItemLike[] array = fishes.toArray(ItemLike[]::new);
        Criterion criterion = new Criterion(FishingRodHookedTrigger.
                TriggerInstance.fishedItem(
                        ItemPredicate.ANY,
                        EntityPredicate.ANY,
                        ItemPredicate.Builder.item().of(array).build()));

        String name = Registry.ITEM.getKey(array[0].asItem()).toString();
        adv.addOrCriterion(name, criterion);

        return true;
    }

}
