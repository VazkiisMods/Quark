/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 01:13:02 (GMT)]
 */
package vazkii.quark.misc.feature;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.api.ICustomEnchantColor;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.misc.item.ItemRune;

public class ColorRunes extends Feature {

	public static final String TAG_RUNE_ATTACHED = "Quark:RuneAttached";
	public static final String TAG_RUNE_COLOR = "Quark:RuneColor";

	private static ItemStack targetStack;

	public static Item rune;

	int dungeonWeight, netherFortressWeight, jungleTempleWeight, desertTempleWeight, itemQuality, applyCost;
	boolean enableRainbowRuneCrafting, enableRainbowRuneChests, stackable;
	
	@Override
	public void setupConfig() {
		dungeonWeight = loadPropInt("Dungeon loot weight", "", 20);
		netherFortressWeight = loadPropInt("Nether Fortress loot weight", "", 15);
		jungleTempleWeight = loadPropInt("Jungle Temple loot weight", "", 15);
		desertTempleWeight = loadPropInt("Desert Temple loot weight", "", 15);
		itemQuality = loadPropInt("Item quality for loot", "", 0);
		applyCost = loadPropInt("Cost to apply rune", "", 15);
		enableRainbowRuneCrafting = loadPropBool("Enable Rainbow Rune Crafting", "", true);
		enableRainbowRuneChests = loadPropBool("Enable Rainbow Rune in Chests", "", false);
		stackable = loadPropBool("Stackable Runes", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {		
		rune = new ItemRune(stackable);
		
		if(enableRainbowRuneCrafting) {
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(rune, 7, 16), 
					"345", "2G6", "1W7",
					'G', ProxyRegistry.newStack(Blocks.GLASS),
					'W', ProxyRegistry.newStack(rune, 1, 0),
					'1', ProxyRegistry.newStack(rune, 1, 14),
					'2', ProxyRegistry.newStack(rune, 1, 1),
					'3', ProxyRegistry.newStack(rune, 1, 4),
					'4', ProxyRegistry.newStack(rune, 1, 5),
					'5', ProxyRegistry.newStack(rune, 1, 3),
					'6', ProxyRegistry.newStack(rune, 1, 11),
					'7', ProxyRegistry.newStack(rune, 1, 2));
		}
	}
	
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		LootFunction[] funcs = new LootFunction[] { new SetMetadata(new LootCondition[0], new RandomValueRange(0, enableRainbowRuneChests ? 16 : 15)) };

		if(event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON))
			event.getTable().getPool("main").addEntry(new LootEntryItem(rune, dungeonWeight, itemQuality, funcs, new LootCondition[0], "quark:rune"));
		else if(event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE))
			event.getTable().getPool("main").addEntry(new LootEntryItem(rune, netherFortressWeight, itemQuality, funcs, new LootCondition[0], "quark:rune"));
		else if(event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE))
			event.getTable().getPool("main").addEntry(new LootEntryItem(rune, jungleTempleWeight, itemQuality, funcs, new LootCondition[0], "quark:rune"));
		else if(event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID))
			event.getTable().getPool("main").addEntry(new LootEntryItem(rune, desertTempleWeight, itemQuality, funcs, new LootCondition[0], "quark:rune"));
	}

	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if(!left.isEmpty() && !right.isEmpty() && left.isItemEnchanted() && right.getItem() == rune) {
			ItemStack out = left.copy();
			ItemNBTHelper.setBoolean(out, TAG_RUNE_ATTACHED, true);
			ItemNBTHelper.setInt(out, TAG_RUNE_COLOR, right.getItemDamage());
			event.setOutput(out);
			event.setCost(applyCost);
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

	public static void setTargetStack(ItemStack stack) {
		targetStack = stack;
	}

	public static int getColor(int original) {
		if(!ModuleLoader.isFeatureEnabled(ColorRunes.class) || !doesStackHaveRune(targetStack) && !targetStack.isEmpty() && !(targetStack.getItem() instanceof ICustomEnchantColor))
			return original;

		return getColorFromStack(targetStack);
	}

	public static void applyColor() {
		if(!ModuleLoader.isFeatureEnabled(ColorRunes.class) || !doesStackHaveRune(targetStack)) {
			return;
		}

		int color = getColorFromStack(targetStack);
		float r = (color >> 16 & 0xFF) / 255F;
		float g = (color >> 8 & 0xFF) / 255F;
		float b = (color & 0xFF) / 255F;

		GlStateManager.color(r, g, b, 1F);
	}

	public static int getColorFromStack(ItemStack stack) {
		if(stack.isEmpty())
			return 0xFFFFFF;

		int retColor = 0xFFFFFF;
		boolean truncate = true;

		if (stack.getItem() instanceof ICustomEnchantColor) {
			int color = ((ICustomEnchantColor) stack.getItem()).getEnchantEffectColor(stack);
			truncate = ((ICustomEnchantColor) stack.getItem()).shouldTruncateColorBrightness(stack);
			retColor = 0xFF000000 | color;
		} else if (doesStackHaveRune(stack)) {
			int color = ItemRune.getColor(ItemNBTHelper.getInt(targetStack, TAG_RUNE_COLOR, 0));
			retColor = 0xFF000000 | color;
		}

		if(truncate) {
			int r = retColor >> 16 & 0xFF;
			int g = retColor >> 8 & 0xFF;
			int b = retColor & 0xFF;

			int t = r + g + b;
			if (t > 396) {
				float mul = 396F / t;
				r = (int) (r * mul);
				g = (int) (g * mul);
				b = (int) (b * mul);

				retColor = (0xFF << 24) + (r << 16) + (g << 8) + b;
			}
		}

		return retColor;
	}

	public static boolean doesStackHaveRune(ItemStack stack) {
		return !stack.isEmpty() && stack.hasTagCompound() && ItemNBTHelper.getBoolean(stack, TAG_RUNE_ATTACHED, false);
	}

}
