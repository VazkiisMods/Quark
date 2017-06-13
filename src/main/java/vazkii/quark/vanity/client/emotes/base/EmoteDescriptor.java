package vazkii.quark.vanity.client.emotes.base;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class EmoteDescriptor {

	public final Class<? extends EmoteBase> clazz;
	public final int index;
	public final String name;
	public final ResourceLocation texture;
	
	public EmoteDescriptor(Class<? extends EmoteBase> clazz, String name, int index) {
		this.clazz = clazz;
		this.index = index;
		this.name = name;
		texture = new ResourceLocation("quark", "textures/emotes/" + name + ".png");
	}
	
	public String getUnlocalizedName() {
		return "quark.emote." + name;
	}
	
	public String getCommand() {
		return "/emote " + name;
	}
	
	public EmoteBase instantiate(EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegModel) {
		try {
			return clazz.getConstructor(EmoteDescriptor.class, EntityPlayer.class, ModelBiped.class, ModelBiped.class, ModelBiped.class).newInstance(this, player, model, armorModel, armorLegModel);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
