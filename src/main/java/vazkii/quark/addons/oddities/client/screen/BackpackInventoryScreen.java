package vazkii.quark.addons.oddities.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import vazkii.quark.addons.oddities.container.BackpackContainer;
import vazkii.quark.addons.oddities.module.BackpackModule;
import vazkii.quark.base.Quark;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.HandleBackpackMessage;

import java.util.HashMap;
import java.util.Map;

public class BackpackInventoryScreen extends InventoryScreen {
	
	private static final ResourceLocation BACKPACK_INVENTORY_BACKGROUND = new ResourceLocation(Quark.MOD_ID, "textures/misc/backpack_gui.png");
	
	private final PlayerEntity player;
	private final Map<Button, Integer> buttonYs = new HashMap<>();
	
	private boolean closeHack = false;
	private static PlayerContainer oldContainer;
	
	public BackpackInventoryScreen(PlayerContainer backpack, PlayerInventory inventory, ITextComponent component) {
		super(setBackpackContainer(inventory.player, backpack));
		
		this.player = inventory.player;
		setBackpackContainer(player, oldContainer);
	}
	
	public static PlayerEntity setBackpackContainer(PlayerEntity entity, PlayerContainer container) {
		oldContainer = entity.container;
		entity.container = container;
		
		return entity;
	}

	@Override
	public void init() {
		ySize = 224;
		super.init();
		
		for(Widget widget : buttons)
			if(widget instanceof ImageButton || widget.getClass().getName().contains("svenhjol.charm")) {
				widget.y -= 29;
				buttonYs.put((Button) widget, widget.y);
			}
	}

	@Override
	public void tick() {
		super.tick();

		buttonYs.forEach((button, y) -> button.y = y);
		
		if(!BackpackModule.isEntityWearingBackpack(player)) {
			ItemStack curr = player.inventory.getItemStack();
			BackpackContainer.saveCraftingInventory(player);
			closeHack = true;
			QuarkNetwork.sendToServer(new HandleBackpackMessage(false));
			minecraft.displayGuiScreen(new InventoryScreen(player));
			player.inventory.setItemStack(curr);
		}
	}
	
	@Override
	public void onClose() {
		if(closeHack) {
			closeHack = false;
			return;
		}
			
		super.onClose();
	}
	
	@Override 
	protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(BACKPACK_INVENTORY_BACKGROUND);
		int i = guiLeft;
		int j = guiTop;
		blit(stack, i, j, 0, 0, xSize, ySize);
		drawEntityOnScreen(i + 51, j + 75, 30, i + 51 - mouseX, j + 75 - 50 - mouseY, minecraft.player);
		moveCharmsButtons();
	}

	private void moveCharmsButtons() {
		for(Widget widget : buttons) {
			//Charms buttons have a static Y pos, so use that to only focus on them.
			if(widget instanceof ImageButton && widget.y == height / 2 - 22) {
				((ImageButton) widget).setPosition(widget.x, widget.y - 29);
			}
		}
	}
	
}