package vazkii.quark.content.client.tooltip;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import vazkii.quark.base.Quark;
import vazkii.quark.base.QuarkClient;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.content.client.hax.PseudoAccessorItemStack;
import vazkii.quark.content.client.module.ImprovedTooltipsModule;
import vazkii.quark.content.client.resources.AttributeDisplayType;
import vazkii.quark.content.client.resources.AttributeIconEntry;
import vazkii.quark.content.client.resources.AttributeIconEntry.CompareType;
import vazkii.quark.content.client.resources.AttributeSlot;
import vazkii.zeta.client.event.ZGatherTooltipComponents;
import vazkii.zeta.util.ItemNBTHelper;

/**
 * @author WireSegal
 * Created at 10:34 AM on 9/1/19.
 */
public class AttributeTooltips {

	public static final ResourceLocation TEXTURE_UPGRADE = new ResourceLocation(Quark.MOD_ID, "textures/attribute/upgrade.png");
	public static final ResourceLocation TEXTURE_DOWNGRADE = new ResourceLocation(Quark.MOD_ID, "textures/attribute/downgrade.png");

	private static final Map<ResourceLocation, AttributeIconEntry> attributes = new HashMap<>();

	public static void receiveAttributes(Map<String, AttributeIconEntry> map) {
		attributes.clear();
		for (Map.Entry<String, AttributeIconEntry> entry : map.entrySet()) {
			attributes.put(new ResourceLocation(entry.getKey()), entry.getValue());
		}
	}

	@Nullable
	private static AttributeIconEntry getIconForAttribute(Attribute attribute) {
		ResourceLocation loc = Registry.ATTRIBUTE.getKey(attribute);
		if (loc != null) return attributes.get(loc);
		return null;
	}

