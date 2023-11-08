package org.violetmoon.zetaimplforge.client.event.player;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import org.violetmoon.zeta.client.event.play.ZCustomizeDebugText;

import java.util.ArrayList;

public class ForgeZCustomizeDebugText implements ZCustomizeDebugText {
	private final CustomizeGuiOverlayEvent.DebugText e;

	public ForgeZCustomizeDebugText(CustomizeGuiOverlayEvent.DebugText e) {
		this.e = e;
	}

	@Override
	public ArrayList<String> getLeft() {return e.getLeft();}

	@Override
	public ArrayList<String> getRight() {return e.getRight();}

	@Override
	public Window getWindow() {return e.getWindow();}

	@Override
	public PoseStack getPoseStack() {return e.getPoseStack();}

	@Override
	public float getPartialTick() {return e.getPartialTick();}
}
