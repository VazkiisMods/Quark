package vazkii.quark.content.experimental.module;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = "experimental", enabledByDefault = false, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class AdjustableChatModule extends QuarkModule {

	@Config public static int horizontalShift = 0;
	@Config public static int verticalShift = 0;
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void pre(RenderGuiOverlayEvent.Pre event) {
		if(event.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type())
			event.getPoseStack().translate(horizontalShift, verticalShift, 0);
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void post(RenderGuiOverlayEvent.Post event) {
		if(event.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type())
			event.getPoseStack().translate(-horizontalShift, -verticalShift, 0);
	}
	
}
