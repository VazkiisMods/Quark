/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [06/06/2016, 01:40:29 (GMT)]
 */
package org.violetmoon.quark.content.management.module;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.SharedConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.glfw.GLFW;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.network.QuarkNetwork;
import org.violetmoon.quark.base.network.message.ShareItemMessage;
import org.violetmoon.quark.mixin.accessor.AccessorServerGamePacketListenerImpl;
import org.violetmoon.quark.mixin.client.accessor.AccessorLocalPlayer;
import org.violetmoon.zeta.client.event.play.ZScreen;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

import java.time.Instant;
import java.util.List;

@ZetaLoadModule(category = "management")
public class ItemSharingModule extends ZetaModule {

	@Config
	public static boolean renderItemsInChat = true;

	//global variable to apply 5 sec cooldown
	private static long lastShareTimestamp = -1;

	// used in a mixin because rendering overrides are cursed by necessity hahayes
	public static float alphaValue = 1F;

	public boolean click() {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.screen;

		if(screen instanceof AbstractContainerScreen<?> gui && Screen.hasShiftDown()) {
			List<? extends GuiEventListener> children = gui.children();
			for(GuiEventListener c : children)
				if(c instanceof EditBox tf) {
					if(tf.isFocused())
						return false;
				}

			Slot slot = gui.getSlotUnderMouse();
			if(slot != null) {
				ItemStack stack = slot.getItem();

				if(!stack.isEmpty()) {
					if(mc.level != null && mc.level.getGameTime() - lastShareTimestamp > 100) {
						lastShareTimestamp = mc.level.getGameTime();
					} else return false;

					if (mc.player instanceof AccessorLocalPlayer accessorLocalPlayer) {
						Component itemComp = stack.getDisplayName();
						String rawMessage = SharedConstants.filterText(itemComp.getString());

						@SuppressWarnings("UnstableApiUsage")
						String transformedMessage = ForgeHooksClient.onClientSendMessage(rawMessage);

						if (transformedMessage.equals(rawMessage)) {
							ChatMessageContent content = accessorLocalPlayer.quark$buildSignedContent(rawMessage, itemComp);
							MessageSigner sign = accessorLocalPlayer.quark$createMessageSigner();
							LastSeenMessages.Update update = mc.player.connection.generateMessageAcknowledgements();
							MessageSignature signature = accessorLocalPlayer.quark$signMessage(sign, content, update.lastSeen());

							ShareItemMessage message = new ShareItemMessage(stack, content.plain(), sign.timeStamp(), sign.salt(), signature, content.isDecorated(), update);
							QuarkNetwork.sendToServer(message);

							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public static void shareItem(ServerPlayer player, String message, ItemStack stack, Instant timeStamp, long salt, MessageSignature signature, boolean signedPreview, LastSeenMessages.Update lastSeenMessages) {
		if (!Quark.ZETA.modules.isEnabled(ItemSharingModule.class))
			return;

		Component itemComp = stack.getDisplayName();

		((AccessorServerGamePacketListenerImpl) player.connection).quark$chatPreviewCache().set(message, itemComp);

		player.connection.handleChat(new ServerboundChatPacket(message, timeStamp, salt, signature, signedPreview, lastSeenMessages));
	}

	public static MutableComponent createStackComponent(ItemStack stack, MutableComponent component) {
		if (!Quark.ZETA.modules.isEnabled(ItemSharingModule.class) || !renderItemsInChat)
			return component;

		Style style = component.getStyle();
		if (stack.getCount() > 64) {
			ItemStack copyStack = stack.copy();
			copyStack.setCount(64);
			style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(copyStack)));
			component.withStyle(style);
		}

		MutableComponent out = Component.literal("   ");
		out.setStyle(style);
		return out.append(component);
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends ItemSharingModule {
		public static void renderItemForMessage(PoseStack poseStack, FormattedCharSequence sequence, float x, float y, int color) {
			if (!Quark.ZETA.modules.isEnabled(ItemSharingModule.class) || !renderItemsInChat)
				return;

			Minecraft mc = Minecraft.getInstance();

			StringBuilder before = new StringBuilder();

			int halfSpace = mc.font.width(" ") / 2;

			sequence.accept((counter_, style, character) -> {
				String sofar = before.toString();
				if (sofar.endsWith("   ")) {
					render(mc, poseStack, sofar.substring(0, sofar.length() - 2), character == ' ' ? 0 : -halfSpace, x, y, style, color);
					return false;
				}
				before.append((char) character);
				return true;
			});
		}

		@PlayEvent
		public void onKeyInput(ZScreen.KeyPressed.Pre event) {
			KeyMapping key = getChatKey();
			if(key.getKey().getType() == InputConstants.Type.KEYSYM && event.getKeyCode() == key.getKey().getValue())
				event.setCanceled(click());

		}

		@PlayEvent
		public void onMouseInput(ZScreen.MouseButtonPressed.Post event) {
			KeyMapping key = getChatKey();
			int btn = event.getButton();
			if(key.getKey().getType() == InputConstants.Type.MOUSE && btn != GLFW.GLFW_MOUSE_BUTTON_LEFT && btn == key.getKey().getValue())
				event.setCanceled(click());
		}

		private static void render(Minecraft mc, PoseStack pose, String before, float extraShift, float x, float y, Style style, int color) {
			float a = (color >> 24 & 255) / 255.0F;

			HoverEvent hoverEvent = style.getHoverEvent();
			if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
				HoverEvent.ItemStackInfo contents = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);

				ItemStack stack = contents != null ? contents.getItemStack() : ItemStack.EMPTY;

				if (stack.isEmpty())
					stack = new ItemStack(Blocks.BARRIER); // for invalid icon

				float shift = mc.font.width(before) + extraShift;

				if (a > 0) {
					alphaValue = a;

					PoseStack poseStack = RenderSystem.getModelViewStack();

					poseStack.pushPose();

					poseStack.mulPoseMatrix(pose.last().pose());

					poseStack.translate(shift + x, y, 0);
					poseStack.scale(0.5f, 0.5f, 0.5f);
					mc.getItemRenderer().renderGuiItem(stack, 0, 0);
					poseStack.popPose();

					RenderSystem.applyModelViewMatrix();

					alphaValue = 1F;
				}
			}
		}

		private KeyMapping getChatKey() {
			return Minecraft.getInstance().options.keyChat;
		}
	}
}
