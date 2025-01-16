package org.violetmoon.quark.mixin.mixins.self;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.quark.addons.oddities.item.BackpackItem;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;

import java.util.function.Consumer;

// Forge can't actually mixin to interfaces, so we fake it by just... mixing in to everyone inheriting the interface.
//Copy of the same class from Zeta for quark items
@Mixin({
	BackpackItem.class
})
public class IZetaItemMixin_FAKE implements IItemExtension, IZetaItemExtensions {

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		return onItemUseFirstZeta(stack, context);
	}

	@Override
	public boolean isRepairable(ItemStack stack) {
		return isRepairableZeta(stack);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		return onEntityItemUpdateZeta(stack, entity);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
		return doesSneakBypassUseZeta(stack, level, pos, player);
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
		return canEquipZeta(stack, armorType, entity);
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return isBookEnchantableZeta(stack, book);
	}

	/* TODO: Reimpl in Zeta
	@Override
	public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return getArmorTexture(stack, entity, slot, type);
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return getMaxDamage(stack);
	}
	 */

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return (itemAbility == ItemAbilities.SHEARS_CARVE);
	}

	@Override
	public int getEnchantmentValue(ItemStack stack) {
		return getEnchantmentValueZeta(stack);
	}

	@Override
	public ItemEnchantments getAllEnchantmentsZeta(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
		return getAllEnchantmentsZeta(stack, lookup);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return shouldCauseReequipAnimationZeta(oldStack, newStack, slotChanged);
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		return getBurnTimeZeta(itemStack, recipeType);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		return damageItemZeta(stack, amount, entity, onBroken);
	}

	@Override
	public boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity) {
		return isEnderMaskZeta(stack, player, endermanEntity);
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
		return canElytraFlyZeta(stack, entity);
	}
}
