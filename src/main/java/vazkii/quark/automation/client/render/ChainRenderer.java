/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 17, 2019, 20:06 AM (EST)]
 */
package vazkii.quark.automation.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.automation.base.ChainHandler;

@OnlyIn(Dist.CLIENT)
public class ChainRenderer {
	private static final IntObjectMap<Entity> RENDER_MAP = new IntObjectHashMap<>();

	public static void drawChainSegment(double x, double y, double z, BufferBuilder buf, double offsetX, double offsetY, double offsetZ, double xOff, double zOff, float baseR, float baseG, float baseB, double height) {
		buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

		double centroid = xOff + zOff / 2;
		for (int seg = 0; seg <= 24; ++seg) {
			float r = baseR;
			float g = baseG;
			float b = baseB;

			if (seg % 2 == 0) {
				r *= 0.7F;
				g *= 0.7F;
				b *= 0.7F;
			}

			float amount = seg / 24.0F;
			buf.pos(x + offsetX * amount - centroid, y + offsetY * (amount * amount + amount) * 0.5D + ((24.0F - seg) / 18.0F + 0.125F) * height + xOff / 2 - zOff / 2, z + offsetZ * amount - xOff / 2).color(r, g, b, 1.0F).endVertex();
			buf.pos(x + offsetX * amount + centroid, y + offsetY * (amount * amount + amount) * 0.5D + ((24.0F - seg) / 18.0F + 0.125F) * height + zOff / 2 - xOff / 2, z + offsetZ * amount + xOff / 2).color(r, g, b, 1.0F).endVertex();
		}

		Tessellator.getInstance().draw();
	}
	
	// TODO WIRE: does not render

	public static void renderChain(EntityRenderer render, double x, double y, double z, Entity entity, float partTicks) {
		if (ChainHandler.canBeLinked(entity)/* && !render.renderOutlines*/) {
			renderChain(entity, x, y, z, partTicks);
		}
	}

	private static double interp(double start, double end, double pct)
	{
		return start + (end - start) * pct;
	}

	private static double prevX(Entity entity) {
		if (entity instanceof AbstractMinecartEntity)
			return entity.lastTickPosX;
		return entity.prevPosX;
	}
	private static double prevY(Entity entity) {
		if (entity instanceof AbstractMinecartEntity)
			return entity.lastTickPosY;
		return entity.prevPosY;
	}
	private static double prevZ(Entity entity) {
		if (entity instanceof AbstractMinecartEntity)
			return entity.lastTickPosZ;
		return entity.prevPosZ;
	}

	private static void renderChain(Entity cart, double x, double y, double z, float partialTicks) {
		Entity entity = RENDER_MAP.get(cart.getEntityId());

		if (entity != null) {
			boolean player = entity instanceof PlayerEntity;
			
			if(player)
				y -= 1.3;
			else y += 0.1;
			
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buf = tess.getBuffer();
			double yaw = interp(entity.prevRotationYaw, entity.rotationYaw, (partialTicks * 0.5F)) * Math.PI / 180;
			double pitch = interp(entity.prevRotationPitch, entity.rotationPitch, (partialTicks * 0.5F)) * Math.PI / 180;
			double rotX = Math.cos(yaw);
			double rotZ = Math.sin(yaw);
			double rotY = Math.sin(pitch);

			double height = player ? entity.getEyeHeight() * 0.7 : 0;

			Vec3d entityPos = entity.getPositionVec();
			Vec3d cartPos = cart.getPositionVec();
			
			double pitchMod = Math.cos(pitch);
			double xLocus = interp(prevX(entity), entityPos.x, partialTicks);
			double yLocus = interp(prevY(entity), entityPos.y, partialTicks) + height;
			double zLocus = interp(prevZ(entity), entityPos.z, partialTicks);

			if (player) {
				xLocus += -rotX * 0.7D - rotZ * 0.5D * pitchMod;
				yLocus += -rotY * 0.5D - 0.25D;
				zLocus += -rotZ * 0.7D + rotX * 0.5D * pitchMod;
				
				zLocus -= 1;
				yLocus += 2;
			}

			double targetX = interp(prevX(cart), entityPos.x, partialTicks);
			double targetY = interp(prevY(cart), entityPos.y, partialTicks);
			double targetZ = interp(prevZ(cart), entityPos.z, partialTicks);
			if (player) {
				xLocus -= rotX;
				zLocus -= rotZ;
			}
			
			double offsetX = ((float) (xLocus - targetX));
			double offsetY = ((float) (yLocus - targetY));
			double offsetZ = ((float) (zLocus - targetZ));
			RenderSystem.disableTexture();
			RenderSystem.disableLighting();
			RenderSystem.disableCull();

			drawChainSegment(x, y, z, buf, offsetX, offsetY, offsetZ, 0.025, 0, 0.3f, 0.3f, 0.3f, height);

			drawChainSegment(x, y, z, buf, offsetX, offsetY, offsetZ, 0, 0.025, 0.3f, 0.3f, 0.3f, height);

			RenderSystem.enableLighting();
			RenderSystem.enableTexture();
			RenderSystem.enableCull();
		}
	}

	public static void updateTick() {
		RENDER_MAP.clear();

		ClientWorld world = Minecraft.getInstance().world;
		if (world == null)
			return;

		for (Entity entity : world.getAllEntities()) {
			if (ChainHandler.canBeLinked(entity)) {
				Entity other = ChainHandler.getLinked(entity);
				if (other != null)
					RENDER_MAP.put(entity.getEntityId(), other);
			}
		}
	}
}
