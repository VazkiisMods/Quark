package vazkii.quark.content.experimental.module;

import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.event.client.ZRenderChat;
import vazkii.zeta.module.ModuleSide;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "experimental", enabledByDefault = false, side = ModuleSide.CLIENT_ONLY)
public class AdjustableChatModule extends ZetaModule {

	@Config public static int horizontalShift = 0;
	@Config public static int verticalShift = 0;
	
	@PlayEvent
	public void pre(ZRenderChat.Pre event) {
		event.getPoseStack().translate(horizontalShift, verticalShift, 0);
	}
	
	@PlayEvent
	public void post(ZRenderChat.Post event) {
		event.getPoseStack().translate(-horizontalShift, -verticalShift, 0);
	}
	
}
