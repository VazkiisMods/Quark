package vazkii.quark.content.client.module;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "client", hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class ElytraIndicatorModule extends QuarkModule {

	private static int shift = 0;
	private static boolean staticEnabled;
	
	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		staticEnabled = enabled;
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void hudPre(RenderGuiOverlayEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		
		if(event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type() && mc.gui instanceof ForgeGui fg && fg.shouldDrawSurvivalElements()) {
	         ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);
	         
	         if(itemstack.canElytraFly(player)) {
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
				int y = window.getGuiScaledHeight() - fg.leftHeight;
				Screen.blit(pose, x, y, 184, 35, 9, 9, 256, 256);
				
				pose.popPose();
	         }
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void hudPost(RenderGuiOverlayEvent.Post event) {
		if(shift != 0) {
			event.getPoseStack().translate(-shift, 0, 0);
			shift = 0;
		}
	}
	
	public static int getArmorLimit(int curr) {
		if(!staticEnabled)
			return curr;
		
		return 20 - ((shift / 9) * 2);
	}
	
}
