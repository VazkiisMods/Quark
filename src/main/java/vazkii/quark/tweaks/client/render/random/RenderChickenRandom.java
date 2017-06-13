package vazkii.quark.tweaks.client.render.random;

import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.tweaks.feature.RandomAnimalTextures;
import vazkii.quark.tweaks.feature.RandomAnimalTextures.RandomTextureType;

public class RenderChickenRandom extends RenderChicken {

	public RenderChickenRandom(RenderManager manager) {
		super(manager);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityChicken entity) {
		if(RandomAnimalTextures.enableChick && entity.isChild())
			return RandomAnimalTextures.getRandomTexture(entity, RandomTextureType.CHICK);
		
		return RandomAnimalTextures.getRandomTexture(entity, RandomTextureType.CHICKEN);
	}
	
	public static IRenderFactory factory() {
		return manager -> new RenderChickenRandom(manager);
	}


}
