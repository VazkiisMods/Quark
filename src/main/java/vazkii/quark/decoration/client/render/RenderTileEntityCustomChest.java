/**
 * This class was created by <SanAndreasP>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [20/03/2016, 22:33:44 (GMT)]
 */
package vazkii.quark.decoration.client.render;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import vazkii.quark.decoration.tileentity.TileEntityCustomChest;

public class RenderTileEntityCustomChest extends TileEntitySpecialRenderer<TileEntityCustomChest> {

    private final ModelChest simpleChest = new ModelChest();
    private final ModelChest largeChest = new ModelLargeChest();

    @Override
    public void renderTileEntityAt(TileEntityCustomChest te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.depthMask(true);
        int meta;

        if(te.hasWorldObj()) {
            meta = te.getBlockMetadata();
            te.checkForAdjacentChests();
        } else
            meta = 0;

        if(te.adjacentChestZNeg == null && te.adjacentChestXNeg == null) {
            ModelChest model;

            if(te.adjacentChestXPos == null && te.adjacentChestZPos == null) {
                model = this.simpleChest;

                if(destroyStage >= 0) {
                    this.bindTexture(DESTROY_STAGES[destroyStage]);
                    GlStateManager.matrixMode(GL11.GL_TEXTURE);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(4.0F, 4.0F, 1.0F);
                    GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                    GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                } else
                    this.bindTexture(te.chestType.nrmTex);
            } else {
                model = this.largeChest;

                if(destroyStage >= 0) {
                    this.bindTexture(DESTROY_STAGES[destroyStage]);
                    GlStateManager.matrixMode(GL11.GL_TEXTURE);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(8.0F, 4.0F, 1.0F);
                    GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                    GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                } else
                    this.bindTexture(te.chestType.dblTex);
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();

            if(destroyStage < 0)
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.translate(x, y + 1.0F, z + 1.0F);
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);
            int angle = 0;

            if(meta == 2)
                angle = 180;

            if(meta == 3)
                angle = 0;

            if(meta == 4)
                angle = 90;

            if(meta == 5)
                angle = -90;

            if(meta == 2 && te.adjacentChestXPos != null) {
                GlStateManager.translate(1.0F, 0.0F, 0.0F);
            }

            if(meta == 5 && te.adjacentChestZPos != null) {
                GlStateManager.translate(0.0F, 0.0F, -1.0F);
            }

            GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            float lidAngle = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;

            if(te.adjacentChestZNeg != null) {
                float adjLidAngle = te.adjacentChestZNeg.prevLidAngle + (te.adjacentChestZNeg.lidAngle - te.adjacentChestZNeg.prevLidAngle) * partialTicks;

                if(adjLidAngle > lidAngle)
                    lidAngle = adjLidAngle;
            }

            if(te.adjacentChestXNeg != null) {
                float adjLidAngle = te.adjacentChestXNeg.prevLidAngle + (te.adjacentChestXNeg.lidAngle - te.adjacentChestXNeg.prevLidAngle) * partialTicks;

                if(adjLidAngle > lidAngle)
                    lidAngle = adjLidAngle;
            }

            lidAngle = 1.0F - lidAngle;
            lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
            model.chestLid.rotateAngleX = -(lidAngle * ((float) Math.PI / 2F));
            model.renderAll();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if(destroyStage >= 0) {
                GlStateManager.matrixMode(GL11.GL_TEXTURE);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            }
        }
    }
}
