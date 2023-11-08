package org.violetmoon.quark.content.building.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.util.VanillaWoods;
import org.violetmoon.quark.base.util.VanillaWoods.Wood;
import org.violetmoon.quark.content.building.block.HedgeBlock;
import org.violetmoon.quark.content.world.block.BlossomSaplingBlock.BlossomTree;
import org.violetmoon.quark.content.world.module.AncientWoodModule;
import org.violetmoon.quark.content.world.module.BlossomTreesModule;
import org.violetmoon.zeta.client.AlikeColorHandler;
import org.violetmoon.zeta.client.event.load.ZAddBlockColorHandlers;
import org.violetmoon.zeta.client.event.load.ZAddItemColorHandlers;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

@ZetaLoadModule(category = "building")
public class HedgesModule extends ZetaModule {

	public static TagKey<Block> hedgesTag;
	
	@LoadEvent
	public final void register(ZRegister event) {
		for(Wood wood : VanillaWoods.OVERWORLD)
			new HedgeBlock(this, wood.fence(), wood.leaf());
		
		new HedgeBlock(this, Blocks.OAK_FENCE, Blocks.AZALEA_LEAVES);
		new HedgeBlock(this, Blocks.OAK_FENCE, Blocks.FLOWERING_AZALEA_LEAVES);
	}

	@LoadEvent
	public void postRegister(ZRegister.Post e) {
		for (BlossomTree tree : BlossomTreesModule.trees.keySet())
			new HedgeBlock(this, BlossomTreesModule.woodSet.fence, tree.leaf.getBlock()).setCondition(tree.sapling::isEnabled);
		
		new HedgeBlock(this, AncientWoodModule.woodSet.fence, AncientWoodModule.ancient_leaves).setCondition(() -> Quark.ZETA.modules.isEnabled(AncientWoodModule.class));
	}
	
	@LoadEvent
	public final void setup(ZCommonSetup event) {
		hedgesTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "hedges"));
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends HedgesModule {

		@LoadEvent
		public void blockColorProviders(ZAddBlockColorHandlers event) {
			event.registerNamed(b -> new AlikeColorHandler((HedgeBlock) b, HedgeBlock::getLeaf), "hedge");
		}

		@LoadEvent
		public void itemColorProviders(ZAddItemColorHandlers event) {
			event.registerNamed(i -> new AlikeColorHandler(i, HedgeBlock::getLeaf), "hedge");
		}

	}

}
