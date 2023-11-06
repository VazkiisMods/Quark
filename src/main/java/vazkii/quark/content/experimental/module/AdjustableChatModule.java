package vazkii.quark.content.experimental.module;

import vazkii.quark.base.module.config.Config;
import vazkii.zeta.client.event.ZRenderGuiOverlay;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "experimental", enabledByDefault = false)
public class AdjustableChatModule extends ZetaModule {

	@Config public static int horizontalShift = 0;
	@Config public static int verticalShift = 0;

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends AdjustableChatModule {

		@PlayEvent
		public void pre(ZRenderGuiOverlay.ChatPanel.Pre event) {
			event.getPoseStack().translate(horizontalShift, verticalShift, 0);
		}

		@PlayEvent
		public void post(ZRenderGuiOverlay.ChatPanel.Post event) {
			event.getPoseStack().translate(-horizontalShift, -verticalShift, 0);
		}
	}
	
}
