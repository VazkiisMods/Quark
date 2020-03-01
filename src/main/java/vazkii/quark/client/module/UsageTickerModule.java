package vazkii.quark.client.module;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.Config;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.tools.item.TrowelItem;

@LoadModule(category = ModuleCategory.CLIENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class UsageTickerModule extends Module {

	public static List<TickerElement> elements = new ArrayList<>();
	
	@Config(description = "Switch the armor display to the off hand side and the hand display to the main hand side")
	public static boolean invert = false;
	
	@Config public static int shiftLeft = 0;
	@Config public static int shiftRight = 0;
	
	@Config public static boolean enableMainHand = true;
	@Config public static boolean enableOffHand = true;
	@Config public static boolean enableArmor = true;
	
	@Override
	public void configChanged() {
		elements = new ArrayList<>();
		
		if(enableMainHand)
			elements.add(new TickerElement(EquipmentSlotType.MAINHAND));
		if(enableOffHand)
			elements.add(new TickerElement(EquipmentSlotType.OFFHAND));
		if(enableArmor) {
			elements.add(new TickerElement(EquipmentSlotType.HEAD));
			elements.add(new TickerElement(EquipmentSlotType.CHEST));
			elements.add(new TickerElement(EquipmentSlotType.LEGS));
			elements.add(new TickerElement(EquipmentSlotType.FEET));
		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void clientTick(ClientTickEvent event) {
		if(event.phase == Phase.START) {
			Minecraft mc = Minecraft.getInstance();
			if(mc.player != null)
				elements.forEach((ticker) -> ticker.tick(mc.player));
		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void renderHUD(RenderGameOverlayEvent.Post event) {
		if(event.getType() == ElementType.HOTBAR) {
			MainWindow window = event.getWindow();
			PlayerEntity player = Minecraft.getInstance().player;
			float partial = event.getPartialTicks();
			elements.forEach((ticker) -> ticker.render(window, player, invert, partial));
		}
	}
	
	public static class TickerElement {
		
		private static final int MAX_TIME = 60;
		private static final int ANIM_TIME = 5;

		public int liveTicks;
		public final EquipmentSlotType slot;
		public ItemStack currStack = ItemStack.EMPTY;
		public int currCount;
		
		public TickerElement(EquipmentSlotType slot) {
			this.slot = slot;
		}
		
		@OnlyIn(Dist.CLIENT)
		public void tick(PlayerEntity player) {
			ItemStack heldStack = getStack(player);
			
			int count = getStackCount(player, heldStack);

			heldStack = getDisplayedStack(heldStack, count);

			if(heldStack.isEmpty())
				liveTicks = 0;
			else if(shouldChange(heldStack, currStack, count, currCount)) {
				boolean done = liveTicks == 0;
				boolean animatingIn = liveTicks > MAX_TIME - ANIM_TIME;
				boolean animatingOut = liveTicks < ANIM_TIME && !done;
				if(animatingOut)
					liveTicks = MAX_TIME - liveTicks;
				else if(!animatingIn) {
					if(!done)
						liveTicks = MAX_TIME - ANIM_TIME;
					else liveTicks = MAX_TIME;
				}
			} else if(liveTicks > 0)
				liveTicks--;
				
			currCount = count;
			currStack = heldStack;
		}
		
		@OnlyIn(Dist.CLIENT)
		public void render(MainWindow window, PlayerEntity player, boolean invert, float partialTicks) {
			if(liveTicks > 0) {
				float animProgress; 
				
				if(liveTicks < ANIM_TIME)
					animProgress = Math.max(0, liveTicks - partialTicks) / ANIM_TIME;
				else animProgress = Math.min(ANIM_TIME, (MAX_TIME - liveTicks) + partialTicks) / ANIM_TIME;
				
				float anim = -animProgress * (animProgress - 2) * 20F;
				
				float x = window.getScaledWidth() / 2f;
				float y = window.getScaledHeight() - anim;
				
				int barWidth = 190;
				boolean armor = slot.getSlotType() == Group.ARMOR;
				
				HandSide primary = player.getPrimaryHand();
				HandSide ourSide = (armor != invert) ? primary : primary.opposite();
				
				int slots = armor ? 4 : 2;
				int index = slots - slot.getIndex() - 1;
				float mul = ourSide == HandSide.LEFT ? -1 : 1;

				if(ourSide != primary && !player.getHeldItem(Hand.OFF_HAND).isEmpty())
					barWidth += 58;
				
				Minecraft mc = Minecraft.getInstance();
				x += (barWidth / 2f) * mul + index * 20;
				if(ourSide == HandSide.LEFT) {
					x -= slots * 20;
					x += shiftLeft;
				} else x += shiftRight;
					
				ItemStack stack = getRenderedStack(player);
				
				RenderSystem.pushMatrix();
				RenderSystem.translatef(x, y, 0);
				RenderHelper.enableStandardItemLighting();
				mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
				mc.getItemRenderer().renderItemOverlays(Minecraft.getInstance().fontRenderer, stack, 0, 0);
				RenderHelper.disableStandardItemLighting();
				RenderSystem.popMatrix();
			}
		}
		
		@OnlyIn(Dist.CLIENT)
		public boolean shouldChange(ItemStack currStack, ItemStack prevStack, int currentTotal, int pastTotal) {
			return !prevStack.isItemEqual(currStack) || (currStack.isDamageable() && currStack.getDamage() != prevStack.getDamage()) || currentTotal != pastTotal;
		}
		
		@OnlyIn(Dist.CLIENT)
		public ItemStack getStack(PlayerEntity player) {
			return player.getItemStackFromSlot(slot);
		}
		
		@OnlyIn(Dist.CLIENT)
		public ItemStack getDisplayedStack(ItemStack stack, int count) {
			boolean verifySize = true;
			if(stack.getItem() instanceof BowItem && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) == 0) {
				stack = new ItemStack(Items.ARROW);
				verifySize = false;
			}
			
			if(stack.getItem() instanceof TrowelItem) {
				stack = TrowelItem.getLastStack(stack);
				verifySize = false;
			}
			
			if(!stack.isStackable() && slot.getSlotType() == Group.HAND)
				return ItemStack.EMPTY;
			
			if(verifySize && stack.isStackable() && count == stack.getCount())
				return ItemStack.EMPTY;
			
			return stack;
		}
		
		@OnlyIn(Dist.CLIENT)
		public ItemStack getRenderedStack(PlayerEntity player) {
			ItemStack stack = getStack(player);
			int count = getStackCount(player, stack);
			ItemStack displayStack = getDisplayedStack(stack, count).copy();
			if(displayStack != stack)
				count = getStackCount(player,  displayStack);
			displayStack.setCount(count);
			
			return displayStack;
		}
		
		@OnlyIn(Dist.CLIENT)
		public int getStackCount(PlayerEntity player, ItemStack stack) {
			if(!stack.isStackable())
				return 1;
			
			Predicate<ItemStack> predicate = (stackAt) -> ItemStack.areItemsEqual(stackAt, stack) && ItemStack.areItemStackTagsEqual(stackAt, stack);
			
			if(stack.getItem() == Items.ARROW)
				predicate = (stackAt) -> stackAt.getItem() instanceof ArrowItem;
			
			int total = 0;
			for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack stackAt = player.inventory.getStackInSlot(i);
				if(predicate.test(stackAt))
					total += stackAt.getCount();
			}
			
			return total;
		}
				
	}
	
}
