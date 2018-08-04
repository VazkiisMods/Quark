/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/03/2016, 17:24:39 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.item.ItemGlassShard;

public class GlassShards extends Feature {

	public static Item glass_shard;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		glass_shard = new ItemGlassShard();

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.GLASS),
				"SS", "SS",
				'S', ProxyRegistry.newStack(glass_shard, 1, 0));

		for(int i = 0; i < 16; i++)
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.STAINED_GLASS, 1, i),
					"SS", "SS",
					'S', ProxyRegistry.newStack(glass_shard, 1, i + 1));
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		OreDictionary.registerOre("shardGlass", glass_shard);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onDrops(HarvestDropsEvent event) {
		Block block = event.getState().getBlock();
		if(event.getDrops() != null && event.getDrops().isEmpty() && block != null && (block == Blocks.GLASS || block == Blocks.STAINED_GLASS) && !event.isSilkTouching()) {
			int meta = 0;
			if(block == Blocks.STAINED_GLASS)
				meta = block.getMetaFromState(event.getState()) + 1;

			int quantity = MathHelper.clamp(2 + event.getWorld().rand.nextInt(3) + event.getWorld().rand.nextInt(event.getFortuneLevel() + 1), 1, 4);

			event.getDrops().add(ProxyRegistry.newStack(glass_shard, quantity, meta));
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
