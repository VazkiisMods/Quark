package org.violetmoon.quark.base.client.config;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.QuarkClient;
import org.violetmoon.quark.base.handler.ContributorRewardHandler;
import org.violetmoon.quark.base.handler.GeneralConfig;
import org.violetmoon.zeta.client.config.screen.ZetaConfigHomeScreen;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class QuarkConfigHomeScreen extends ZetaConfigHomeScreen {

	private static final CubeMap CUBE_MAP = new CubeMap(new ResourceLocation(Quark.MOD_ID, "textures/misc/panorama/panorama"));
	private static final PanoramaRenderer PANORAMA = new PanoramaRenderer(CUBE_MAP);
	private float time;

	public QuarkConfigHomeScreen(Screen parent) {
		super(QuarkClient.ZETA_CLIENT, parent);
	}

	@Override
	protected void init() {
		super.init();

		List<Integer> socialButtonPlacements = centeredRow(width / 2, 20, 5, 5);
		Iterator<Integer> iter = socialButtonPlacements.iterator();
		addRenderableWidget(new SocialButton(iter.next(), height - 55, Component.translatable("quark.gui.config.social.website"), 0x48ddbc, 0, "https://quarkmod.net"));
		addRenderableWidget(new SocialButton(iter.next(), height - 55, Component.translatable("quark.gui.config.social.discord"), 0x7289da, 1, "https://discord.gg/vm"));
		addRenderableWidget(new SocialButton(iter.next(), height - 55, Component.translatable("quark.gui.config.social.patreon"), 0xf96854, 2, "https://patreon.com/vazkii"));
		addRenderableWidget(new SocialButton(iter.next(), height - 55, Component.translatable("quark.gui.config.social.forum"), 0xb650d8, 3, "https://forum.violetmoon.org"));
		addRenderableWidget(new SocialButton(iter.next(), height - 55, Component.translatable("quark.gui.config.social.twitter"), 0x1da1f2, 4, "https://twitter.com/VazkiiMods"));
	}

	//annoyingly it's not passed to renderBackground
	protected float partialTicks;

	@Override
	public void renderBackground(PoseStack mstack) {
		time += partialTicks;

		Minecraft mc = Minecraft.getInstance();
		if(mc.level == null) {
			float spin = partialTicks * 2;
			float blur = 0.85F;

			if(time < 20F && !GeneralConfig.disableQMenuEffects) {
				spin += (20F - time);
				blur = (time / 20F) * 0.75F + 0.1F;
			}

			PANORAMA.render(spin, blur);
		} else super.renderBackground(mstack);

		int boxWidth = 400;
		fill(mstack, width / 2 - boxWidth / 2, 0, width / 2 + boxWidth / 2, this.height, 0x66000000);
		fill(mstack, width / 2 - boxWidth / 2 - 1, 0, width / 2 - boxWidth / 2, this.height, 0x66999999); // nice
		fill(mstack, width / 2 + boxWidth / 2, 0, width / 2 + boxWidth / 2 + 1, this.height, 0x66999999);
	}

	@Override
	public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		this.partialTicks = partialTicks;

		super.render(mstack, mouseX, mouseY, partialTicks);

		drawCenteredString(mstack, font, I18n.get("quark.gui.config.subheader1", ChatFormatting.LIGHT_PURPLE, ContributorRewardHandler.featuredPatron, ChatFormatting.RESET), width / 2, 28, 0x9EFFFE);
		drawCenteredString(mstack, font, I18n.get("quark.gui.config.subheader2"), width / 2, 38, 0x9EFFFE);

		//TODO TODO TODO, flesh this out !
		int changeCount = changeSet.changeCount();
		if(changeCount != 0)
			drawCenteredString(mstack, font, changeCount + " unsaved change" + (changeCount > 1 ? "s" : ""), width/2 - 150, height-30, 0xFF8800);
	}

}
