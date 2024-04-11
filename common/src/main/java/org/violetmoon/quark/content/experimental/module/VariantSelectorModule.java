package org.violetmoon.quark.content.experimental.module;

import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.QuarkClient;
import org.violetmoon.quark.base.client.handler.ClientUtil;
import org.violetmoon.quark.base.network.message.experimental.PlaceVariantUpdateMessage;
import org.violetmoon.quark.content.experimental.client.screen.VariantSelectorScreen;
import org.violetmoon.quark.content.experimental.client.tooltip.VariantsComponent;
import org.violetmoon.quark.content.experimental.config.VariantsConfig;
import org.violetmoon.quark.content.experimental.item.HammerItem;
import org.violetmoon.zeta.client.event.load.ZKeyMapping;
import org.violetmoon.zeta.client.event.load.ZTooltipComponents;
import org.violetmoon.zeta.client.event.play.ZClientTick;
import org.violetmoon.zeta.client.event.play.ZInput;
import org.violetmoon.zeta.client.event.play.ZRenderGuiOverlay;
import org.violetmoon.zeta.client.event.play.ZRenderTooltip;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.bus.ZPhase;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.event.play.entity.ZEntityJoinLevel;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@ZetaLoadModule(
	category = "experimental", enabledByDefault = false,
	description = "Allows placing variant blocks automatically via a selector menu triggered from a keybind"
)
public class VariantSelectorModule extends ZetaModule {

	private static final String TAG_CURRENT_VARIANT = Quark.MOD_ID + ":CurrentSelectedVariant";

	private static String clientVariant = null;
	private static boolean staticEnabled;

	@Config(description = "Set this to true to automatically convert any dropped variant items into their originals. Do this ONLY if you intend to take control of every recipe via a data pack or equivalent, as this will introduce dupes otherwise.")
	public static boolean convertVariantItems = false;

	@Config(flag = "hammer", description = "Enable the hammer, allowing variants to be swapped between eachother, including the original block. Do this ONLY under the same circumstances as Convert Variant Items.")
	public static boolean enableHammer = false;

	@Config
	public static boolean showTooltip = true;
	@Config
	public static boolean alignHudToHotbar = false;
	@Config
	public static boolean showSimpleHud = false;
	@Config
	public static boolean showHud = true;
	@Config
	public static boolean enableGreenTint = true;
	@Config
	public static boolean overrideHeldItemRender = true;
	@Config
	public static int hudOffsetX = 0;
	@Config
	public static int hudOffsetY = 0;

	@Config
	public static VariantsConfig variants = new VariantsConfig();

	public static Item hammer;

	@LoadEvent
	public final void register(ZRegister event) {
		hammer = new HammerItem(this).setCondition(() -> enableHammer);
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}

	public static String getSavedVariant(Player player) {
		if(player.level().isClientSide)
			return clientVariant;

		return player.getPersistentData().getString(TAG_CURRENT_VARIANT);
	}

	public static void setSavedVariant(ServerPlayer player, String variant) {
		if(variant == null)
			variant = "";

		if(variant.isEmpty() || variants.isKnownVariant(variant))
			player.getPersistentData().putString(TAG_CURRENT_VARIANT, variant);
	}

	private static Block getMainHandVariantBlock(Player player, String variant) {
		ItemStack mainHand = player.getMainHandItem();
		if(mainHand.getItem() instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			return getVariantForBlock(block, variant);
		}

		return null;
	}

	public static Block getVariantForBlock(Block block, String variant) {
		return variants.getBlockForVariant(block, variant);
	}

	public static Block getVariantOrOriginal(Block block, String variant) {
		if(!variants.isVariant(block) && !variants.isOriginal(block))
			return null;

		block = variants.getOriginalBlock(block);

		if(variant == null || variant.isEmpty())
			return variants.getOriginalBlock(block);

		return getVariantForBlock(block, variant);
	}

	@PlayEvent
	public void addEntityToWorld(ZEntityJoinLevel event) {
		Entity entity = event.getEntity();
		if(convertVariantItems && entity instanceof ItemEntity ie) {
			ItemStack stack = ie.getItem();
			if(stack.getItem() instanceof BlockItem bi) {
				Block block = bi.getBlock();
				Block otherBlock = variants.getOriginalBlock(block);

				if(otherBlock != block) {
					ItemStack clone = new ItemStack(otherBlock.asItem());
					clone.setTag(stack.getTag());
					clone.setCount(stack.getCount()); //TODO: maybe this leads to double slab dupes lol
					ie.setItem(clone);
				}
			}

		}
	}

