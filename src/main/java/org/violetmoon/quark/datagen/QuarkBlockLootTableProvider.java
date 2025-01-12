package org.violetmoon.quark.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.violetmoon.quark.content.automation.module.ChuteModule;
import org.violetmoon.quark.content.building.module.*;
import org.violetmoon.quark.content.tools.module.BottledCloudModule;
import org.violetmoon.quark.content.tweaks.module.GlassShardModule;
import org.violetmoon.quark.content.world.module.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuarkBlockLootTableProvider extends BlockLootSubProvider {

    protected QuarkBlockLootTableProvider(HolderLookup.Provider holderLookupProvider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), holderLookupProvider);
    }

    @Override
    protected void generate() {
        //Automation
        dropSelf(ChuteModule.chute);
        //Building
        dropSelf(CelebratoryLampsModule.stone_lamp);
        dropSelf(CelebratoryLampsModule.stone_brick_lamp);
        for(Block block : CompressedBlocksModule.blocks)
            dropSelf(block);
        for(Block block : FramedGlassModule.glassBlocks)
            dropSelf(block);
        dropSelf(GoldBarsModule.gold_bars);
        for(Block block : HedgesModule.hedges)
            dropSelf(block);
        for(Block block : HollowLogsModule.hollowLogs)
            dropSelf(block);
        dropSelf(GrateModule.grate);
        for(Block block : JapanesePaletteModule.blocks)
            dropSelf(block);
        for(Block block : LeafCarpetModule.carpets)
            dropSelf(block);
        for(Block block : MidoriModule.blocks)
            dropSelf(block);
        for(Block block : MoreMudBlocksModule.blocks)
            dropSelf(block);
        for(Block block : MorePottedPlantsModule.pottedPlants)
            createPotFlowerItemTable(MorePottedPlantsModule.getItemLikeFromBlock(block)); //untested
        dropSelf(NetherBrickFenceGateModule.netherBrickFenceGate);
        for(Block block : RainbowLampsModule.lamps)
            dropSelf(block);
        for(Block block : RawMetalBricksModule.blocks)
            dropSelf(block);
        dropSelf(RopeModule.rope);
        dropNothing(ShearVinesModule.cut_vine);
        for(Block block : ShinglesModule.blocks)
            dropSelf(block);
        for(Block block : StoolsModule.stools)
            dropSelf(block);
        dropSelf(SturdyStoneModule.sturdy_stone);
        dropSelf(ThatchModule.thatch);
        for(Block block : VariantBookshelvesModule.variantBookshelves)
            createBookshelfDrops(block);
        for(Block block : VariantChestsModule.regularChests)
            dropSelf(block);
        for(Block block : VariantChestsModule.trappedChests)
            dropSelf(block);
        dropSelf(VariantFurnacesModule.deepslateFurnace);
        dropSelf(VariantFurnacesModule.blackstoneFurnace);
        for(Block block : VariantLaddersModule.variantLadders)
            dropSelf(block);
        for(Block block : VerticalPlanksModule.blocks)
            dropSelf(block);
        for(Block block : VerticalSlabsModule.blocks)
            createSlabItemTable(block); //untested, this is for vanilla double slabs
        for(Block block : WoodenPostsModule.blocks)
            dropSelf(block);
        //Tools
        dropNothing(BottledCloudModule.cloud);
        //Tweaks
        //dropShards(GlassShardModule.dirtyGlass); //TODO: implement this
        dropWhenSilkTouch(GlassShardModule.dirtyGlassPane);
        //World
        //createLeafShearsDrops(AncientWoodModule.ancient_leaves); //TODO: implement c:tools/shears check
        //Azalea leaves are vanilla
        //createShearsDrops(ChorusVegetationModule.chorus_weeds); //TODO: implement c:tools/shears check
        //createShearsDrops(ChorusVegetationModule.chorus_twist);
        for(Block block : CorundumModule.clusters)
            dropSelf(block);
        for(Block block : CorundumModule.waxedCrystals)
            dropSelf(block);
        for(Block block : CorundumModule.clusters)
            dropSelf(block);
        for(Block block : CorundumModule.panes)
            dropSelf(block);
        dropSelf(GlimmeringWealdModule.glow_shroom);
        dropSelf(GlimmeringWealdModule.glow_lichen_growth);
        //TODO GlimmeringWealdModule.glow_shroom_block. mushroom cap fullblock drops are weird
        dropWhenSilkTouch(GlimmeringWealdModule.glow_shroom_stem);
        dropSelf(GlimmeringWealdModule.glow_shroom_ring);
        dropNothing(MonsterBoxModule.monster_box);
        dropSelf(NewStoneTypesModule.limestoneBlock);
        dropSelf(NewStoneTypesModule.jasperBlock);
        dropSelf(NewStoneTypesModule.shaleBlock);
        dropSelf(NewStoneTypesModule.myaliteBlock);
        for(Block block : NewStoneTypesModule.polishedBlocks.values())
            dropSelf(block);
        dropSelf(SpiralSpiresModule.myalite_crystal);
        dropSelf(SpiralSpiresModule.dusky_myalite);
        for(Block block : BlossomTreesModule.woodSet.allBlocks())
            dropSelf(block);
        for(Block block : BlossomTreesModule.blossomTrees.stream().map(blossomTree -> blossomTree.leaves).toList()){
            //createLeafShearsDrops(block);
        }


    }

    @Override
    protected Iterable<Block> getKnownBlocks(){
        List<Block> ret = new ArrayList<>();
        //Automation
        ret.add(ChuteModule.chute);
        //Building
        ret.add(CelebratoryLampsModule.stone_lamp);
        ret.add(CelebratoryLampsModule.stone_brick_lamp);
        ret.addAll(CompressedBlocksModule.blocks);
        //ret.addAll(DuskboundBlocksModule.blocks); //need a way to access VariantRegistry slabs, stairs, and walls lists
        ret.addAll(FramedGlassModule.glassBlocks);
        ret.add(GoldBarsModule.gold_bars);
        ret.addAll(HedgesModule.hedges);
        ret.addAll(HollowLogsModule.hollowLogs);
        //ret.addAll(IndustrialPaletteModule.blocks); //need slabs stairs walls etc
        ret.add(GrateModule.grate);
        ret.addAll(JapanesePaletteModule.blocks);
        ret.addAll(LeafCarpetModule.carpets);
        ret.addAll(MidoriModule.blocks);
        //ret.addAll(MoreBrickTypesModule.);
        ret.addAll(MoreMudBlocksModule.blocks);
        ret.addAll(MorePottedPlantsModule.pottedPlants);
        //ret.addAll(MoreStoneVariantsModule.blocks); //stairs slab walls
        ret.add(NetherBrickFenceGateModule.netherBrickFenceGate);
        ret.addAll(RainbowLampsModule.lamps);
        ret.addAll(RawMetalBricksModule.blocks);
        ret.add(RopeModule.rope);
        ret.add(ShearVinesModule.cut_vine);
        ret.addAll(ShinglesModule.blocks);
        //ret.addAll(SoulSandstoneModule.blocks); //stairs slab walls
        ret.addAll(StoolsModule.stools);
        ret.add(SturdyStoneModule.sturdy_stone);
        ret.add(ThatchModule.thatch);
        ret.addAll(VariantBookshelvesModule.variantBookshelves);
        ret.addAll(VariantChestsModule.regularChests);
        ret.addAll(VariantChestsModule.trappedChests);
        ret.add(VariantFurnacesModule.deepslateFurnace);
        ret.add(VariantFurnacesModule.blackstoneFurnace);
        ret.addAll(VariantLaddersModule.variantLadders);
        ret.addAll(VerticalPlanksModule.blocks);
        ret.addAll(VerticalSlabsModule.blocks);
        ret.addAll(WoodenPostsModule.blocks);
        //Tools
        ret.add(BottledCloudModule.cloud);
        //Tweaks
        ret.add(GlassShardModule.dirtyGlass);
        ret.add(GlassShardModule.dirtyGlassPane);
        //World
        ret.addAll(AncientWoodModule.woodSet.allBlocks());
        ret.add(AncientWoodModule.ancient_leaves);
        ret.addAll(AzaleaWoodModule.woodSet.allBlocks());
        ret.add(ChorusVegetationModule.chorus_weeds);
        ret.add(ChorusVegetationModule.chorus_twist);
        ret.addAll(CorundumModule.crystals);
        ret.addAll(CorundumModule.waxedCrystals);
        ret.addAll(CorundumModule.clusters);
        ret.addAll(CorundumModule.panes);
        ret.add(GlimmeringWealdModule.glow_shroom);
        ret.add(GlimmeringWealdModule.glow_lichen_growth);
        ret.add(GlimmeringWealdModule.glow_shroom_block);
        ret.add(GlimmeringWealdModule.glow_shroom_stem);
        ret.add(GlimmeringWealdModule.glow_shroom_ring);
        ret.add(MonsterBoxModule.monster_box);
        ret.add(NewStoneTypesModule.limestoneBlock); //MoreStoneVariants should handle the other variants
        ret.add(NewStoneTypesModule.jasperBlock);
        ret.add(NewStoneTypesModule.shaleBlock);
        ret.add(NewStoneTypesModule.myaliteBlock);
        ret.addAll(NewStoneTypesModule.polishedBlocks.values());
        //ret.add(PermafrostModule.permafrost); //stairs slab walls
        ret.add(SpiralSpiresModule.myalite_crystal);
        ret.add(SpiralSpiresModule.dusky_myalite);
        ret.addAll(BlossomTreesModule.woodSet.allBlocks());
        ret.addAll(BlossomTreesModule.blossomTrees.stream().map(blossomTree -> blossomTree.leaves).toList());
        //Oddities
        //Experimental

        return ret;
    }

    protected LootTable.Builder createBookshelfDrops(Block block) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .when(this.hasSilkTouch())
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        LootItem.lootTableItem(block)
                                )
                )
                .withPool(
                        LootPool.lootPool()
                                .when(this.doesNotHaveSilkTouch())
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        LootItem.lootTableItem(Items.BOOK)
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F)))
                                )
                );
    }

    protected LootTable.Builder dropNothing(Block block) {
        return LootTable.lootTable();
    }
}
