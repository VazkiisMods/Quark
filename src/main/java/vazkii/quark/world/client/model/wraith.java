/*
package model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * wraith - wiiv
 * Created using Tabula 4.1.1
 */
/*
public class wraith extends ModelBase {
    public ModelRenderer body;
    public ModelRenderer armr;
    public ModelRenderer arml;

    public wraith() {
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
        this.body.render(f5);
        this.arml.render(f5);
        this.armr.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
/*
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
*/