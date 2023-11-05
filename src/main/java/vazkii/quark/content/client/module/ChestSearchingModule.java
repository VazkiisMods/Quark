package vazkii.quark.content.client.module;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.Nullable;
import vazkii.quark.api.IQuarkButtonAllowed;
import vazkii.quark.base.client.handler.InventoryButtonHandler;
import vazkii.quark.base.client.handler.InventoryButtonHandler.ButtonTargetType;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.handler.InventoryTransferHandler;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.SimilarBlockTypeHandler;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.inputtable.RGBAColorConfig;
import vazkii.quark.content.management.client.screen.widgets.MiniInventoryButton;
import vazkii.zeta.client.event.*;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.ItemNBTHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

@ZetaLoadModule(category = "client")
public class ChestSearchingModule extends ZetaModule {

	@Config
	public RGBAColorConfig overlayColor = RGBAColorConfig.forColor(0, 0, 0, 0.67);

	public boolean searchBarShown() {
		return false;
	}

	public boolean namesMatch(ItemStack stack) {
		return false;
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends ChestSearchingModule {

		private EditBox searchBar;

		private String text = "";
		public boolean searchEnabled = false;
		private long lastClick;
		private int matched;

		@Override
		public boolean searchBarShown() {
			return searchEnabled;
		}

		@LoadEvent
		public final void clientSetup(ZClientSetup event) {
			InventoryButtonHandler.addButtonProvider(this, ButtonTargetType.CONTAINER_INVENTORY, 1, (parent, x, y) ->
					new MiniInventoryButton(parent, 3, x, y, "quark.gui.button.filter", (b) -> {
						if(searchBar != null) {
							searchEnabled = !searchEnabled;
							updateSearchStatus();
							searchBar.setFocus(true);
						}
					}).setTextureShift(() -> searchEnabled),
				null);
		}

		@PlayEvent
		public void initGui(ZScreen.Init.Post event) {
			Screen gui = event.getScreen();
			boolean apiAllowed = gui instanceof IQuarkButtonAllowed;
			if(!(gui instanceof InventoryScreen) &&
				gui instanceof AbstractContainerScreen<?> chest &&
				(apiAllowed || GeneralConfig.isScreenAllowed(gui))) {
				Minecraft mc = gui.getMinecraft();
				if(apiAllowed || InventoryTransferHandler.accepts(chest.getMenu(), mc.player)) {
					searchBar = new EditBox(mc.font, 18, 6, 117, 10, Component.literal(text));

					searchBar.setValue(text);
					searchBar.setMaxLength(50);
					searchBar.setBordered(false);
					updateSearchStatus();

					return;
				}
			}

			searchBar = null;
		}

		private void updateSearchStatus() {
			if(searchBar != null) {
				searchBar.setEditable(searchEnabled);
				searchBar.setVisible(searchEnabled);

				if(!searchEnabled)
					searchBar.setFocus(false);
			}
		}

		@PlayEvent
		public void charTyped(ZScreen.CharacterTyped.Pre event) {
			if(searchBar != null && searchBar.isFocused() && searchEnabled) {
				searchBar.charTyped(event.getCodePoint(), event.getModifiers());
				text = searchBar.getValue();

				event.setCanceled(true);
			}
		}

		@PlayEvent
		public void onKeypress(ZScreen.KeyPressed.Pre event) {
			if(searchBar != null && searchBar.isFocused() && searchEnabled) {
				searchBar.keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers());
				text = searchBar.getValue();

				event.setCanceled(event.getKeyCode() != 256); // 256 = escape
			}
		}

		@PlayEvent
		public void onClick(ZScreen.MouseButtonPressed.Pre event) {
			if(searchBar != null && searchEnabled && event.getScreen() instanceof AbstractContainerScreen<?> containerScreen) {
				searchBar.mouseClicked(event.getMouseX() - containerScreen.getGuiLeft(), event.getMouseY() - containerScreen.getGuiTop(), event.getButton());

				long time = System.currentTimeMillis();
				long delta = time - lastClick;
				if(delta < 200 && searchBar.isFocused()) {
					searchBar.setValue("");
					text = "";
				}

				lastClick = time;
			}
		}

		@PlayEvent
		public void renderForeground(ZRenderContainerScreen.Foreground event) {
			if(searchBar != null && searchEnabled) {
				PoseStack matrix = event.getPoseStack();
				AbstractContainerScreen<?> gui = event.getContainerScreen();

				matrix.pushPose();

				drawBackground(matrix, gui, searchBar.x - 11, searchBar.y - 3);

				if(!text.isEmpty()) {
					AbstractContainerMenu container = gui.getMenu();
					matched = 0;
					for(Slot s : container.slots) {
						if(s.isActive()) {
							ItemStack stack = s.getItem();
							if(!namesMatch(stack, text)) {
								int x = s.x;
								int y = s.y;

								Screen.fill(matrix, x, y, x + 16, y + 16, overlayColor.getColor());
							} else matched++;
						}
					}
				}

				if(matched == 0 && !text.isEmpty())
					searchBar.setTextColor(0xFF5555);
				else searchBar.setTextColor(0xFFFFFF);

				searchBar.render(matrix, 0, 0, 0);
				matrix.popPose();
			}
		}

		private void drawBackground(PoseStack matrix, Screen gui, int x, int y) {
			if(gui == null)
				return;

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, MiscUtil.GENERAL_ICONS);

			Screen.blit(matrix, x, y, 0, 0, 126, 13, 256, 256);
		}

