package vazkii.zetaimplforge.event.client;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import vazkii.zeta.event.bus.FiredAs;
import vazkii.zeta.event.client.ZPreTextureStitch;

@FiredAs(ZPreTextureStitch.class)
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
