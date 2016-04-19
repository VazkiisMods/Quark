/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [19/03/2016, 17:24:39 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.handler.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.item.ItemGlassShard;

public class GlassShards extends Feature {

	public static Item glass_shard;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		glass_shard = new ItemGlassShard();

		RecipeHandler.addOreDictRecipe(new ItemStack(Blocks.glass),
				"SS", "SS",
				'S', new ItemStack(glass_shard, 1, 0));

		for(int i = 0; i < 16; i++)
			RecipeHandler.addOreDictRecipe(new ItemStack(Blocks.stained_glass, 1, i),
					"SS", "SS",
					'S', new ItemStack(glass_shard, 1, i + 1));
	}

	@SubscribeEvent
	public void onDrops(HarvestDropsEvent event) {
		Block block = event.getState().getBlock();
		if(event.getDrops().isEmpty() && block == Blocks.glass || block == Blocks.stained_glass) {
			int meta = 0;
			if(block == Blocks.stained_glass)
				meta = block.getMetaFromState(event.getState()) + 1;

			int quantity = MathHelper.clamp_int(2 + event.getWorld().rand.nextInt(3) + event.getWorld().rand.nextInt(event.getFortuneLevel() + 1), 1, 4);

			event.getDrops().add(new ItemStack(glass_shard, quantity, meta));
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
