package org.violetmoon.quark.content.management.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.violetmoon.quark.addons.oddities.module.BackpackModule;
import org.violetmoon.quark.api.IQuarkButtonAllowed;
import org.violetmoon.quark.content.management.inventory.HeldShulkerBoxMenu;

public class HeldShulkerBoxScreen extends AbstractContainerScreen<HeldShulkerBoxMenu> implements IQuarkButtonAllowed {

	private static final ResourceLocation CONTAINER_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");

	public HeldShulkerBoxScreen(HeldShulkerBoxMenu p_99240_, Inventory p_99241_, Component p_99242_) {
		super(p_99240_, p_99241_, p_99242_);
	}

	@Override
	public void render(PoseStack p_99249_, int p_99250_, int p_99251_, float p_99252_) {
		this.renderBackground(p_99249_);
		super.render(p_99249_, p_99250_, p_99251_, p_99252_);
		this.renderTooltip(p_99249_, p_99250_, p_99251_);
	}

	@Override
	protected void renderBg(PoseStack p_99244_, float p_99245_, int p_99246_, int p_99247_) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, CONTAINER_TEXTURE);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(p_99244_, i, j, 0, 0, this.imageWidth, this.imageHeight);

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if(player != null) {
			int s = menu.blockedSlot;
			ItemStack stack = player.getInventory().getItem(s);

			int x = getGuiLeft() + (8 + (s % 9) * 18);
			int y = getGuiTop() + (s < 9 ? 142 : 84 + ((s - 9) / 9) * 18);

			mc.getItemRenderer().renderGuiItem(stack, x, y);

			fill(p_99244_, x, y, x + 16, y + 16, 0x88000000);
		}
	}

	@Override
	public void onClose() {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if(player != null) {
			double mx = mc.mouseHandler.xpos();
			double my = mc.mouseHandler.ypos();
			
			player.playSound(SoundEvents.SHULKER_BOX_CLOSE, 1F, 1F);
			
			if(player.getItemBySlot(EquipmentSlot.CHEST).is(BackpackModule.backpack))
				BackpackModule.requestBackpack();
			else {
				player.closeContainer();
				mc.setScreen(new InventoryScreen(player));
			}
			
			GLFW.glfwSetCursorPos(mc.getWindow().getWindow(), mx, my);
		}
	}

}
