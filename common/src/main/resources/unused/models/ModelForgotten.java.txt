package egg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelForgotten - Undefined
 * Created using Tabula 7.0.0
 */
public class ModelForgotten extends ModelBase {
    public ModelRenderer Head;
    public ModelRenderer Body;
    public ModelRenderer Robes;
    public ModelRenderer RightArm;
    public ModelRenderer LeftArm;
    public ModelRenderer Nose;

    public ModelForgotten() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.Nose = new ModelRenderer(this, 24, 0);
        this.Nose.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.Nose.addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, 0.0F);
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.setRotationPoint(0.0F, -0.1F, 0.0F);
        this.Head.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, 0.0F);
        this.RightArm = new ModelRenderer(this, 32, 0);
        this.RightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.RightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.setRotateAngle(RightArm, 0.0F, 0.0F, 0.4363323129985824F);
        this.Body = new ModelRenderer(this, 0, 34);
        this.Body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Body.addBox(-4.0F, 0.0F, -3.0F, 8, 24, 6, 0.0F);
        this.LeftArm = new ModelRenderer(this, 48, 0);
        this.LeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.LeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.setRotateAngle(LeftArm, 0.0F, 0.0F, -0.4363323129985824F);
        this.Robes = new ModelRenderer(this, 28, 34);
        this.Robes.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Robes.addBox(-4.0F, 0.0F, -3.0F, 8, 24, 6, 0.5F);
        this.Head.addChild(this.Nose);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.Head.render(f5);
        this.RightArm.render(f5);
        this.Body.render(f5);
        this.LeftArm.render(f5);
        this.Robes.render(f5);
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
