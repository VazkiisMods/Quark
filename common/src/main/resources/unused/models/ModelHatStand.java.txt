package egg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelHatStand - Undefined
 * Created using Tabula 7.0.0
 */
public class ModelHatStand extends ModelBase {
    public ModelRenderer Stand;
    public ModelRenderer Neck;
    public ModelRenderer Head;

    public ModelHatStand() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Stand = new ModelRenderer(this, 0, 0);
        this.Stand.setRotationPoint(0.0F, 22.0F, 0.0F);
        this.Stand.addBox(-5.0F, 0.0F, -5.0F, 10, 2, 10, 0.0F);
        this.Head = new ModelRenderer(this, 0, 12);
        this.Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Head.addBox(-4.0F, -10.0F, -4.0F, 8, 8, 8, 0.0F);
        this.Neck = new ModelRenderer(this, 0, 0);
        this.Neck.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Neck.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
        this.Stand.addChild(this.Head);
        this.Stand.addChild(this.Neck);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.Stand.render(f5);
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
