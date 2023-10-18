package vazkii.quark.content.experimental.module;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

import java.util.List;

@LoadModule(category = "experimental", enabledByDefault = false,
description = "This feature generates Resource Pack Item Model predicates on the items defined in 'Items to Change'\n"
		+ "for the Enchantments defined in 'Enchantments to Register'.\n\n"
		+ "Example: if 'minecraft:silk_touch' is added to 'Enchantments to Register', and 'minecraft:netherite_pickaxe'\n"
		+ "is added to 'Items to Change', then a predicate named 'quark_has_enchant_minecraft_silk_touch' will be available\n"
		+ "to the netherite_pickaxe.json item model, whose value will be the enchantment level.")
public class EnchantmentPredicatesModule extends QuarkModule {

	@Config
	public static List<String> itemsToChange = Lists.newArrayList();

	@Config
	public static List<String> enchantmentsToRegister = Lists.newArrayList();

	@LoadEvent
	@OnlyIn(Dist.CLIENT)
	public void clientSetup(ZClientSetup e) {
		if(enabled) {
			e.enqueueWork(() -> {
				List<Item> items = MiscUtil.massRegistryGet(itemsToChange, ForgeRegistries.ITEMS);
				List<Enchantment> enchants = MiscUtil.massRegistryGet(enchantmentsToRegister, ForgeRegistries.ENCHANTMENTS);

				for(Enchantment enchant : enchants) {
					ResourceLocation enchantRes = ForgeRegistries.ENCHANTMENTS.getKey(enchant);
					ResourceLocation name = new ResourceLocation(Quark.MOD_ID + "_has_enchant_" + enchantRes.getNamespace() + "_" + enchantRes.getPath());
					ItemPropertyFunction fun = (stack, level, entity, i) -> EnchantmentHelper.getTagEnchantmentLevel(enchant, stack);

					for(Item item : items)
						ItemProperties.register(item, name, fun);
				}
			});
		}
	}


}
