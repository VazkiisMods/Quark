package vazkii.quark.addons.oddities.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import vazkii.zeta.util.ItemNBTHelper;
import vazkii.quark.addons.oddities.inventory.BackpackMenu;
import vazkii.quark.addons.oddities.module.BackpackModule;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.base.client.handler.RequiredModTooltipHandler;
import vazkii.quark.base.handler.ProxiedItemStackHandler;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.registry.IZetaItemColorProvider;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public class BackpackItem extends DyeableArmorItem implements IQuarkItem, MenuProvider, IZetaItemColorProvider {

	private static final String WORN_TEXTURE = Quark.MOD_ID + ":textures/misc/backpack_worn.png";
	private static final String WORN_OVERLAY_TEXTURE = Quark.MOD_ID + ":textures/misc/backpack_worn_overlay.png";

	private final QuarkModule module;

	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("rawtypes")
	private HumanoidModel model;

	public BackpackItem(QuarkModule module) {
		super(ArmorMaterials.LEATHER, EquipmentSlot.CHEST,
				new Item.Properties()
				.stacksTo(1)
				.durability(0)
				.tab(CreativeModeTab.TAB_TOOLS)
				.rarity(Rarity.RARE));

		Quark.ZETA.registry.registerItem(this, "backpack");
		this.module = module;

		if(module.category.isAddon())
			RequiredModTooltipHandler.map(this, module.category.requiredMod);
	}

	@Override
	public int getDefaultTooltipHideFlags(@Nonnull ItemStack stack) {
		return stack.isEnchanted() ? ItemStack.TooltipPart.ENCHANTMENTS.getMask() : 0;
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	public static boolean doesBackpackHaveItems(ItemStack stack) {
		LazyOptional<IItemHandler> handlerOpt = stack.getCapability(ForgeCapabilities.ITEM_HANDLER, null);

		if (!handlerOpt.isPresent())
			return false;

		IItemHandler handler = handlerOpt.orElse(new ItemStackHandler());
		for(int i = 0; i < handler.getSlots(); i++)
			if(!handler.getStackInSlot(i).isEmpty())
				return true;

		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	@Override
	public int getEnchantmentValue(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean canBeDepleted() {
		return false;
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		return 0;
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, Level worldIn, @Nonnull Entity entityIn, int itemSlot, boolean isSelected) {
		if(worldIn.isClientSide)
			return;

		boolean hasItems = !BackpackModule.superOpMode && doesBackpackHaveItems(stack);

		Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
		boolean isCursed = enchants.containsKey(Enchantments.BINDING_CURSE);
		boolean changedEnchants = false;

		if(hasItems) {
			if(BackpackModule.isEntityWearingBackpack(entityIn, stack)) {
				if(!isCursed) {
					enchants.put(Enchantments.BINDING_CURSE, 1);
					changedEnchants = true;
				}

				if(BackpackModule.itemsInBackpackTick) {
					LazyOptional<IItemHandler> handlerOpt = stack.getCapability(ForgeCapabilities.ITEM_HANDLER, null);
					IItemHandler handler = handlerOpt.orElse(new ItemStackHandler());
					for(int i = 0; i < handler.getSlots(); i++) {
						ItemStack inStack = handler.getStackInSlot(i);
						if(!inStack.isEmpty())
							inStack.getItem().inventoryTick(inStack, worldIn, entityIn, i, false);
					}
				}
			} else {
				ItemStack copy = stack.copy();
				stack.setCount(0);
				entityIn.spawnAtLocation(copy, 0);
			}
		} else if(isCursed) {
			enchants.remove(Enchantments.BINDING_CURSE);
			changedEnchants = true;
		}

		if(changedEnchants)
			EnchantmentHelper.setEnchantments(enchants, stack);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entityItem) {
		if(BackpackModule.superOpMode || entityItem.level.isClientSide)
			return false;

		if (!ItemNBTHelper.detectNBT(stack))
			return false;

		LazyOptional<IItemHandler> handlerOpt = stack.getCapability(ForgeCapabilities.ITEM_HANDLER, null);

		if(!handlerOpt.isPresent())
			return false;

		IItemHandler handler = handlerOpt.orElse(new ItemStackHandler());

		for(int i = 0; i < handler.getSlots(); i++) {
			ItemStack stackAt = handler.getStackInSlot(i);
			if(!stackAt.isEmpty()) {
				ItemStack copy = stackAt.copy();
				Containers.dropItemStack(entityItem.level, entityItem.getX(), entityItem.getY(), entityItem.getZ(), copy);
			}
		}

		CompoundTag comp = ItemNBTHelper.getNBT(stack);
		comp.remove("Inventory");
		if (comp.size() == 0)
			stack.setTag(null);

		return false;
	}

	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag oldCapNbt) {
		ProxiedItemStackHandler handler = new ProxiedItemStackHandler(stack, 27);

		if (oldCapNbt != null && oldCapNbt.contains("Parent")) {
			CompoundTag itemData = oldCapNbt.getCompound("Parent");
			ItemStackHandler stacks = new ItemStackHandler();
			stacks.deserializeNBT(itemData);

			for (int i = 0; i < stacks.getSlots(); i++)
				handler.setStackInSlot(i, stacks.getStackInSlot(i));

			oldCapNbt.remove("Parent");
		}

		return handler;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		return ImmutableMultimap.of();
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return type != null && type.equals("overlay") ? WORN_OVERLAY_TEXTURE : WORN_TEXTURE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {

			@Override
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
				return ModelHandler.armorModel(ModelHandler.backpack, armorSlot);
			}

		});
	}

	@Override
	public boolean isFoil(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public boolean isEnchantable(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public ItemColor getItemColor() {
		return (stack, i) -> i > 0 ? -1 : ((DyeableArmorItem) stack.getItem()).getColor(stack);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inv, @Nonnull Player player) {
		return new BackpackMenu(id, player);
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return Component.translatable(getDescriptionId());
	}


}
