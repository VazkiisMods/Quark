package vazkii.quark.content.client.module;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.zeta.client.event.ZRenderGuiOverlay;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "client")
public class ElytraIndicatorModule extends ZetaModule {

	public int getArmorLimit(int curr) {
		return curr;
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends ElytraIndicatorModule {

		private int shift = 0;

		@PlayEvent
		public void hudPre(ZRenderGuiOverlay.ArmorLevel.Pre event) {
			if(!event.shouldDrawSurvivalElements())
				return;

			Minecraft mc = Minecraft.getInstance();
			Player player = mc.player;
			ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);

			if(zeta.canElytraFly(itemstack, player)) {
				int armor = player.getArmorValue();
				shift = (armor >= 20 ? 0 : 9);

				PoseStack pose = event.getPoseStack();
				Window window = event.getWindow();

				pose.translate(shift, 0, 0);

				pose.pushPose();
				pose.translate(0, 0, 100);
				RenderSystem.setShaderTexture(0, MiscUtil.GENERAL_ICONS);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

				int x = window.getGuiScaledWidth() / 2 - 100;
				int y = window.getGuiScaledHeight() - event.getLeftHeight();
				Screen.blit(pose, x, y, 184, 35, 9, 9, 256, 256);

				pose.popPose();
			}
		}

		@PlayEvent
		public void hudPost(ZRenderGuiOverlay.ArmorLevel.Post event) {
			if(shift != 0) {
				event.getPoseStack().translate(-shift, 0, 0);
				shift = 0;
			}
		}

		@Override
		public int getArmorLimit(int curr) {
			if(!enabled)
				return curr;

			return 20 - ((shift / 9) * 2);
		}

	}
	
}
