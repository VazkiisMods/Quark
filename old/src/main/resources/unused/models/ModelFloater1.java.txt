package toilet humor;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelFloater1 - Undefined
 * Created using Tabula 7.1.0
 */
public class ModelFloater1 extends ModelBase {
    public ModelRenderer Body;
    public ModelRenderer RightEye;
    public ModelRenderer LeftEye;
    public ModelRenderer Wiggle1;
    public ModelRenderer Wiggle2;
    public ModelRenderer Wiggle3;
    public ModelRenderer Wiggle4;
    public ModelRenderer Wiggle5;
    public ModelRenderer Wiggle6;

    public ModelFloater1() {
        this.textureWidth = 80;
        this.textureHeight = 27;
        this.LeftEye = new ModelRenderer(this, 0, 0);
        this.LeftEye.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.LeftEye.addBox(3.0F, -3.5F, -10.0F, 2, 2, 2, 0.0F);
        this.Body = new ModelRenderer(this, 0, 0);
        this.Body.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.Body.addBox(-8.0F, -3.0F, -12.0F, 16, 3, 24, 0.0F);
        this.Wiggle6 = new ModelRenderer(this, 0, 0);
        this.Wiggle6.mirror = true;
        this.Wiggle6.setRotationPoint(8.0F, -2.0F, 4.0F);
        this.Wiggle6.addBox(0.0F, 0.0F, 0.0F, 8, 0, 8, 0.0F);
        this.setRotateAngle(Wiggle6, 0.0F, -0.2181661564992912F, -0.1090830782496456F);
        this.Wiggle2 = new ModelRenderer(this, 0, 0);
        this.Wiggle2.setRotationPoint(-8.0F, -2.0F, 0.0F);
        this.Wiggle2.addBox(-8.0F, 0.0F, -4.0F, 8, 0, 8, 0.0F);
        this.setRotateAngle(Wiggle2, 0.0F, 0.0F, 0.2181661564992912F);
        this.Wiggle1 = new ModelRenderer(this, 0, 0);
        this.Wiggle1.setRotationPoint(-8.0F, -2.0F, -4.0F);
        this.Wiggle1.addBox(-8.0F, 0.0F, -8.0F, 8, 0, 8, 0.0F);
        this.setRotateAngle(Wiggle1, 0.0F, -0.2181661564992912F, 0.1090830782496456F);
        this.Wiggle4 = new ModelRenderer(this, 0, 0);
        this.Wiggle4.mirror = true;
        this.Wiggle4.setRotationPoint(8.0F, -2.0F, -4.0F);
        this.Wiggle4.addBox(0.0F, 0.0F, -8.0F, 8, 0, 8, 0.0F);
        this.setRotateAngle(Wiggle4, 0.0F, 0.2181661564992912F, -0.1090830782496456F);
        this.RightEye = new ModelRenderer(this, 0, 0);
        this.RightEye.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.RightEye.addBox(-5.0F, -3.5F, -11.0F, 2, 2, 2, 0.0F);
        this.Wiggle3 = new ModelRenderer(this, 0, 0);
        this.Wiggle3.setRotationPoint(-8.0F, -2.0F, 4.0F);
        this.Wiggle3.addBox(-8.0F, 0.0F, 0.0F, 8, 0, 8, 0.0F);
        this.setRotateAngle(Wiggle3, 0.0F, 0.2181661564992912F, 0.1090830782496456F);
        this.Wiggle5 = new ModelRenderer(this, 0, 0);
        this.Wiggle5.mirror = true;
        this.Wiggle5.setRotationPoint(8.0F, -2.0F, 0.0F);
        this.Wiggle5.addBox(0.0F, 0.0F, -4.0F, 8, 0, 8, 0.0F);
        this.setRotateAngle(Wiggle5, 0.0F, 0.0F, -0.2181661564992912F);
        this.Body.addChild(this.LeftEye);
        this.Body.addChild(this.Wiggle6);
        this.Body.addChild(this.Wiggle2);
        this.Body.addChild(this.Wiggle1);
        this.Body.addChild(this.Wiggle4);
        this.Body.addChild(this.RightEye);
        this.Body.addChild(this.Wiggle3);
        this.Body.addChild(this.Wiggle5);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.Body.render(f5);
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
