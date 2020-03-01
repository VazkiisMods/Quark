package vazkii.quark.management.client.render;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import vazkii.quark.management.entity.ChestPassengerEntity;

/**
 * @author WireSegal
 * Created at 2:02 PM on 9/3/19.
 */
public class ChestPassengerRenderer extends EntityRenderer<ChestPassengerEntity> {

    public ChestPassengerRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }
    
    @Override
    	public void render(ChestPassengerEntity entity, float yaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light) {
        if(!entity.isPassenger())
            return;

        Entity riding = entity.getRidingEntity();
        if (riding == null)
            return;

        BoatEntity boat = (BoatEntity) riding;
        super.render(entity, yaw, partialTicks, matrix, buffer, light);
        
        float rot = 180F - yaw;

        ItemStack stack = entity.getChestType();

        matrix.push();
        matrix.translate(0, 0.375, 0);
        matrix.rotate(Vector3f.YP.rotationDegrees(rot));
        float timeSinceHit = boat.getTimeSinceHit() - partialTicks;
        float damageTaken = boat.getDamageTaken() - partialTicks;

        if (damageTaken < 0.0F)
            damageTaken = 0.0F;

        if (timeSinceHit > 0.0F) {
        	double angle = MathHelper.sin(timeSinceHit) * timeSinceHit * damageTaken / 10.0F * boat.getForwardDirection();
            matrix.rotate(Vector3f.XP.rotationDegrees((float) angle));
        }

        float rock = boat.getRockingAngle(partialTicks);
        if (!MathHelper.epsilonEquals(rock, 0.0F)) {
        	 matrix.rotate(Vector3f.XP.rotationDegrees(rock));
        }

        if (riding.getPassengers().size() > 1)
        	matrix.translate(0F, 0F, -0.6F);
        else
        	matrix.translate(0F, 0F, -0.45F);

        matrix.translate(0F, 0.7F - 0.375F, 0.6F - 0.15F);

        matrix.scale(1.75F, 1.75F, 1.75F);

        Minecraft.getInstance().getItemRenderer().renderItem(stack, TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, buffer);
        matrix.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(@Nonnull ChestPassengerEntity entity) {
        return null;
    }

}
