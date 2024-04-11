package eggy;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSnom - Undefined
 * Created using Tabula 7.1.0
 */
public class ModelSnom extends ModelBase {
    public ModelRenderer Head;
    public ModelRenderer Shell;
    public ModelRenderer Body;
    public ModelRenderer RightMouth;
    public ModelRenderer LeftMouth;
    public ModelRenderer Spikes1;
    public ModelRenderer Spikes2;
    public ModelRenderer Tail;

    public ModelSnom() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.Head = new ModelRenderer(this, 0, 28);
        this.Head.setRotationPoint(0.0F, 21.0F, 0.0F);
        this.Head.addBox(-4.0F, -3.1F, -6.0F, 8, 6, 6, 0.0F);
        this.LeftMouth = new ModelRenderer(this, 10, 12);
        this.LeftMouth.setRotationPoint(1.5F, 1.5F, -6.5F);
        this.LeftMouth.addBox(-1.0F, -1.6F, -1.0F, 2, 3, 3, 0.0F);
        this.setRotateAngle(LeftMouth, 0.20943951023931953F, 0.7853981633974483F, 0.0F);
        this.Spikes1 = new ModelRenderer(this, 32, 0);
        this.Spikes1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Spikes1.addBox(-7.0F, -6.5F, 1.0F, 14, 9, 2, 0.0F);
        this.Shell = new ModelRenderer(this, 30, 26);
        this.Shell.setRotationPoint(0.0F, 21.5F, -1.0F);
        this.Shell.addBox(-5.0F, -4.5F, 0.0F, 10, 7, 7, 0.0F);
        this.RightMouth = new ModelRenderer(this, 0, 12);
        this.RightMouth.setRotationPoint(-1.5F, 1.5F, -6.5F);
        this.RightMouth.addBox(-1.0F, -1.6F, -1.0F, 2, 3, 3, 0.0F);
        this.setRotateAngle(RightMouth, 0.20943951023931953F, -0.7853981633974483F, 0.0F);
        this.Spikes2 = new ModelRenderer(this, 0, 0);
        this.Spikes2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Spikes2.addBox(-7.0F, -6.5F, 4.0F, 14, 9, 2, 0.0F);
        this.Body = new ModelRenderer(this, 0, 40);
        this.Body.setRotationPoint(0.0F, 21.5F, 0.0F);
        this.Body.addBox(-3.0F, -2.0F, 0.0F, 6, 4, 4, 0.0F);
        this.Tail = new ModelRenderer(this, 20, 13);
        this.Tail.setRotationPoint(0.0F, -0.1F, 7.0F);
        this.Tail.addBox(-3.0F, -2.5F, 0.0F, 6, 5, 2, 0.0F);
        this.Head.addChild(this.LeftMouth);
        this.Shell.addChild(this.Spikes1);
        this.Head.addChild(this.RightMouth);
        this.Shell.addChild(this.Spikes2);
        this.Shell.addChild(this.Tail);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.Head.render(f5);
        this.Shell.render(f5);
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
