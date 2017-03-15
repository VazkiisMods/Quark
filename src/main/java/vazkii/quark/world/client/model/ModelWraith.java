package vazkii.quark.world.client.model;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelWraith extends ModelBase {
	
    public ModelRenderer body;
    public ModelRenderer armr;
    public ModelRenderer arml;

    public ModelWraith() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, -8.0F, -4.0F, 8, 24, 8, 0.0F);
        this.arml = new ModelRenderer(this, 32, 16);
        this.arml.mirror = true;
        this.arml.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.arml.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.armr = new ModelRenderer(this, 32, 16);
        this.armr.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.armr.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
    	GlStateManager.pushMatrix();
    	Random rng = new Random(entity.getEntityId());
    	float time = entity.ticksExisted + f + rng.nextInt(10000000);
    	
    	GlStateManager.translate(0, Math.sin(time / 16) * 0.1 + 0.15, 0);
    	arml.rotateAngleX = (float) Math.toRadians(-50F + rng.nextFloat() * 20F);
    	armr.rotateAngleX = (float) Math.toRadians(-50F + rng.nextFloat() * 20F);
    	arml.rotateAngleZ = (float) Math.toRadians(-110F + (float) Math.cos(time / (8 + rng.nextInt(2))) * (8F + rng.nextFloat() * 8F));
    	armr.rotateAngleZ = (float) Math.toRadians(110F + (float) Math.cos((time + 300) / (8 + rng.nextInt(2))) * (8F + rng.nextFloat() * 8F));
    	
    	GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		float r = 1F;
		float g = 1F;
		float b = 1F;
		float mute = 0.5F;
		if(rng.nextInt(80) == 0)
			switch(rng.nextInt(6)) {
			case 0:
				g = mute;
				b = mute;
				break;
			case 1:
				r = mute;
				b = mute;
				break;
			case 2:
				r = mute;
				g = mute;
				break;
			case 3:
				r = mute;
				break;
			case 4:
				g = mute;
				break;
			case 5:
				b = mute;
				break;
			}
		
		GlStateManager.color(r, g, b, 0.7F + (float) Math.sin(time / 20)  * 0.3F); 
    	
        body.render(f5);
        arml.render(f5);
        armr.render(f5);
        GlStateManager.popMatrix();
    }
    
}

