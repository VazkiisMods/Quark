package vazkii.quark.content.experimental.module;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.Key;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.ModKeybindHandler;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.experimental.PlaceVariantUpdateMessage;
import vazkii.quark.content.experimental.client.screen.VariantSelectorScreen;
import vazkii.quark.content.experimental.client.tooltip.VariantsComponent;
import vazkii.quark.content.experimental.config.BlockSuffixConfig;
import vazkii.quark.content.experimental.item.HammerItem;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZKeyMapping;
import vazkii.zeta.event.client.ZTooltipComponents;

@LoadModule(category = "experimental", hasSubscriptions = true, enabledByDefault = false,
		description = "Allows placing variant blocks automatically via a selector menu triggered from a keybind")
public class VariantSelectorModule extends QuarkModule {

	private static final String TAG_CURRENT_VARIANT = Quark.MOD_ID + ":CurrentSelectedVariant";

	private static String clientVariant = null;
	private static boolean staticEnabled;

	@Config(description = "Set this to true to automatically convert any dropped variant items into their originals. Do this ONLY if you intend to take control of every recipe via a data pack or equivalent, as this will introduce dupes otherwise.")
	public static boolean convertVariantItems = false;

	@Config(flag = "hammer", description = "Enable the hammer, allowing variants to be swapped between eachother, including the original block. Do this ONLY under the same circumstances as Convert Variant Items.")
	public static boolean enableHammer = false;
	
	@Config public static boolean showTooltip = true;
	@Config public static boolean alignHudToHotbar = false;
	@Config public static boolean showSimpleHud = false;
	@Config public static boolean showHud = true;
	@Config public static boolean enableGreenTint = true;
	@Config public static boolean overrideHeldItemRender = true;

	@Config
	public static BlockSuffixConfig variants = new BlockSuffixConfig(
			Arrays.asList("slab", "stairs", "wall", "fence", "fence_gate", "vertical_slab"),
			Arrays.asList("quark"),
			Arrays.asList("carpet=slab")
			);

	public static Item hammer;

	@OnlyIn(Dist.CLIENT)
	private static KeyMapping variantSelectorKey;

	@LoadEvent
	public final void register(ZRegister event) {
		hammer = new HammerItem(this).setCondition(() -> enableHammer);
	}

	@LoadEvent
	public void registerKeybinds(ZKeyMapping event) {
		variantSelectorKey = ModKeybindHandler.init(event, "variant_selector", "r", ModKeybindHandler.MISC_GROUP);
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}

	public static String getSavedVariant(Player player) {
		if(player.level.isClientSide)
			return clientVariant;

		return player.getPersistentData().getString(TAG_CURRENT_VARIANT);
	}

	public static void setSavedVariant(ServerPlayer player, String variant) {
		if(variant == null)
			variant = "";

		if(variant.isEmpty() || variants.isKnownVariant(variant))
			player.getPersistentData().putString(TAG_CURRENT_VARIANT, variant);
	}

	@OnlyIn(Dist.CLIENT)
	public static void setClientVariant(String variant, boolean sync) {
		clientVariant = variant;
		
		if(sync) {
			if(variant == null)
				variant = "";
			QuarkNetwork.sendToServer(new PlaceVariantUpdateMessage(variant));
		}
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
		Block variantBlock = variants.getBlockForVariant(block, variant);
		if(variantBlock != null)
			return variantBlock;

		return null;
	}

