package org.violetmoon.zetaimplforge.client.event.load;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import org.violetmoon.zeta.client.event.load.ZPreTextureStitch;

public record ForgeZPreTextureStitch(TextureStitchEvent.Pre e) implements ZPreTextureStitch {
	@Override
	public TextureAtlas getAtlas() {
		return e.getAtlas();
	}

	@Override
	public boolean addSprite(ResourceLocation sprite) {
		return e.addSprite(sprite);
	}
}
