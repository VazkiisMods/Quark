package vazkii.quark.client.feature;

import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Keyboard;

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
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;

public class VisualStatDisplay extends Feature {

	public static final ImmutableSet<String> VALID_ATTRIBUTES = ImmutableSet.of("generic.attackDamage", "generic.attackSpeed", "generic.armor", "generic.armorToughness");
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void makeTooltip(ItemTooltipEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		ItemStack stack = event.getItemStack();

		if(Keyboard.isCreated() && !GuiScreen.isShiftKeyDown() && isAttributeStrippable(stack)) {
			List<String> tooltip = event.getToolTip();
			String allDesc = "";
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
					if(VALID_ATTRIBUTES.contains(s))
						allDesc += ItemStack.DECIMALFORMAT.format(getAttribute(event.getEntityPlayer(), stack, slotAttributes, s));
						
					String name = I18n.translateToLocal("attribute.name." + s);
					for(int i = 1; i < tooltip.size(); i++)
						if(tooltip.get(i).contains(name)) {
							tooltip.remove(i);
							clearedAny = true;
							break;
						}
				}
			}

			if(clearedAny) {
				int len = mc.fontRenderer.getStringWidth(allDesc) + 32;
				String spaces = "";
				while(mc.fontRenderer.getStringWidth(spaces) < len)
					spaces += " " ;
				
				tooltip.add(1, spaces);
			}
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
				if(TextFormatting.getTextWithoutFormattingCodes(s).trim().isEmpty()) {
					y += 10 * (i - 1) + 1;
					break;
				}
			}
			
			Multimap<String, AttributeModifier> slotAttributes = null;
			if(item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemHoe) {
				slotAttributes = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);

				double damage = getAttribute(mc.player, stack, slotAttributes, "generic.attackDamage");
				if(damage > 0) {
					GlStateManager.color(1F, 1F, 1F);
					mc.getTextureManager().bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
					Gui.drawModalRectWithCustomSizedTexture(x, y, 238, 0, 9, 9, 256, 256);

					String dmgStr = ItemStack.DECIMALFORMAT.format(damage);
					mc.fontRenderer.drawStringWithShadow(dmgStr, x + 12, y + 1, 0xFFFFFF);
					x += mc.fontRenderer.getStringWidth(dmgStr) + 20;
				}

				double speed = getAttribute(mc.player, stack, slotAttributes, "generic.attackSpeed");
				if(speed > 0) {
					GlStateManager.color(1F, 1F, 1F);
					mc.getTextureManager().bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
					Gui.drawModalRectWithCustomSizedTexture(x, y, 247, 0, 9, 9, 256, 256);
					
					String spdStr = ItemStack.DECIMALFORMAT.format(speed);
					mc.fontRenderer.drawStringWithShadow(spdStr, x + 12, y + 1, 0xFFFFFF);
					x += mc.fontRenderer.getStringWidth(spdStr) + 20;
				}
			} else if(item instanceof ItemArmor) {
				ItemArmor armor = (ItemArmor) item;
				EntityEquipmentSlot slot = armor.getEquipmentSlot();
				
				slotAttributes = stack.getAttributeModifiers(slot);
				
				double armorLevel = getAttribute(mc.player, stack, slotAttributes, "generic.armor");
				if(armorLevel > 0) {
					GlStateManager.color(1F, 1F, 1F);
					mc.getTextureManager().bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
					Gui.drawModalRectWithCustomSizedTexture(x, y, 229, 0, 9, 9, 256, 256);
					
					String armorStr = ItemStack.DECIMALFORMAT.format(armorLevel);
					mc.fontRenderer.drawStringWithShadow(armorStr, x + 12, y + 1, 0xFFFFFF);
					x += mc.fontRenderer.getStringWidth(armorStr) + 20;
				}
				
				double toughness = getAttribute(mc.player, stack, slotAttributes, "generic.armorToughness");
				if(toughness > 0) {
					GlStateManager.color(1F, 1F, 1F);
					mc.getTextureManager().bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
					Gui.drawModalRectWithCustomSizedTexture(x, y, 220, 0, 9, 9, 256, 256);
					
					String toughnessStr = ItemStack.DECIMALFORMAT.format(toughness);
					mc.fontRenderer.drawStringWithShadow(toughnessStr, x + 12, y + 1, 0xFFFFFF);
					x += mc.fontRenderer.getStringWidth(toughnessStr) + 20;
				}
			}
			
			if(slotAttributes != null)
				for(String s : slotAttributes.keySet())
					if(!VALID_ATTRIBUTES.contains(s)) {
						mc.fontRenderer.drawStringWithShadow(TextFormatting.YELLOW + "[+]", x, y + 1, 0xFFFFFF);
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
		if(player == null) // apparently this can happen
			return 0;
		
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
