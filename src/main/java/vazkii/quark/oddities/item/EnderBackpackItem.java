package vazkii.quark.oddities.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.ProxiedItemStackHandler;
import vazkii.quark.base.module.Module;
import vazkii.quark.oddities.client.model.BackpackModel;
import vazkii.quark.oddities.container.BackpackContainer;
import vazkii.quark.oddities.module.BackpackModule;

import javax.annotation.Nonnull;
import java.util.Map;

public class EnderBackpackItem extends ArmorItem implements INamedContainerProvider {

	private static final String WORN_TEXTURE = Quark.MOD_ID + ":textures/misc/ender_backpack_worn.png";
	private static final String WORN_OVERLAY_TEXTURE = Quark.MOD_ID + ":textures/misc/ender_backpack_worn_overlay.png";

	private final Module module;

	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("rawtypes")
	private BipedModel model;

	public EnderBackpackItem(Module module) {
		super(ArmorMaterial.CHAIN, EquipmentSlotType.CHEST,
				new Properties()
				.maxStackSize(1)
				.maxDamage(0)
				.group(ItemGroup.TOOLS)
				.rarity(Rarity.EPIC));

		RegistryHelper.registerItem(this, "ender_backpack");
		this.module = module;
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if(worldIn.isRemote)
			return;

		Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
		boolean isCursed = enchants.containsKey(Enchantments.BINDING_CURSE);
		boolean changedEnchants = false;

		if(isCursed) {
			enchants.remove(Enchantments.BINDING_CURSE);
			changedEnchants = true;
		}

		if(changedEnchants)
			EnchantmentHelper.setEnchantments(enchants, stack);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		return HashMultimap.create();
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return type != null && type.equals("overlay") ? WORN_OVERLAY_TEXTURE : WORN_TEXTURE;
	}

	@Override
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
		if(model == null)
			model = new BackpackModel();

		return model;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isEnchantable(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	public boolean isEnabled() {
		return module != null && module.enabled;
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new BackpackContainer(id, player);
	}

	@Override
	public ITextComponent getDisplayName() {
		return getName();
	}

}
