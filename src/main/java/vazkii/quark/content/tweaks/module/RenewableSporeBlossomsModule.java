package vazkii.quark.content.tweaks.module;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class RenewableSporeBlossomsModule extends QuarkModule {
	
	@Config public double boneMealChance = 0.2;
	
	@Hint Item spore_blossom = Items.SPORE_BLOSSOM;
	
	@SubscribeEvent
	public void onBoneMealed(BonemealEvent event) {
		if(event.getBlock().is(Blocks.SPORE_BLOSSOM) && boneMealChance > 0) {
			if(Math.random() < boneMealChance)
				Block.popResource(event.getLevel(), event.getPos(), new ItemStack(Items.SPORE_BLOSSOM));
			
			event.setResult(Result.ALLOW);
		}
	}	

}
