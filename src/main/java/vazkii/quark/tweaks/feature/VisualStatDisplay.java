package vazkii.quark.tweaks.feature;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;
import vazkii.quark.management.client.gui.GuiButtonChest;

public class VisualStatDisplay extends Feature {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void makeTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();

		if(!GuiScreen.isShiftKeyDown() && isAttributeStrippable(stack)) {
			List<String> tooltip = event.getToolTip();
			boolean clearedAny = false;

			for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
				Multimap<String, AttributeModifier> slotAttributes = stack.getAttributeModifiers(slot);
				if(!slotAttributes.isEmpty()) {
					String slotDesc = I18n.translateToLocal("item.modifiers." + slot.getName());
					int index = tooltip.indexOf(slotDesc) - 1;
					if(index < 0)
						continue;
					
					tooltip.remove(index); // Remove twice to clear the empty space
					tooltip.remove(index);
				}

				for(String s : slotAttributes.keys()) {
					String name = I18n.translateToLocal("attribute.name." + s);
					for(int i = 1; i < tooltip.size(); i++)
						if(tooltip.get(i).contains(name)) {
							tooltip.remove(i);
							clearedAny = true;
							break;
						}
				}
			}

			if(clearedAny)
				tooltip.add(1, "");
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderTooltip(RenderTooltipEvent.PostText event) {
		ItemStack stack = event.getStack();
		if(!GuiScreen.isShiftKeyDown() && stack != null && isAttributeStrippable(stack)) {
			Item item = stack.getItem();
			GlStateManager.pushMatrix();
			GlStateManager.color(1F, 1F, 1F);
			Minecraft mc = Minecraft.getMinecraft();

			int x = event.getX() + 0;
			int y = event.getY() + 10;
			
			for(int i = 1; i < event.getLines().size(); i++) {
				String s = event.getLines().get(i);
				if(TextFormatting.getTextWithoutFormattingCodes(s).isEmpty()) {
					y += 10 * (i - 1) + 1;
					break;
				}
			}
			
			Multimap<String, AttributeModifier> slotAttributes = null;
			if(item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemHoe) {
				GlStateManager.color(1F, 1F, 1F);
				mc.getTextureManager().bindTexture(GuiButtonChest.GENERAL_ICONS_RESOURCE);
				Gui.drawModalRectWithCustomSizedTexture(x, y, 238, 0, 9, 9, 256, 256);
				slotAttributes = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);

				double damage = getAttribute(mc.player, stack, slotAttributes, "generic.attackDamage");
				String dmgStr = ItemStack.DECIMALFORMAT.format(damage);
				mc.fontRendererObj.drawStringWithShadow(dmgStr, x + 12, y + 1, 0xFFFFFF);
				x += mc.fontRendererObj.getStringWidth(dmgStr) + 20;
				
				GlStateManager.color(1F, 1F, 1F);
				mc.getTextureManager().bindTexture(GuiButtonChest.GENERAL_ICONS_RESOURCE);
				Gui.drawModalRectWithCustomSizedTexture(x, y, 247, 0, 9, 9, 256, 256);
				double speed = getAttribute(mc.player, stack, slotAttributes, "generic.attackSpeed");
				String spdStr = ItemStack.DECIMALFORMAT.format(speed);
				mc.fontRendererObj.drawStringWithShadow(spdStr, x + 12, y + 1, 0xFFFFFF);
				x += mc.fontRendererObj.getStringWidth(spdStr) + 20;
			} else if(item instanceof ItemArmor) {
				ItemArmor armor = (ItemArmor) item;
				EntityEquipmentSlot slot = armor.getEquipmentSlot();
				
				GlStateManager.color(1F, 1F, 1F);
				mc.getTextureManager().bindTexture(GuiButtonChest.GENERAL_ICONS_RESOURCE);
				slotAttributes = stack.getAttributeModifiers(slot);
				
				double armorLevel = getAttribute(mc.player, stack, slotAttributes, "generic.armor");
				Gui.drawModalRectWithCustomSizedTexture(x, y, 229, 0, 9, 9, 256, 256);
				String armorStr = ItemStack.DECIMALFORMAT.format(armorLevel);
				mc.fontRendererObj.drawStringWithShadow(armorStr, x + 12, y + 1, 0xFFFFFF);
				x += mc.fontRendererObj.getStringWidth(armorStr) + 20;
				
				double toughness = getAttribute(mc.player, stack, slotAttributes, "generic.armorToughness");
				mc.getTextureManager().bindTexture(GuiButtonChest.GENERAL_ICONS_RESOURCE);
				Gui.drawModalRectWithCustomSizedTexture(x, y, 220, 0, 9, 9, 256, 256);
				String toughnessStr = ItemStack.DECIMALFORMAT.format(toughness);
				mc.fontRendererObj.drawStringWithShadow(toughnessStr, x + 12, y + 1, 0xFFFFFF);
				x += mc.fontRendererObj.getStringWidth(toughnessStr) + 20;
			}
			
			ImmutableSet<String> validAttributes = ImmutableSet.of("generic.attackDamage", "generic.attackSpeed", "generic.armor", "generic.armorToughness");
			if(slotAttributes != null)
				for(String s : slotAttributes.keySet())
					if(!validAttributes.contains(s)) {
						mc.fontRendererObj.drawStringWithShadow(TextFormatting.YELLOW + "[+]", x, y + 1, 0xFFFFFF);
						break;
					}
			
			GlStateManager.popMatrix();
		}
	}

	private boolean isAttributeStrippable(ItemStack stack) {
		Item item = stack.getItem();
		return !stack.isEmpty() && (item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemArmor || item instanceof ItemHoe);
	}

	private double getAttribute(EntityPlayer player, ItemStack stack, Multimap<String, AttributeModifier> map, String key) {
		Collection<AttributeModifier> collection = map.get(key);
		if(collection.isEmpty())
			return 0;
		
        AttributeModifier attributemodifier = collection.iterator().next();
        double d0 = attributemodifier.getAmount();
        boolean flag = false;

        if(key.equals("generic.attackDamage")) {
            d0 += player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
            d0 += (double) EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
        }
        else if(key.equals("generic.attackSpeed"))
            d0 += player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
        
        return d0;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}


}
