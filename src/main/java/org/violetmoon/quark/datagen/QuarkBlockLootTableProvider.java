package org.violetmoon.quark.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.violetmoon.quark.content.automation.module.ChuteModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuarkBlockLootTableProvider extends BlockLootSubProvider {

    protected QuarkBlockLootTableProvider(HolderLookup.Provider holderLookupProvider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), holderLookupProvider);
    }

    @Override
    protected void generate() {
        dropSelf(ChuteModule.chute);
    }

    @Override
    protected Iterable<Block> getKnownBlocks(){
        List<Block> ret = new ArrayList<>();
        //Automation
        ret.add(ChuteModule.chute);

        //Building

        //Experimental
        //etc

        return ret;
    }
}
