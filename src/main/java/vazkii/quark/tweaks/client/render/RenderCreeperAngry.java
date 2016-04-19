/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [19/03/2016, 16:57:46 (GMT)]
 */
package vazkii.quark.tweaks.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCreeperAngry extends RenderCreeper {

	public RenderCreeperAngry(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCreeper entity) {
		if(entity.getCreeperState() == 1) {
			float antired = 1F - (entity.getCreeperFlashIntensity(0F) / 1.17F + 0.1F);
			GL11.glColor3f(1F, antired, antired);
		}
		return super.getEntityTexture(entity);
	}

	public static IRenderFactory factory() {
		return manager -> new RenderCreeperAngry(manager);
	}

}