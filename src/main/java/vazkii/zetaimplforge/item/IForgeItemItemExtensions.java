package vazkii.zetaimplforge.item;

import java.util.Map;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;
import vazkii.zeta.item.ext.IZetaItemExtensions;

public class IForgeItemItemExtensions implements IZetaItemExtensions {
	public static final IForgeItemItemExtensions INSTANCE = new IForgeItemItemExtensions();

	@Override
	public int getBurnTimeZeta(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		return ForgeHooks.getBurnTime(stack, recipeType);
	}

	@Override
	public boolean canElytraFlyZeta(ItemStack stack, LivingEntity entity) {
		return stack.canElytraFly(entity);
	}

	@Override
	public boolean isEnderMaskZeta(ItemStack stack, Player player, EnderMan enderboy) {
		return stack.isEnderMask(player, enderboy);
	}

	@Override
	public boolean canShearZeta(ItemStack stack) {
		return stack.canPerformAction(ToolActions.SHEARS_CARVE);
	}

	@Override
	public int getEnchantmentLevelZeta(ItemStack stack, Enchantment enchantment) {
		return stack.getEnchantmentLevel(enchantment);
	}

	@Override
	public Map<Enchantment, Integer> getAllEnchantmentsZeta(ItemStack stack) {
		return stack.getAllEnchantments();
	}

	@Override
	public int getEnchantmentValueZeta(ItemStack stack) {
		return stack.getEnchantmentValue();
	}
}
