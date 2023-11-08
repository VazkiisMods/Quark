package org.violetmoon.zetaimplforge.client.event.player;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.violetmoon.zeta.client.event.play.ZRenderLiving;

public abstract class ForgeZRenderLiving implements ZRenderLiving {
	protected final RenderLivingEvent<?, ?> e;

	public ForgeZRenderLiving(RenderLivingEvent<?, ?> e) {
		this.e = e;
	}

	@Override
	public Entity getEntity() {
		return e.getEntity();
	}

	@Override
	public PoseStack getPoseStack() {
		return e.getPoseStack();
	}

	public static class PreHighest extends ForgeZRenderLiving implements ZRenderLiving.PreHighest {
		public PreHighest(RenderLivingEvent.Pre<?, ?> e) {
			super(e);
		}
	}

	public static class PostLowest extends ForgeZRenderLiving implements ZRenderLiving.PostLowest {
		public PostLowest(RenderLivingEvent.Post<?, ?> e) {
			super(e);
		}
	}
}
