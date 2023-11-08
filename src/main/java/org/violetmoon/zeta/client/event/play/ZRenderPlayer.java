package org.violetmoon.zeta.client.event.play;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

//TODO: most of this is not used in quark, just patreon renderer
public interface ZRenderPlayer extends IZetaPlayEvent {
	PlayerRenderer getRenderer();
	float getPartialTick();
	PoseStack getPoseStack();
	MultiBufferSource getMultiBufferSource();
	int getPackedLight();
	Player getEntity();

	interface Pre extends ZRenderPlayer { }
	interface Post extends ZRenderPlayer { }
}
