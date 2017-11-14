package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockCandle;
import vazkii.quark.decoration.item.ItemTallow;

public class TallowAndCandles extends Feature {

	public static Item tallow;
	public static Block candle;
	
	public static boolean candlesFall;
	boolean enableTallow;
	int minDrop, maxDrop, candlesCrafted;
	
	@Override
	public void setupConfig() {
		candlesFall = loadPropBool("Candles Fall", "Set to false to disable candles falling like sand.", true);
		enableTallow = loadPropBool("Enable Tallow", "Turn this off if you don't want Tallow. This won't disable the candles, but will disable their recipes.", true);
		minDrop = loadPropInt("Min Tallow Dropped", "", 1);
		maxDrop = loadPropInt("Max Tallow Dropped", "", 3);
		candlesCrafted = loadPropInt("Candles Crafted", "", 2);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		candle = new BlockCandle();
		
		if(enableTallow) {
			tallow = new ItemTallow();
			
			OreDictionary.registerOre("tallow", tallow);
			
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(candle, candlesCrafted), 
					"S", "T", "T",
					'S', "string",
					'T', "tallow");
			
			ItemStack baseCandle = ProxyRegistry.newStack(candle);
			for(int i = 0; i < 16; i++) {
				String dye = LibMisc.OREDICT_DYES.get(15 - i);
				RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(candle, 1, i), baseCandle, dye);
				RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(candle, 8, i), 
						baseCandle, baseCandle, baseCandle, baseCandle, baseCandle, baseCandle, baseCandle, baseCandle, dye);
			}
		}
	}
	
	@SubscribeEvent
	public void onDrops(LivingDropsEvent event) {
		EntityLivingBase e = event.getEntityLiving();
		if(enableTallow && e instanceof EntityPig && maxDrop > 0) {
			int drops = minDrop + e.world.rand.nextInt(maxDrop - minDrop + 1);
			if(drops > 0)
				event.getDrops().add(new EntityItem(e.world, e.posX, e.posY, e.posZ, new ItemStack(tallow, drops)));
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
