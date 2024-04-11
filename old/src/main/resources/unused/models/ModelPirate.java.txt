package egg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelPirate - Undefined
 * Created using Tabula 7.0.0
 */
public class ModelPirate extends ModelBase {
    public ModelRenderer Head;
    public ModelRenderer HatLayer;
    public ModelRenderer Torso;
    public ModelRenderer RightArm;
    public ModelRenderer LeftArm;
    public ModelRenderer RightLeg;
    public ModelRenderer LeftLeg;
    public ModelRenderer TorsoLayer;
    public ModelRenderer RightArmLayer;
    public ModelRenderer LeftArmLayer;
    public ModelRenderer RightLegLayer;
    public ModelRenderer LeftLegLayer;

    public ModelPirate() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.HatLayer = new ModelRenderer(this, 32, 0);
        this.HatLayer.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.HatLayer.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
        this.RightArmLayer = new ModelRenderer(this, 40, 48);
        this.RightArmLayer.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.RightArmLayer.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F);
        this.Torso = new ModelRenderer(this, 16, 16);
        this.Torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.RightLegLayer = new ModelRenderer(this, 0, 48);
        this.RightLegLayer.setRotationPoint(-2.0F, 12.0F, 0.1F);
        this.RightLegLayer.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F);
        this.LeftLeg = new ModelRenderer(this, 0, 16);
        this.LeftLeg.mirror = true;
        this.LeftLeg.setRotationPoint(2.0F, 12.0F, 0.1F);
        this.LeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F);
        this.LeftArmLayer = new ModelRenderer(this, 40, 48);
        this.LeftArmLayer.mirror = true;
        this.LeftArmLayer.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.LeftArmLayer.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F);
        this.LeftLegLayer = new ModelRenderer(this, 0, 48);
        this.LeftLegLayer.mirror = true;
        this.LeftLegLayer.setRotationPoint(2.0F, 12.0F, 0.1F);
        this.LeftLegLayer.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F);
        this.RightLeg = new ModelRenderer(this, 0, 16);
        this.RightLeg.setRotationPoint(-2.0F, 12.0F, 0.1F);
        this.RightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F);
        this.TorsoLayer = new ModelRenderer(this, 16, 48);
        this.TorsoLayer.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TorsoLayer.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F);
        this.RightArm = new ModelRenderer(this, 40, 16);
        this.RightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.RightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0.0F);
        this.LeftArm = new ModelRenderer(this, 40, 16);
        this.LeftArm.mirror = true;
        this.LeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.LeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.Head.render(f5);
        this.HatLayer.render(f5);
        this.RightArmLayer.render(f5);
        this.Torso.render(f5);
        this.RightLegLayer.render(f5);
        this.LeftLeg.render(f5);
        this.LeftArmLayer.render(f5);
        this.LeftLegLayer.render(f5);
        this.RightLeg.render(f5);
        this.TorsoLayer.render(f5);
        this.RightArm.render(f5);
        this.LeftArm.render(f5);
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
