package vazkii.zeta.item.ext;

import java.util.Map;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

//TODO ZETA: more extensions (remove all usages of IForgeItem, implement this interface on Quark's items)
@SuppressWarnings("deprecation") //forge ext
public interface IZetaItemExtensions {

	int getBurnTimeZeta(ItemStack stack, @Nullable RecipeType<?> recipeType);

	default boolean canElytraFlyZeta(ItemStack stack, LivingEntity entity) {
		//forge has a funky little extension for this
		return stack.getItem() instanceof ElytraItem && ElytraItem.isFlyEnabled(stack);
	}

	default boolean isEnderMaskZeta(ItemStack stack, Player player, EnderMan enderboy) {
		return stack.getItem() == Items.CARVED_PUMPKIN;
	}

	default boolean canShearZeta(ItemStack stack) {
		return stack.getItem() instanceof ShearsItem;
	}

	default int getEnchantmentLevelZeta(ItemStack stack, Enchantment enchantment) {
		return EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
	}

	default Map<Enchantment, Integer> getAllEnchantmentsZeta(ItemStack stack) {
		return EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags());
	}

	default int getEnchantmentValueZeta(ItemStack stack) {
		return stack.getItem().getEnchantmentValue();
	}

}
