package org.violetmoon.quark.content.tweaks.client.emote;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.violetmoon.quark.content.tweaks.module.EmotesModule;

@OnlyIn(Dist.CLIENT)
public class CustomEmoteDescriptor extends EmoteDescriptor {

	public CustomEmoteDescriptor(String name, String regName, int index) {
		super(CustomEmote.class, name, regName, index, getSprite(name), new CustomEmoteTemplate(name));
	}
	
	public static ResourceLocation getSprite(String name) {
		ResourceLocation customRes = new ResourceLocation(EmoteHandler.CUSTOM_EMOTE_NAMESPACE, name);
		if(EmotesModule.Client.resourcePack.hasResource(PackType.CLIENT_RESOURCES, customRes))
			return customRes;
		
		return new ResourceLocation("quark", "textures/emote/custom.png");
	}
	
	@Override
	public String getTranslationKey() {
		return ((CustomEmoteTemplate) template).getName();
	}
	
	@Override
	public String getLocalizedName() {
		return getTranslationKey();
	}

}
