package vazkii.zeta.event.client;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import vazkii.zeta.event.bus.IZetaLoadEvent;

public interface ZPreTextureStitch extends IZetaLoadEvent {
	TextureAtlas getAtlas();
	boolean addSprite(ResourceLocation sprite);
}
