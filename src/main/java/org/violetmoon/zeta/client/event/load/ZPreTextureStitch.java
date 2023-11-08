package org.violetmoon.zeta.client.event.load;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

public interface ZPreTextureStitch extends IZetaLoadEvent {
	TextureAtlas getAtlas();
	boolean addSprite(ResourceLocation sprite);
}