	public static BlockState modifyBlockPlacementState(BlockState state, BlockPlaceContext ctx) {
		if(!staticEnabled || state == null)
			return state;

		Player player = ctx.getPlayer();
		if(player != null) {
			String variant = getSavedVariant(player);
			if(variant != null && !variant.isEmpty()) {
				Block target = getVariantForBlock(state.getBlock(), variant);
				return target.getStateForPlacement(ctx);
			}
		}

		return state;
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends VariantSelectorModule {
		private static KeyMapping variantSelectorKey;

		public static ItemStack modifyHeldItemStack(AbstractClientPlayer player, ItemStack stack) {
			if(!staticEnabled || !overrideHeldItemRender)
				return stack;

			Minecraft mc = Minecraft.getInstance();
			if(player == mc.player && stack.getItem() instanceof BlockItem bi) {
				Block block = bi.getBlock();
				if(clientVariant != null && !clientVariant.isEmpty()) {
					Block variant = variants.getBlockForVariant(block, clientVariant);
					if(variant != null && variant != block)
						return new ItemStack(variant);
				}
			}

			return stack;
		}

		@LoadEvent
		public void registerKeybinds(ZKeyMapping event) {
			variantSelectorKey = event.init("quark.keybind.variant_selector", "r", QuarkClient.MISC_GROUP);
		}

		public static void setClientVariant(String variant, boolean sync) {
			clientVariant = variant;

			if(sync) {
				if(variant == null)
					variant = "";
				QuarkClient.ZETA_CLIENT.sendToServer(new PlaceVariantUpdateMessage(variant));
			}
		}

		@PlayEvent
		public void keystroke(ZInput.Key event) {
			Minecraft mc = Minecraft.getInstance();
			if(mc.level != null && event.getAction() == GLFW.GLFW_PRESS) {
				if(variantSelectorKey.isDown()) {

					ItemStack stack = mc.player.getMainHandItem();
					if(stack.is(hammer)) {
						HitResult result = mc.hitResult;
						if(result instanceof BlockHitResult bhr) {
							BlockPos pos = bhr.getBlockPos();
							Block block = mc.player.level().getBlockState(pos).getBlock();
							stack = new ItemStack(variants.getOriginalBlock(block));
						}
					}

					if(!stack.isEmpty() && stack.getItem() instanceof BlockItem)
						mc.setScreen(new VariantSelectorScreen(stack, variantSelectorKey, clientVariant, variants.getVisibleVariants()));

					return;
				}
			}
		}

		@LoadEvent
		public void registerClientTooltipComponentFactories(ZTooltipComponents event) {
			event.register(VariantsComponent.class);
		}

		@PlayEvent
		public void gatherComponents(ZRenderTooltip.GatherComponents event) {
			if(!showTooltip)
				return;

			ItemStack stack = event.getItemStack();

			if(hasTooltip(stack)) {
				List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
				int index = 1;

				if(Screen.hasShiftDown()) {
					elements.add(index, Either.left(Component.translatable("quark.misc.variant_tooltip_header").withStyle(ChatFormatting.GRAY)));
					elements.add(index + 1, Either.right(new VariantsComponent(stack)));
				} else
					elements.add(index, Either.left(Component.translatable("quark.misc.variant_tooltip_hold_shift").withStyle(ChatFormatting.GRAY)));
			}
		}

		private boolean hasTooltip(ItemStack stack) {
			return !stack.isEmpty() && stack.getItem() instanceof BlockItem bi && !variants.getAllVariants(bi.getBlock()).isEmpty();
		}

		@PlayEvent
		public void clientTick(ZClientTick event) {
			if(event.getPhase() != ZPhase.END)
				return;

			Minecraft mc = Minecraft.getInstance();
			Level level = mc.level;
			if(level == null)
				setClientVariant(null, false);
			else {
				if(clientVariant == null)
					setClientVariant("", true);
			}
		}

		@PlayEvent
		public void onRender(ZRenderGuiOverlay.Crosshair.Pre event) {
			GuiGraphics guiGraphics = event.getGuiGraphics();

			Minecraft mc = Minecraft.getInstance();
			if(mc.screen instanceof VariantSelectorScreen || !showHud)
				return;

			Player player = mc.player;
			String savedVariant = getSavedVariant(player);

			if(savedVariant != null) {
				ItemStack mainHand = player.getMainHandItem();
				ItemStack displayLeft = mainHand.copy();

				Block variantBlock = null;

				if(displayLeft.is(hammer)) {
					HitResult result = mc.hitResult;
					if(result instanceof BlockHitResult bhr) {
						BlockPos pos = bhr.getBlockPos();
						Block testBlock = player.level().getBlockState(pos).getBlock();

						displayLeft = new ItemStack(testBlock);
						variantBlock = getVariantOrOriginal(testBlock, savedVariant);
					}
				} else
					variantBlock = getMainHandVariantBlock(player, savedVariant);

				if(variantBlock != null) {
					ItemStack displayRight = new ItemStack(variantBlock);

					if(displayLeft.getItem() == displayRight.getItem())
						return;

					Window window = event.getWindow();
					int x = window.getGuiScaledWidth() / 2;
					int y = window.getGuiScaledHeight() / 2 + 12;

					if(alignHudToHotbar) {
						HumanoidArm arm = mc.options.mainHand().get();
						if(arm == HumanoidArm.RIGHT)
							x += 125;
						else
							x -= 93;

						y = window.getGuiScaledHeight() - 19;
					}

					int offset = 8;
					int width = 16;

					displayLeft.setCount(1);

					int posX = x - offset - width + hudOffsetX;
					int posY = y + hudOffsetY;

					if(!showSimpleHud) {
						guiGraphics.renderFakeItem(displayLeft, posX, posY);

						RenderSystem.enableBlend();
						RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						RenderSystem.setShader(GameRenderer::getPositionTexShader);
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.75F);

						guiGraphics.blit(ClientUtil.GENERAL_ICONS, posX + 8, posY, 0, 141, 22, 15, 256, 256);

						posX += width * 2;
					} else {
						final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widget.png");

						if(alignHudToHotbar) {
							RenderSystem.enableBlend();
							RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
							RenderSystem.setShader(GameRenderer::getPositionTexShader);
							if(enableGreenTint)
								RenderSystem.setShaderColor(0.5F, 1.0F, 0.5F, 1.0F);
							else
								RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
							guiGraphics.blit(WIDGETS_LOCATION, posX - 3, posY - 3, 24, 23, 22, 22, 256, 256);
						} else
							posX += width;
					}

					guiGraphics.renderFakeItem(displayRight, posX, posY);
				}
			}
		}
	}

}
