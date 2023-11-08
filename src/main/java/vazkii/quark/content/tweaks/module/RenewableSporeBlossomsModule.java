package vazkii.quark.content.tweaks.module;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.ZBonemeal;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.event.bus.ZResult;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

@ZetaLoadModule(category = "tweaks")
public class RenewableSporeBlossomsModule extends ZetaModule {
	
	@Config public double boneMealChance = 0.2;
	
	@Hint Item spore_blossom = Items.SPORE_BLOSSOM;
	
	@PlayEvent
	public void onBoneMealed(ZBonemeal event) {
		if(event.getBlock().is(Blocks.SPORE_BLOSSOM) && boneMealChance > 0) {
			if(Math.random() < boneMealChance)
				Block.popResource(event.getLevel(), event.getPos(), new ItemStack(Items.SPORE_BLOSSOM));
			
			event.setResult(ZResult.ALLOW);
		}
	}	

}
