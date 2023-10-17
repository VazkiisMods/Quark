package vazkii.quark.content.building.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.quark.content.building.block.HedgeBlock;
import vazkii.quark.content.world.block.BlossomSaplingBlock.BlossomTree;
import vazkii.quark.content.world.module.AncientWoodModule;
import vazkii.quark.content.world.module.BlossomTreesModule;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = ModuleCategory.BUILDING)
public class HedgesModule extends QuarkModule {

	public static TagKey<Block> hedgesTag;
	
	@Override
	public void register() {
		for(Wood wood : VanillaWoods.OVERWORLD)
			new HedgeBlock(this, wood.fence(), wood.leaf());
		
		new HedgeBlock(this, Blocks.OAK_FENCE, Blocks.AZALEA_LEAVES);
		new HedgeBlock(this, Blocks.OAK_FENCE, Blocks.FLOWERING_AZALEA_LEAVES);
	}

	@LoadEvent
	public void postRegister(ZRegister.Post e) {
		for (BlossomTree tree : BlossomTreesModule.trees.keySet())
			new HedgeBlock(this, BlossomTreesModule.woodSet.fence, tree.leaf.getBlock()).setCondition(tree.sapling::isEnabled);
		
		new HedgeBlock(this, AncientWoodModule.woodSet.fence, AncientWoodModule.ancient_leaves).setCondition(() -> ModuleLoader.INSTANCE.isModuleEnabled(AncientWoodModule.class));
	}
	
	@Override
	public void setup() {
		hedgesTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "hedges"));
	}
	
}