	public static Block getVariantOrOriginal(Block block, String variant) {
		block = variants.getOriginalBlock(block);

		if(variant == null || variant.isEmpty())
			return variants.getOriginalBlock(block);

		return getVariantForBlock(block, variant);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void keystroke(Key event) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.level != null && event.getAction() == GLFW.GLFW_PRESS) {
			if(variantSelectorKey.isDown()) {

				ItemStack stack = mc.player.getMainHandItem();
				if(stack.is(hammer)) {
					HitResult result = mc.hitResult;
					if(result instanceof BlockHitResult bhr) {
						BlockPos pos = bhr.getBlockPos();
						Block block = mc.player.level.getBlockState(pos).getBlock();
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
	@OnlyIn(Dist.CLIENT)
	public void registerClientTooltipComponentFactories(ZTooltipComponents event) {
		event.register(VariantsComponent.class);
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void gatherComponents(RenderTooltipEvent.GatherComponents event) {
		if(!showTooltip)
			return;
		
		ItemStack stack = event.getItemStack();			

		if(hasTooltip(stack)) {
			List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
			int index = 1;
			
			if(Screen.hasShiftDown()) {
				elements.add(index, Either.left(Component.translatable("quark.misc.variant_tooltip_header").withStyle(ChatFormatting.GRAY)));
				elements.add(index + 1, Either.right(new VariantsComponent(stack)));
			}
			else 
				elements.add(index, Either.left(Component.translatable("quark.misc.variant_tooltip_hold_shift").withStyle(ChatFormatting.GRAY)));
		}
	}
	
	private boolean hasTooltip(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof BlockItem bi && !variants.getAllVariants(bi.getBlock()).isEmpty();
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void clientTick(ClientTickEvent event) {
		if(event.phase != Phase.END)
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

	@SubscribeEvent
	public void addEntityToWorld(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		if(convertVariantItems && entity instanceof ItemEntity ie) {
			ItemStack stack = ie.getItem();
			if(stack.getItem() instanceof BlockItem bi) {
				Block block = bi.getBlock();
				Block otherBlock = variants.getOriginalBlock(block);

				if(otherBlock != block) {
					ItemStack clone = new ItemStack(otherBlock.asItem());
					clone.setTag(stack.getTag());
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
	
	@OnlyIn(Dist.CLIENT)
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

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onRender(RenderGuiOverlayEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		if(event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type() || mc.screen instanceof VariantSelectorScreen || !showHud)
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
					Block testBlock = player.level.getBlockState(pos).getBlock();

					displayLeft = new ItemStack(testBlock);
					variantBlock = getVariantOrOriginal(testBlock, savedVariant);
				}
			}
			else
				variantBlock = getMainHandVariantBlock(player, savedVariant);

			if(variantBlock != null) {
				ItemStack displayRight = new ItemStack(variantBlock);

				if(displayLeft.getItem() == displayRight.getItem())
					return;

				Window window = event.getWindow();
				int x = window.getGuiScaledWidth() / 2;
				int y = window.getGuiScaledHeight() / 2 + 12;
				
				
				showSimpleHud = true;
				alignHudToHotbar = true;
				
				if(alignHudToHotbar) {
					HumanoidArm arm = mc.options.mainHand().get();
					if(arm == HumanoidArm.RIGHT)
						x += 125;
					else x -= 93;
					
					y = window.getGuiScaledHeight() - 19;
				}
				
				int offset = 8;
				int width = 16;

				displayLeft.setCount(1);
				
				int posX = x - offset - width;
				int posY = y;

				if(!showSimpleHud) {
					mc.getItemRenderer().renderAndDecorateItem(displayLeft, posX, posY);
					
					RenderSystem.enableBlend();
					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					RenderSystem.setShader(GameRenderer::getPositionTexShader);
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.75F);
					RenderSystem.setShaderTexture(0, MiscUtil.GENERAL_ICONS);
					
					Screen.blit(event.getPoseStack(), posX + 8, posY, 0, 141, 22, 15, 256, 256);
					
					posX += width * 2;
				} 
				else {
					final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
					
					if(alignHudToHotbar) {
						RenderSystem.enableBlend();
						RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						RenderSystem.setShader(GameRenderer::getPositionTexShader);
						if(enableGreenTint)
							RenderSystem.setShaderColor(0.5F, 1.0F, 0.5F, 1.0F);
						else
							RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
						RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
						Screen.blit(event.getPoseStack(), posX -3, posY -3, 24, 23, 22, 22, 256, 256);
					} else
						posX += width;
				}	
				
				mc.getItemRenderer().renderAndDecorateItem(displayRight, posX , posY);
			}
		}
	}

}
