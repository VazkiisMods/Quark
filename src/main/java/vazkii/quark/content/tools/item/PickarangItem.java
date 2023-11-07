package vazkii.quark.content.tools.item;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkItem;
import vazkii.zeta.module.ZetaModule;
import vazkii.quark.content.tools.config.PickarangType;
import vazkii.quark.content.tools.entity.rang.AbstractPickarang;
import vazkii.quark.content.tools.module.PickarangModule;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;

public class PickarangItem extends QuarkItem {

	public final PickarangType<?> type;

	public PickarangItem(String regname, ZetaModule module, Properties properties, PickarangType<?> type) {
		super(regname, module, properties);
		this.type = type;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
		stack.hurtAndBreak(2, attacker, (player) -> player.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		return true;
	}

	@Override
	public boolean isCorrectToolForDrops(@Nonnull BlockState blockIn) {
		return switch (type.harvestLevel) {
			case 0 -> Items.WOODEN_PICKAXE.isCorrectToolForDrops(blockIn) ||
				(type.canActAsAxe && Items.WOODEN_AXE.isCorrectToolForDrops(blockIn)) ||
				(type.canActAsShovel && Items.WOODEN_SHOVEL.isCorrectToolForDrops(blockIn)) ||
				(type.canActAsHoe && Items.WOODEN_HOE.isCorrectToolForDrops(blockIn));
			case 1 -> Items.STONE_PICKAXE.isCorrectToolForDrops(blockIn) ||
				(type.canActAsAxe && Items.STONE_AXE.isCorrectToolForDrops(blockIn)) ||
				(type.canActAsShovel && Items.STONE_SHOVEL.isCorrectToolForDrops(blockIn)) ||
				(type.canActAsHoe && Items.STONE_HOE.isCorrectToolForDrops(blockIn));
			case 2 -> Items.IRON_PICKAXE.isCorrectToolForDrops(blockIn) ||
				(type.canActAsAxe && Items.IRON_AXE.isCorrectToolForDrops(blockIn)) ||
				(type.canActAsShovel && Items.IRON_SHOVEL.isCorrectToolForDrops(blockIn)) ||
				(type.canActAsHoe && Items.IRON_HOE.isCorrectToolForDrops(blockIn));
			case 3 -> Items.DIAMOND_PICKAXE.isCorrectToolForDrops(blockIn) ||
				(type.canActAsAxe && Items.DIAMOND_AXE.isCorrectToolForDrops(blockIn)) ||
				(type.canActAsShovel && Items.DIAMOND_SHOVEL.isCorrectToolForDrops(blockIn)) ||
				(type.canActAsHoe && Items.DIAMOND_HOE.isCorrectToolForDrops(blockIn));
			default -> Items.NETHERITE_PICKAXE.isCorrectToolForDrops(blockIn) ||
				(type.canActAsAxe && Items.NETHERITE_AXE.isCorrectToolForDrops(blockIn)) ||
				(type.canActAsShovel && Items.NETHERITE_SHOVEL.isCorrectToolForDrops(blockIn)) ||
				(type.canActAsHoe && Items.NETHERITE_HOE.isCorrectToolForDrops(blockIn));
		};
	}

	//TODO: IForgeItem
	@Override
	public int getMaxDamage(ItemStack stack) {
		return Math.max(type.durability, 0);
	}

	@Override
	public boolean mineBlock(@Nonnull ItemStack stack, @Nonnull Level worldIn, BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity entityLiving) {
		if (state.getDestroySpeed(worldIn, pos) != 0)
			stack.hurtAndBreak(1, entityLiving, (player) -> player.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		return true;
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		playerIn.setItemInHand(handIn, ItemStack.EMPTY);
		int eff = Quark.ZETA.itemExtensions.get(itemstack).getEnchantmentLevelZeta(itemstack, Enchantments.BLOCK_EFFICIENCY);
		Vec3 pos = playerIn.position();
		worldIn.playSound(null, pos.x, pos.y, pos.z, QuarkSounds.ENTITY_PICKARANG_THROW, SoundSource.NEUTRAL, 0.5F + eff * 0.14F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

		if(!worldIn.isClientSide) {
			Inventory inventory = playerIn.getInventory();
			int slot = handIn == InteractionHand.OFF_HAND ? inventory.getContainerSize() - 1 : inventory.selected;
			AbstractPickarang<?> entity = type.makePickarang(worldIn, playerIn);
			entity.setThrowData(slot, itemstack);
			entity.shoot(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 1.5F + eff * 0.325F, 0F);
			entity.setOwner(playerIn);

			worldIn.addFreshEntity(entity);

			if(playerIn instanceof ServerPlayer sp)
				PickarangModule.throwPickarangTrigger.trigger(sp);
		}

		if(!playerIn.getAbilities().instabuild && type.cooldown > 0) {
			int cooldown = type.cooldown - eff;
			if (cooldown > 0)
				playerIn.getCooldowns().addCooldown(this, cooldown);
		}

		playerIn.awardStat(Stats.ITEM_USED.get(this));
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
	}

	@SuppressWarnings("deprecation") //Avoiding FOrge extension
	@Nonnull
	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nonnull EquipmentSlot slot) {
		Multimap<Attribute, AttributeModifier> multimap = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);

		if (slot == EquipmentSlot.MAINHAND) {
			multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", type.attackDamage, AttributeModifier.Operation.ADDITION));
			multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.8, AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		return 0F;
	}

	//TODO: IForgeItem
	@Override
	public boolean isRepairable(@Nonnull ItemStack stack) {
		return true;
	}

	@Override
	public boolean isValidRepairItem(@Nonnull ItemStack toRepair, ItemStack repair) {
		return type.repairMaterial != null && repair.getItem() == type.repairMaterial;
	}

	//TODO: IForgeItem
	@Override
	public int getEnchantmentValue(ItemStack stack) {
		return type.pickaxeEquivalent != null ? type.pickaxeEquivalent.getEnchantmentValue(stack) : 0;
	}

	@SuppressWarnings("deprecation") //Forge replacement
	@Override
	public int getEnchantmentValue() {
		return type.pickaxeEquivalent != null ? type.pickaxeEquivalent.getEnchantmentValue() : 0;
	}

	//TODO: IForgeItem
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return super.canApplyAtEnchantingTable(stack, enchantment) || ImmutableSet.of(Enchantments.BLOCK_FORTUNE, Enchantments.SILK_TOUCH, Enchantments.BLOCK_EFFICIENCY).contains(enchantment);
	}
}
