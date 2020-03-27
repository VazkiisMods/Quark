package vazkii.quark.oddities.client.screen;

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
import vazkii.quark.base.Quark;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.HandleBackpackMessage;
import vazkii.quark.oddities.container.BackpackContainer;
import vazkii.quark.oddities.module.BackpackModule;

public class BackpackInventoryScreen extends InventoryScreen {
	
	private static final ResourceLocation BACKPACK_INVENTORY_BACKGROUND = new ResourceLocation(Quark.MOD_ID, "textures/misc/backpack_gui.png");
	
	private final PlayerEntity player;
	private Button recipeButton;
	private int recipeButtonY;
	
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
	public void init(Minecraft mc, int width, int height) {
		ySize = 224;
		super.init(mc, width, height);
		
		for(Widget widget : buttons)
			if(widget instanceof ImageButton) {
				widget.y -= 29;
				
				recipeButton = (Button) widget;
				recipeButtonY = widget.y;
			}
	}

	@Override
	public void tick() {
		super.tick();
		
		recipeButton.y = recipeButtonY;
		
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
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(BACKPACK_INVENTORY_BACKGROUND);
		int i = guiLeft;
		int j = guiTop;
		blit(i, j, 0, 0, xSize, ySize);
		drawEntityOnScreen(i + 51, j + 75, 30, i + 51 - mouseX, j + 75 - 50 - mouseY, minecraft.player);
	}
	
}