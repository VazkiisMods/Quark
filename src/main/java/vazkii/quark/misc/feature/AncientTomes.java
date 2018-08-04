/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [01/06/2016, 19:41:33 (GMT)]
 */
package vazkii.quark.misc.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.item.ItemAncientTome;

public class AncientTomes extends Feature {

	public static Item ancient_tome;
	public static List<Enchantment> validEnchants = new ArrayList();
	private String[] enchantNames;

	int dungeonWeight, libraryWeight, itemQuality, mergeTomeCost, applyTomeCost;

	@Override
	public void setupConfig() {
		enchantNames = loadPropStringList("Valid Enchantments", "", generateDefaultEnchantmentList());
		dungeonWeight = loadPropInt("Dungeon loot weight", "", 8);
		libraryWeight = loadPropInt("Stronghold Library loot weight", "", 12);
		itemQuality = loadPropInt("Item quality for loot", "", 2);
		mergeTomeCost = loadPropInt("Cost to apply tome", "", 35);
		applyTomeCost = loadPropInt("Cost to apply upgraded book to item", "", 35);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		ancient_tome = new ItemAncientTome();
		
		LootFunctionManager.registerFunction(new EnchantTomeFunction.Serializer());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		validEnchants.clear();
		for(String s : enchantNames) {
			ResourceLocation r = new ResourceLocation(s);
			Enchantment e = Enchantment.REGISTRY.getObject(r);
			if(e != null)
				validEnchants.add(e);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		if(event.getName().equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY))
			event.getTable().getPool("main").addEntry(new LootEntryItem(ancient_tome, libraryWeight, itemQuality, new LootFunction[] { new EnchantTomeFunction() }, new LootCondition[0], "quark:ancient_tome"));
		else if(event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON))
			event.getTable().getPool("main").addEntry(new LootEntryItem(ancient_tome, dungeonWeight, itemQuality, new LootFunction[] { new EnchantTomeFunction() }, new LootCondition[0], "quark:ancient_tome"));
	}

	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if(!left.isEmpty() && !right.isEmpty()) {
			if(left.getItem() == Items.ENCHANTED_BOOK && right.getItem() == ancient_tome)
				handleTome(left, right, event);
			else if(right.getItem() == Items.ENCHANTED_BOOK && left.getItem() == ancient_tome)
				handleTome(right, left, event);

			else if(right.getItem() == Items.ENCHANTED_BOOK) {
				Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(right);
				Map<Enchantment, Integer> currentEnchants = EnchantmentHelper.getEnchantments(left);
				boolean hasOverLevel = false;
				boolean hasMatching = false;
				for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
					Enchantment ench = entry.getKey();
					if(ench == null)
						continue;
					
					int level = entry.getValue();
					if (level > ench.getMaxLevel()) {
						hasOverLevel = true;
						if (ench.canApply(left)) {
							hasMatching = true;
							//remove incompatible enchantments
							for (Iterator<Enchantment> iterator = currentEnchants.keySet().iterator(); iterator.hasNext(); ) {
								Enchantment enchCompare = iterator.next();
								if (enchCompare == ench)
									continue;

								if (!enchCompare.isCompatibleWith(ench)) {
									iterator.remove();
								}
							}
							currentEnchants.put(ench, level);
						}
					} else if (ench.canApply(left)) {
						boolean compatible = true;
						//don't apply incompatible enchantments
						for (Enchantment enchCompare : currentEnchants.keySet()) {
							if (enchCompare == ench)
								continue;

							if (enchCompare != null && !enchCompare.isCompatibleWith(ench)) {
								compatible = false;
								break;
							}
						}
						if (compatible) {
							currentEnchants.put(ench, level);
						}
					}
				}
				if (hasOverLevel) {
					if (hasMatching) {
						ItemStack out = left.copy();
						EnchantmentHelper.setEnchantments(currentEnchants, out);
						String name = event.getName();
						int cost = applyTomeCost;
						if(name != null && !name.isEmpty()){
							out.setStackDisplayName(name);
							cost++;
						}
						event.setOutput(out);
						event.setCost(cost);
					} else {
						event.setCanceled(true);
					}
				}
			}
		}
	}

	private void handleTome(ItemStack book, ItemStack tome, AnvilUpdateEvent event) {
		Map<Enchantment, Integer> enchantsBook = EnchantmentHelper.getEnchantments(book);
		Map<Enchantment, Integer> enchantsTome = EnchantmentHelper.getEnchantments(tome);
		for (Map.Entry<Enchantment, Integer> entry : enchantsTome.entrySet()) {
			if(enchantsBook.getOrDefault(entry.getKey(), 0).equals(entry.getValue())){
				enchantsBook.put(entry.getKey(), entry.getValue() + 1);
			} else {
				return;
			}
		}
		ItemStack output = ProxyRegistry.newStack(Items.ENCHANTED_BOOK);
		for (Map.Entry<Enchantment, Integer> entry : enchantsBook.entrySet()) {
			ItemEnchantedBook.addEnchantment(output, new EnchantmentData(entry.getKey(), entry.getValue()));
		}
		event.setOutput(output);
		event.setCost(mergeTomeCost);
	}

	private String[] generateDefaultEnchantmentList() {
		Enchantment[] enchants = new Enchantment[] {
				Enchantments.FEATHER_FALLING,
				Enchantments.THORNS,
				Enchantments.SHARPNESS,
				Enchantments.SMITE,
				Enchantments.BANE_OF_ARTHROPODS,
				Enchantments.KNOCKBACK,
				Enchantments.FIRE_ASPECT,
				Enchantments.LOOTING,
				Enchantments.SWEEPING,
				Enchantments.EFFICIENCY,
				Enchantments.UNBREAKING,
				Enchantments.FORTUNE,
				Enchantments.POWER,
				Enchantments.PUNCH,
				Enchantments.LUCK_OF_THE_SEA,
				Enchantments.LURE
		};

		List<String> strings = new ArrayList();
		for(Enchantment e : enchants)
			if(e != null && e.getRegistryName() != null)
				strings.add(e.getRegistryName().toString());

		return strings.toArray(new String[strings.size()]);
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	public static class EnchantTomeFunction extends LootFunction {

		protected EnchantTomeFunction() {
			super(new LootCondition[0]);
		}

		@Override
		public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
			Enchantment enchantment = validEnchants.get(rand.nextInt(validEnchants.size()));
			stack.addEnchantment(enchantment, enchantment.getMaxLevel());
			return stack;
		}
		
		public static class Serializer extends LootFunction.Serializer<EnchantTomeFunction> {

			protected Serializer() {
				super(new ResourceLocation(LibMisc.MOD_ID, "enchant_tome"), EnchantTomeFunction.class);
			}

			@Override
			public void serialize(JsonObject object, EnchantTomeFunction functionClazz,
					JsonSerializationContext serializationContext) {}

			@Override
			public EnchantTomeFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
					LootCondition[] conditionsIn) {
				return new EnchantTomeFunction();
			}	
		}
	}

}
