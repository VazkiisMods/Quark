package org.violetmoon.quark.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;

public class QuarkEntityLootTableProvider extends EntityLootSubProvider {

    protected QuarkEntityLootTableProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), FeatureFlags.REGISTRY.allFlags(), registries);
        //I'm not sure why this constructor takes two FeatureFlagSets - Partonetrain
    }


    @Override
    public void generate() {

    }
}
