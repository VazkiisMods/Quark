package egg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelWrapped - Undefined
 * Created using Tabula 7.0.0
 */
public class ModelWrapped extends ModelBase {
    public ModelRenderer Body;
    public ModelRenderer Head;
    public ModelRenderer RightLeg;
    public ModelRenderer LeftLeg;
    public ModelRenderer Hips;

    public ModelWrapped() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.LeftLeg = new ModelRenderer(this, 16, 34);
        this.LeftLeg.setRotationPoint(1.9F, 12.0F, 2.0F);
        this.LeftLeg.addBox(-2.0F, 0.0F, -1.9F, 4, 12, 4, 0.0F);
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.Hips = new ModelRenderer(this, 38, 26);
        this.Hips.setRotationPoint(0.0F, 12.0F, 0.0F);
        this.Hips.addBox(-4.0F, -2.0F, -2.0F, 8, 4, 4, 0.0F);
        this.setRotateAngle(Hips, -0.17453292519943295F, 0.0F, 0.0F);
        this.RightLeg = new ModelRenderer(this, 0, 34);
        this.RightLeg.setRotationPoint(-1.9F, 12.0F, 2.0F);
        this.RightLeg.addBox(-2.0F, 0.0F, -1.9F, 4, 12, 4, 0.0F);
        this.Body = new ModelRenderer(this, 0, 16);
        this.Body.setRotationPoint(0.0F, -1.8F, 0.0F);
        this.Body.addBox(-6.5F, 0.0F, -3.0F, 13, 12, 6, 0.0F);
        this.setRotateAngle(Body, 0.17453292519943295F, 0.0F, 0.0F);
        this.Body.addChild(this.Hips);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.LeftLeg.render(f5);
        this.Head.render(f5);
        this.RightLeg.render(f5);
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
