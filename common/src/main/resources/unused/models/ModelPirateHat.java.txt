package egg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelPirateHat - Undefined
 * Created using Tabula 7.0.0
 */
public class ModelPirateHat extends ModelBase {
    public ModelRenderer Hat1;
    public ModelRenderer Hat2;
    public ModelRenderer Hat3;
    public ModelRenderer Hat4;

    public ModelPirateHat() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Hat2 = new ModelRenderer(this, 41, 17);
        this.Hat2.setRotationPoint(-7.1F, -9.5F, 0.0F);
        this.Hat2.addBox(-1.0F, -3.0F, -4.0F, 2, 4, 8, 0.0F);
        this.setRotateAngle(Hat2, 0.0F, 0.0F, -0.2617993877991494F);
        this.Hat1 = new ModelRenderer(this, 0, 20);
        this.Hat1.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.Hat1.addBox(-8.0F, -2.0F, -4.5F, 16, 3, 9, 0.0F);
        this.Hat3 = new ModelRenderer(this, 41, 17);
        this.Hat3.mirror = true;
        this.Hat3.setRotationPoint(7.1F, -9.5F, 0.0F);
        this.Hat3.addBox(-1.0F, -3.0F, -4.0F, 2, 4, 8, 0.0F);
        this.setRotateAngle(Hat3, 0.0F, 0.0F, 0.2617993877991494F);
        this.Hat4 = new ModelRenderer(this, 0, 0);
        this.Hat4.setRotationPoint(0.0F, -8.0F, 4.5F);
        this.Hat4.addBox(-4.5F, -5.0F, -9.0F, 9, 5, 9, 0.0F);
        this.setRotateAngle(Hat4, -0.08726646259971647F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.Hat2.render(f5);
        this.Hat1.render(f5);
        this.Hat3.render(f5);
        this.Hat4.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
