package vazkii.quark.client.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardCharTypedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.api.IQuarkButtonIgnored;
import vazkii.quark.base.client.InventoryButtonHandler;
import vazkii.quark.base.client.InventoryButtonHandler.ButtonTargetType;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.handler.InventoryTransferHandler;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.SimilarBlockTypeHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.management.client.gui.MiniInventoryButton;

@LoadModule(category = ModuleCategory.CLIENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class ChestSearchingModule extends Module {

	@OnlyIn(Dist.CLIENT) 
	private static TextFieldWidget searchBar;
	
	private static String text = "";
	private static boolean searchEnabled = false;
	private static boolean skip;
	private static long lastClick;
	private static int matched;

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		InventoryButtonHandler.addButtonProvider(this, ButtonTargetType.CONTAINER_INVENTORY, 1, (parent, x, y) -> 
		new MiniInventoryButton(parent, 3, x, y, "quark.gui.button.filter", (b) -> {
			searchEnabled = !searchEnabled;
			updateSearchStatus();
		}).setTextureShift(() -> searchEnabled));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		Screen gui = event.getGui();
		if(gui instanceof ContainerScreen && !(event.getGui() instanceof IQuarkButtonIgnored) && !GeneralConfig.ignoredScreens.contains(event.getGui().getClass().getName())) {
			Minecraft mc = gui.getMinecraft();
			ContainerScreen<?> chest = (ContainerScreen<?>) gui;
			if(InventoryTransferHandler.accepts(chest.getContainer(), mc.player)) {
				searchBar = new TextFieldWidget(mc.fontRenderer, chest.getGuiLeft() + 18, chest.getGuiTop() + 6, 117, 10, text);

				searchBar.setText(text);
				searchBar.setMaxStringLength(50);
				searchBar.setEnableBackgroundDrawing(false);
				updateSearchStatus();

				return;
			}
		} 

		searchBar = null;
	}

	private void updateSearchStatus() {
		searchBar.setEnabled(searchEnabled);
		searchBar.setVisible(searchEnabled);
		searchBar.setFocused2(searchEnabled);
	}

	@SubscribeEvent
	public void charTyped(KeyboardCharTypedEvent.Pre event) {
		if(searchBar != null && searchBar.isFocused() && searchEnabled) {
			searchBar.charTyped(event.getCodePoint(), event.getModifiers());
			text = searchBar.getText();

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onKeypress(KeyboardKeyPressedEvent.Pre event) {
		if(searchBar != null && searchBar.isFocused() && searchEnabled) {
			searchBar.keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers());
			text = searchBar.getText();

			event.setCanceled(event.getKeyCode() != 256); // 256 = escape
		}
	}

	@SubscribeEvent
	public void onClick(MouseClickedEvent.Pre event) {
		if(searchBar != null && searchEnabled) {
			searchBar.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton());

			long time = System.currentTimeMillis();
			long delta = time - lastClick;
			if(delta < 200 && searchBar.isFocused()) {
				searchBar.setText("");
				text = "";
			}

			lastClick = time;
		}
	}

	@SubscribeEvent
	public void onRender(GuiScreenEvent.DrawScreenEvent.Post event) {
		if(searchBar != null && !skip && searchEnabled)
			renderElements(event.getGui());
		skip = false;
	}

	@SubscribeEvent
	public void drawTooltipEvent(RenderTooltipEvent.Pre event) {
		if(searchBar != null && searchEnabled) {
			renderElements(Minecraft.getInstance().currentScreen);
			skip = true;
		}
	}

	private void renderElements(Screen gui) {
		RenderSystem.pushMatrix();
		RenderSystem.translated(0, 0, 500);
		drawBackground(gui, searchBar.x - 11, searchBar.y - 3);

		if(!text.isEmpty()) {
			if(gui instanceof ContainerScreen) {
				ContainerScreen<?> guiContainer = (ContainerScreen<?>) gui;
				Container container = guiContainer.getContainer();

				int guiLeft = guiContainer.getGuiLeft();
				int guiTop = guiContainer.getGuiTop();

				matched = 0;
				for(Slot s : container.inventorySlots) {
					ItemStack stack = s.getStack();
					if(!namesMatch(stack, text)) {
						int x = guiLeft + s.xPos;
						int y = guiTop + s.yPos;

						RenderSystem.disableDepthTest();
						Screen.fill(x, y, x + 16, y + 16, 0xAA000000);
					} else matched++;
				}
			}
		}

		if(matched == 0 && !text.isEmpty())
			searchBar.setTextColor(0xFF5555);
		else searchBar.setTextColor(0xFFFFFF);

		searchBar.render(0, 0, 0);
		RenderSystem.popMatrix();
	}

	private void drawBackground(Screen gui, int x, int y) {
		if(gui == null)
			return;

		RenderSystem.color4f(1F, 1F, 1F, 1F);
		RenderSystem.disableLighting();
		Minecraft.getInstance().getTextureManager().bindTexture(MiscUtil.GENERAL_ICONS);
		Screen.blit(x, y, 0, 0, 126, 13, 256, 256);
	}

	public static boolean namesMatch(ItemStack stack) {
		return !searchEnabled || namesMatch(stack, text);
	}

	public static boolean namesMatch(ItemStack stack, String search) {
		search = TextFormatting.getTextWithoutFormattingCodes(search.trim().toLowerCase());
		if(search == null || search.isEmpty())
			return true;

		if(stack.isEmpty())
			return false;

		Item item = stack.getItem();
		ResourceLocation res = item.getRegistryName();
		if(SimilarBlockTypeHandler.isShulkerBox(res)) {
			CompoundNBT cmp = ItemNBTHelper.getCompound(stack, "BlockEntityTag", true);
			if (cmp != null) {
				if (!cmp.contains("id", Constants.NBT.TAG_STRING)) {
					cmp = cmp.copy();
					cmp.putString("id", "minecraft:shulker_box");
				}
				TileEntity te = TileEntity.create(cmp);
				if (te != null) {
					LazyOptional<IItemHandler> handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
					if (handler.isPresent()) {
						IItemHandler items = handler.orElseGet(EmptyHandler::new);

						for (int i = 0; i < items.getSlots(); i++)
							if (namesMatch(items.getStackInSlot(i), search))
								return true;
					}
				}
			}
		}

		String name = stack.getDisplayName().getUnformattedComponentText();
		name = TextFormatting.getTextWithoutFormattingCodes(name.trim().toLowerCase());

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
				if(e != null && matcher.test(e.getDisplayName(enchants.get(e)).toString().toLowerCase(), search))
					return true;
		}

		List<ITextComponent> potionNames = new ArrayList<>();
		PotionUtils.addPotionTooltip(stack, potionNames, 1F);
		for(ITextComponent s : potionNames) {
			if (matcher.test(TextFormatting.getTextWithoutFormattingCodes(s.toString().trim().toLowerCase()), search))
				return true;
		}




		for(Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
			int lvl = entry.getValue();
			Enchantment e = entry.getKey();
			if(e != null && matcher.test(e.getDisplayName(lvl).toString().toLowerCase(), search))
				return true;
		}

		ItemGroup tab = item.getGroup();
		if(tab != null && matcher.test(I18n.format(tab.getTranslationKey()).toLowerCase(), search))
			return true;

		//		if(search.matches("favou?rites?") && FavoriteItems.isItemFavorited(stack))
		//			return true;

		ResourceLocation itemName = item.getRegistryName();
		Optional<? extends ModContainer> mod = ModList.get().getModContainerById(itemName.getPath());
		if(mod.isPresent() && matcher.test(mod.orElse(null).getModInfo().getDisplayName().toLowerCase(), search))
			return true;

		if(matcher.test(name, search))
			return true;

		return false;
		//		return ISearchHandler.hasHandler(stack) && ISearchHandler.getHandler(stack).stackMatchesSearchQuery(search, matcher, ChestSearchBar::namesMatch);
	}

	private interface StringMatcher extends BiPredicate<String, String> { }

}
