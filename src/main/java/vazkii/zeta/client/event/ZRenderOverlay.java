package vazkii.zeta.client.event;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZRenderOverlay extends IZetaPlayEvent {
	Window getWindow();
	PoseStack getPoseStack();
	float getPartialTick();

	boolean shouldDrawSurvivalElements();
	int getLeftHeight(); //weird ForgeGui stuff

	interface ArmorLevel extends ZRenderOverlay {
		interface Pre extends Chat { }
		interface Post extends Chat { }
	}
	interface Chat extends ZRenderOverlay {
		interface Pre extends Chat { }
		interface Post extends Chat { }
	}
	interface Crosshair extends ZRenderOverlay {
		interface Pre extends Crosshair { }
		interface Post extends Crosshair { }
	}
	interface Hotbar extends ZRenderOverlay {
		interface Pre extends Chat { }
		interface Post extends Chat { }
	}
}
