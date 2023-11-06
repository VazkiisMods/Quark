package vazkii.zeta.client.event;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZRenderGuiOverlay extends IZetaPlayEvent {
	Window getWindow();
	PoseStack getPoseStack();
	float getPartialTick();

	boolean shouldDrawSurvivalElements();
	int getLeftHeight(); //weird ForgeGui stuff

	interface Hotbar extends ZRenderGuiOverlay {
		interface Pre extends Hotbar { }
		interface Post extends Hotbar { }
	}

	interface Crosshair extends ZRenderGuiOverlay {
		interface Pre extends Crosshair { }
		interface Post extends Crosshair { }
	}

	interface PlayerHealth extends ZRenderGuiOverlay {
		interface Pre extends PlayerHealth { }
		interface Post extends PlayerHealth { }
	}

	interface ArmorLevel extends ZRenderGuiOverlay {
		interface Pre extends ArmorLevel { }
		interface Post extends ArmorLevel { }
	}

	interface DebugText extends ZRenderGuiOverlay {
		interface Pre extends DebugText { }
		interface Post extends DebugText { }
	}

	interface PotionIcons extends ZRenderGuiOverlay {
		interface Pre extends PotionIcons { }
		interface Post extends PotionIcons { }
	}

	interface ChatPanel extends ZRenderGuiOverlay {
		interface Pre extends ChatPanel { }
		interface Post extends ChatPanel { }
	}
}
