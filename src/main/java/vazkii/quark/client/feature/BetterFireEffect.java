package vazkii.quark.client.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class BetterFireEffect extends Feature {
	
	static boolean enableParticles, enableDifferentRender;
	
	@Override
	public void setupConfig() {
		enableParticles = loadPropBool("Enable Particles", "", true);
		enableDifferentRender = loadPropBool("Enable Different Render", "", true);
	}

	public static boolean renderFire(Entity entity, double x, double y, double z, float pticks) {
		if(!ModuleLoader.isFeatureEnabled(BetterFireEffect.class) || !enableDifferentRender)
			return false;

		GlStateManager.disableLighting();
		TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
		TextureAtlasSprite textureatlassprite = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_0");
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y, (float)z);

		float f = Math.min(entity.width, entity.height) * 1.8F;
		GlStateManager.scale(f, f, f);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		float f1 = 0.5F;
		float f2 = 0.0F;
		float f4 = (float)(entity.posY - entity.getEntityBoundingBox().minY);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f5 = 0.0F;
		float f6 = textureatlassprite.getMaxU();
		float f7 = textureatlassprite.getMinV();
		float f8 = textureatlassprite.getMinU();
		float f9 = textureatlassprite.getMaxV();

		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		int itrs = 8;
		float rot = (360F / itrs);
		for(int i = 0; i < itrs; i++) {
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos((double)(f1 - 0.0F), (double)(0.0F - f4), (double)f5).tex((double)f8, (double)f9).endVertex();
			bufferbuilder.pos((double)(-f1 - 0.0F), (double)(0.0F - f4), (double)f5).tex((double)f6, (double)f9).endVertex();
			bufferbuilder.pos((double)(-f1 - 0.0F), (double)(1.4F - f4), (double)f5).tex((double)f6, (double)f7).endVertex();
			bufferbuilder.pos((double)(f1 - 0.0F), (double)(1.4F - f4), (double)f5).tex((double)f8, (double)f7).endVertex();
			tessellator.draw();
			GlStateManager.rotate(rot, 0F, 1F, 0F);
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();

		return true;
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(!enableParticles)
			return;
		
		final int delay = 1;
		final int count = 2;

		Minecraft mc = Minecraft.getMinecraft();
		if(event.phase == Phase.END && mc.world != null && ClientTicker.ticksInGame % delay == 0 && (mc.currentScreen == null || !mc.currentScreen.doesGuiPauseGame())) {
			for(Entity e : mc.world.loadedEntityList)
				if(e.canRenderOnFire()) {
					double w = e.width;
					double h = e.height;
					for(int i = 0; i < count; i++) {
						double x = e.posX + Math.random() * w * 1.2 - w / 2;
						double y = e.posY + Math.random() * h;
						double z = e.posZ + Math.random() * w * 1.2 - w / 2;
						e.world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
					}
				}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

}
