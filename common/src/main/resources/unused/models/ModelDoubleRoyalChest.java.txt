package egg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelDoubleRoyalChest - Undefined
 * Created using Tabula 7.0.0
 */
public class ModelDoubleRoyalChest extends ModelBase {
    public ModelRenderer Body;
    public ModelRenderer Lid;
    public ModelRenderer Lock;

    public ModelDoubleRoyalChest() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.Lock = new ModelRenderer(this, 0, 43);
        this.Lock.setRotationPoint(0.0F, -2.0F, -14.0F);
        this.Lock.addBox(-3.0F, -1.0F, -2.0F, 6, 6, 2, 0.0F);
        this.Lid = new ModelRenderer(this, 0, 0);
        this.Lid.setRotationPoint(0.0F, -9.0F, 7.0F);
        this.Lid.addBox(-15.0F, -5.0F, -14.0F, 30, 5, 14, 0.0F);
        this.Body = new ModelRenderer(this, 0, 19);
        this.Body.setRotationPoint(8.0F, 24.0F, 0.0F);
        this.Body.addBox(-15.0F, -10.0F, -7.0F, 30, 10, 14, 0.0F);
        this.Lid.addChild(this.Lock);
        this.Body.addChild(this.Lid);
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