		@Override
		public boolean namesMatch(ItemStack stack) {
			return !searchEnabled || namesMatch(stack, text);
		}

		public boolean namesMatch(ItemStack stack, String search) {
			search = ChatFormatting.stripFormatting(search.trim().toLowerCase(Locale.ROOT));
			if(search == null || search.isEmpty())
				return true;

			if(stack.isEmpty())
				return false;

			Item item = stack.getItem();
			ResourceLocation res = Registry.ITEM.getKey(item);
			if(SimilarBlockTypeHandler.isShulkerBox(res)) {
				CompoundTag cmp = ItemNBTHelper.getCompound(stack, "BlockEntityTag", true);
				if(cmp != null) {
					if(!cmp.contains("id")) {
						cmp = cmp.copy();
						cmp.putString("id", "minecraft:shulker_box");
					}

					BlockEntity te = BlockEntity.loadStatic(BlockPos.ZERO, ((BlockItem) item).getBlock().defaultBlockState(), cmp);
					if(te != null) {
						LazyOptional<IItemHandler> handler = te.getCapability(ForgeCapabilities.ITEM_HANDLER, null);
						if(handler.isPresent()) {
							IItemHandler items = handler.orElseGet(EmptyHandler::new);

							for(int i = 0; i < items.getSlots(); i++)
								if(namesMatch(items.getStackInSlot(i), search))
									return true;
						}
					}
				}
			}

			String name = stack.getHoverName().getString();
			name = ChatFormatting.stripFormatting(name.trim().toLowerCase(Locale.ROOT));

			StringMatcher matcher = String::contains;

			if(search.length() >= 3 && search.startsWith("\"") && search.endsWith("\"")) {
				search = search.substring(1, search.length() - 1);
				matcher = String::equals;
			}

			if(search.length() >= 3 && search.startsWith("/") && search.endsWith("/")) {
				search = search.substring(1, search.length() - 1);
				matcher = (s1, s2) -> Pattern.compile(s2).matcher(s1).find();
			}

			if(stack.isEnchanted()) {
				Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
				for(Enchantment e : enchants.keySet())
					if(e != null && matcher.test(e.getFullname(enchants.get(e)).toString().toLowerCase(Locale.ROOT), search))
						return true;
			}

			List<Component> potionNames = new ArrayList<>();
			PotionUtils.addPotionTooltip(stack, potionNames, 1F);
			for(Component s : potionNames) {
				if(matcher.test(ChatFormatting.stripFormatting(s.toString().trim().toLowerCase(Locale.ROOT)), search))
					return true;
			}

			for(Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
				int lvl = entry.getValue();
				Enchantment e = entry.getKey();
				if(e != null && matcher.test(e.getFullname(lvl).toString().toLowerCase(Locale.ROOT), search))
					return true;
			}

			CreativeModeTab tab = item.getItemCategory();
			if(tab != null && matcher.test(tab.getDisplayName().getString().toLowerCase(Locale.ROOT), search))
				return true;

			//		if(search.matches("favou?rites?") && FavoriteItems.isItemFavorited(stack))
			//			return true;

			ResourceLocation itemName = Registry.ITEM.getKey(item);
			@Nullable String modDisplayName = zeta.getModDisplayName(itemName.getNamespace());

			if(modDisplayName != null && matcher.test(modDisplayName.toLowerCase(Locale.ROOT), search))
				return true;

			return matcher.test(name, search);
			//		return ISearchHandler.hasHandler(stack) && ISearchHandler.getHandler(stack).stackMatchesSearchQuery(search, matcher, ChestSearchBar::namesMatch);
		}

		private interface StringMatcher extends BiPredicate<String, String> {}

	}

}