	private static MutableComponent format(Attribute attribute, double value, AttributeDisplayType displayType) {
		switch (displayType) {
		case DIFFERENCE -> {
			return Component.literal((value > 0 ? "+" : "") + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value))
					.withStyle(value < 0 ? ChatFormatting.RED : ChatFormatting.WHITE);
		}
		case PERCENTAGE -> {
			return Component.literal((value > 0 ? "+" : "") + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value * 100) + "%")
					.withStyle(value < 0 ? ChatFormatting.RED : ChatFormatting.WHITE);
		}
		case MULTIPLIER -> {
			AttributeSupplier supplier = DefaultAttributes.getSupplier(EntityType.PLAYER);
			double scaledValue = value / supplier.getBaseValue(attribute);
			return Component.literal(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(scaledValue) + "x")
					.withStyle(scaledValue < 1 ? ChatFormatting.RED : ChatFormatting.WHITE);
		}
		default -> {
			return Component.literal(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value))
					.withStyle(value < 0 ? ChatFormatting.RED : ChatFormatting.WHITE);
		}
		}
	}

	public static void makeTooltip(ZGatherTooltipComponents event) {
		ItemStack stack = event.getItemStack();

		if(!Screen.hasShiftDown()) {
			List<Either<FormattedText, TooltipComponent>> tooltipRaw = event.getTooltipElements();
			Map<AttributeSlot, MutableComponent> attributeTooltips = Maps.newHashMap();

			boolean onlyInvalid = true;
			Multimap<Attribute, AttributeModifier> baseCheck = null;
			boolean allAreSame = true;

			for(AttributeSlot slot : AttributeSlot.values()) {
				if (canShowAttributes(stack, slot)) {
					Multimap<Attribute, AttributeModifier> slotAttributes = getModifiers(stack, slot);

					if (baseCheck == null)
						baseCheck = slotAttributes;
					else if (slot.hasCanonicalSlot() && allAreSame && !slotAttributes.equals(baseCheck))
						allAreSame = false;

					if (!slotAttributes.isEmpty() && !slot.hasCanonicalSlot())
						allAreSame = false;

					onlyInvalid = extractAttributeValues(stack, attributeTooltips, onlyInvalid, slot, slotAttributes);
				}
			}

			AttributeSlot primarySlot = getPrimarySlot(stack);

			int i = 1;
			for (AttributeSlot slot : AttributeSlot.values()) {
				if (attributeTooltips.containsKey(slot)) {
					int tooltipSlot = (slot == primarySlot ? 1 : i);
					tooltipRaw.add(tooltipSlot, Either.right(new AttributeComponent(stack, slot)));
					i++;

					if(allAreSame)
						break;
				}
			}
		}
	}

	private static Multimap<Attribute, AttributeModifier> getModifiersOnEquipped(Player player, ItemStack stack, Multimap<Attribute, AttributeModifier> attributes, AttributeSlot slot) {
		if (ImprovedTooltipsModule.showUpgradeStatus && slot.hasCanonicalSlot()) {
			ItemStack equipped = player.getItemBySlot(slot.getCanonicalSlot());
			if (!equipped.equals(stack) && !equipped.isEmpty()) {
				equipped.getTooltipLines(player, TooltipFlag.Default.NORMAL);
				return getModifiers(equipped, slot);

			}
		}
		return ImmutableMultimap.of();
	}

	private static Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack, AttributeSlot slot) {
		var capturedModifiers = ((PseudoAccessorItemStack) (Object) stack).quark$getCapturedAttributes();

		if (capturedModifiers.containsKey(slot)) {
			var map = capturedModifiers.get(slot);
			if (slot == AttributeSlot.MAINHAND) {
				if (!map.containsKey(Attributes.ATTACK_DAMAGE) && (map.containsKey(Attributes.ATTACK_SPEED) || EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED) > 0))
					map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Util.NIL_UUID, "-", 0, AttributeModifier.Operation.ADDITION));

				if (!map.containsKey(Attributes.ATTACK_SPEED) && map.containsKey(Attributes.ATTACK_DAMAGE))
					map.put(Attributes.ATTACK_SPEED, new AttributeModifier(Util.NIL_UUID, "-", 0, AttributeModifier.Operation.ADDITION));

				if (!map.containsKey(Attributes.ATTACK_KNOCKBACK) && Quark.ZETA.itemExtensions.get(stack).getEnchantmentLevelZeta(stack, Enchantments.KNOCKBACK) > 0)
					map.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(Util.NIL_UUID, "-", 0, AttributeModifier.Operation.ADDITION));
			}
			return map;
		}
		return ImmutableMultimap.of();
	}

	private static boolean extractAttributeValues(ItemStack stack, Map<AttributeSlot, MutableComponent> attributeTooltips, boolean onlyInvalid, AttributeSlot slot, Multimap<Attribute, AttributeModifier> slotAttributes) {
		boolean anyInvalid = false;
		for(Attribute attr : slotAttributes.keySet()) {
			AttributeIconEntry entry = getIconForAttribute(attr);
			if(entry != null) {
				onlyInvalid = false;
				Minecraft mc = Minecraft.getInstance();
				double attributeValue = getAttribute(mc.player, slot, stack, slotAttributes, attr);
				if (attributeValue != 0) {
					if (!attributeTooltips.containsKey(slot))
						attributeTooltips.put(slot, Component.literal(""));
					attributeTooltips.get(slot).append(format(attr, attributeValue, entry.displayTypes().get(slot)).getString()).append("/");
				}
			} else if (!anyInvalid) {
				anyInvalid = true;
				if (!attributeTooltips.containsKey(slot))
					attributeTooltips.put(slot, Component.literal(""));
				attributeTooltips.get(slot).append("[+]");
			}
		}
		return onlyInvalid;
	}

	private static int renderAttribute(PoseStack matrix, Attribute attribute, AttributeSlot slot, int x, int y, ItemStack stack, Multimap<Attribute, AttributeModifier> slotAttributes, Minecraft mc, boolean forceRenderIfZero, Multimap<Attribute, AttributeModifier> equippedSlotAttributes, @Nullable Set<Attribute> equippedAttrsToRender) {
		AttributeIconEntry entry = getIconForAttribute(attribute);
		if (entry != null) {
			if (equippedAttrsToRender != null)
				equippedAttrsToRender.remove(attribute);

			double value = getAttribute(mc.player, slot, stack, slotAttributes, attribute);
			if (value != 0 || forceRenderIfZero) {

				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShaderTexture(0, entry.texture());
				GuiComponent.blit(matrix, x, y, 0, 0, 9, 9, 9, 9);

				MutableComponent valueStr = format(attribute, value, entry.displayTypes().get(slot));

				if(ImprovedTooltipsModule.showUpgradeStatus && slot.hasCanonicalSlot()) {
					CompareType compareType = entry.comparison();
					EquipmentSlot equipSlot = slot.getCanonicalSlot();

					if (mc.player != null) {
						ItemStack equipped = mc.player.getItemBySlot(equipSlot);
						if (!equipped.equals(stack) && !equipped.isEmpty()) {
							if (!equippedSlotAttributes.isEmpty()) {
								double otherValue = getAttribute(mc.player, slot, equipped, equippedSlotAttributes, attribute);

								ChatFormatting color = compareType.getColor(value, otherValue);

								if (color != ChatFormatting.WHITE) {
									RenderSystem.setShaderTexture(0, color == ChatFormatting.RED ? TEXTURE_DOWNGRADE : TEXTURE_UPGRADE);
									int xp = x - 2;
									int yp = y - 2;
									if (ImprovedTooltipsModule.animateUpDownArrows && QuarkClient.ticker.total % 20 < 10)
										yp++;

									GuiComponent.blit(matrix, xp, yp, 0, 0, 13, 13, 13, 13);
								}


								valueStr = valueStr.withStyle(color);
							}
						}
					}
				}

				mc.font.draw(matrix, valueStr, x + 12, y + 1, -1);
				x += mc.font.width(valueStr) + 20;
			}
		}

		return x;
	}

	private static AttributeSlot getPrimarySlot(ItemStack stack) {
		if (stack.getItem() instanceof PotionItem || stack.getItem() instanceof TippedArrowItem)
			return AttributeSlot.POTION;
		return AttributeSlot.fromCanonicalSlot(Mob.getEquipmentSlotForItem(stack));
	}

	private static boolean canShowAttributes(ItemStack stack, AttributeSlot slot) {
		if (stack.isEmpty())
			return false;

		if (slot == AttributeSlot.POTION)
			return (ItemNBTHelper.getInt(stack, "HideFlags", 0) & 32) == 0;

		return (ItemNBTHelper.getInt(stack, "HideFlags", 0) & 2) == 0;
	}

	private static double getAttribute(Player player, AttributeSlot slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map, Attribute key) {
		if(player == null) // apparently this can happen
			return 0;

		Collection<AttributeModifier> collection = map.get(key);
		if(collection.isEmpty())
			return 0;

		double value = 0;

		AttributeIconEntry entry = getIconForAttribute(key);
		if (entry == null)
			return 0;

		AttributeDisplayType displayType = entry.displayTypes().get(slot);

		if (displayType != AttributeDisplayType.PERCENTAGE) {
			if (slot != null || !key.equals(Attributes.ATTACK_DAMAGE)) { // ATTACK_DAMAGE
				AttributeInstance attribute = player.getAttribute(key);
				if (attribute != null)
					value = attribute.getBaseValue();
			}
		}

		for (AttributeModifier modifier : collection) {
			if (modifier.getOperation() == AttributeModifier.Operation.ADDITION)
				value += modifier.getAmount();
		}

		double rawValue = value;

		for (AttributeModifier modifier : collection) {
			if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE)
				value += rawValue * modifier.getAmount();
		}

		for (AttributeModifier modifier : collection) {
			if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL)
				value += value * modifier.getAmount();
		}


		if (key.equals(Attributes.ATTACK_DAMAGE) && slot == AttributeSlot.MAINHAND)
			value += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
		if (key.equals(Attributes.ATTACK_KNOCKBACK) && slot == AttributeSlot.MAINHAND)
			value += Quark.ZETA.itemExtensions.get(stack).getEnchantmentLevelZeta(stack, Enchantments.KNOCKBACK);

		if (displayType == AttributeDisplayType.DIFFERENCE) {
			if (slot != null || !key.equals(Attributes.ATTACK_DAMAGE)) {
				AttributeInstance attribute = player.getAttribute(key);
				if (attribute != null)
					value -= attribute.getBaseValue();
			}
		}

		return value;
	}

	public record AttributeComponent(ItemStack stack,
									 AttributeSlot slot) implements ClientTooltipComponent, TooltipComponent {

		@Override
		public void renderImage(@Nonnull Font font, int tooltipX, int tooltipY, @Nonnull PoseStack pose, @Nonnull ItemRenderer itemRenderer, int something) {
			if (!Screen.hasShiftDown()) {
				pose.pushPose();
				pose.translate(0, 0, 500);

				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

				Minecraft mc = Minecraft.getInstance();
				pose.translate(0F, 0F, mc.getItemRenderer().blitOffset);

				int y = tooltipY - 1;

				AttributeSlot primarySlot = getPrimarySlot(stack);
				boolean showSlots = false;
				int x = tooltipX;

				if (canShowAttributes(stack, slot)) {
					Multimap<Attribute, AttributeModifier> slotAttributes = getModifiers(stack, slot);
					Multimap<Attribute, AttributeModifier> presentOnEquipped = getModifiersOnEquipped(mc.player, stack, slotAttributes, slot);
					Set<Attribute> equippedAttrsToRender = new LinkedHashSet<>(presentOnEquipped.keySet());

					for (Attribute attr : slotAttributes.keySet()) {
						if (getIconForAttribute(attr) != null) {
							if (slot != primarySlot) {
								showSlots = true;
								break;
							}
						}
					}

					boolean anyToRender = false;
					for (Attribute attr : slotAttributes.keySet()) {
						double value = getAttribute(mc.player, slot, stack, slotAttributes, attr);
						if (value != 0) {
							anyToRender = true;
							break;
						}
					}

					if (anyToRender) {
						if (showSlots) {
							RenderSystem.setShader(GameRenderer::getPositionTexShader);
							RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
							RenderSystem.setShaderTexture(0, MiscUtil.GENERAL_ICONS);
							GuiComponent.blit(pose, x, y, 193 + slot.ordinal() * 9, 35, 9, 9, 256, 256);
							x += 20;
						}

						for (Attribute key : slotAttributes.keySet())
							x = renderAttribute(pose, key, slot, x, y, stack, slotAttributes, mc, false, presentOnEquipped, equippedAttrsToRender);
						for (Attribute key : equippedAttrsToRender)
							x = renderAttribute(pose, key, slot, x, y, stack, slotAttributes, mc, true, presentOnEquipped, null);


						for (Attribute key : slotAttributes.keys()) {
							if (getIconForAttribute(key) == null) {
								mc.font.drawShadow(pose, "[+]", x + 1, y + 1, 0xFFFF55);
								break;
							}
						}
					}
				}

				pose.popPose();

			}
		}

		@Override
		public int getHeight() {
			return 10;
		}

		@Override
		public int getWidth(@Nonnull Font font) {
			int width = 0;

			if (canShowAttributes(stack, slot)) {
				Minecraft mc = Minecraft.getInstance();
				Multimap<Attribute, AttributeModifier> slotAttributes = getModifiers(stack, slot);
				Multimap<Attribute, AttributeModifier> presentOnEquipped = getModifiersOnEquipped(mc.player, stack, slotAttributes, slot);
				Set<Attribute> equippedAttrsToRender = new LinkedHashSet<>(presentOnEquipped.keySet());

				AttributeSlot primarySlot = getPrimarySlot(stack);
				boolean showSlots = false;

				for (Attribute attr : slotAttributes.keySet()) {
					if (getIconForAttribute(attr) != null) {
						if (slot != primarySlot) {
							showSlots = true;
							break;
						}
					}
				}

				boolean anyToRender = false;
				for (Attribute attr : slotAttributes.keySet()) {
					double value = getAttribute(mc.player, slot, stack, slotAttributes, attr);
					if (value != 0) {
						anyToRender = true;
						break;
					}
				}

				if (anyToRender) {
					if (showSlots)
						width += 20;

					for (Attribute key : slotAttributes.keySet()) {
						AttributeIconEntry icons = getIconForAttribute(key);
						if (icons != null) {
							equippedAttrsToRender.remove(key);

							double value = getAttribute(mc.player, slot, stack, slotAttributes, key);

							if (value != 0) {

								MutableComponent valueStr = format(key, value, icons.displayTypes().get(slot));
								width += font.width(valueStr) + 20;
							}
						}
					}

					for (Attribute key : equippedAttrsToRender) {
						AttributeIconEntry icons = getIconForAttribute(key);
						if (icons != null) {
							double value = getAttribute(mc.player, slot, stack, slotAttributes, key);
							MutableComponent valueStr = format(key, value, icons.displayTypes().get(slot));
							width += font.width(valueStr) + 20;
						}
					}


					for (Attribute key : slotAttributes.keys()) {
						if (getIconForAttribute(key) == null) {
							width += font.width("[+]") + 8;
							break;
						}
					}
				}
			}

			return width - 8;
		}

	}

}
