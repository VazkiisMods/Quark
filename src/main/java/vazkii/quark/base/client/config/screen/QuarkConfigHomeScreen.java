package vazkii.quark.base.client.config.screen;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.config.IngameConfigHandler;
import vazkii.quark.base.client.config.external.ExternalConfigHandler;
import vazkii.quark.base.client.config.screen.widgets.CheckboxButton;
import vazkii.quark.base.client.config.screen.widgets.IconButton;
import vazkii.quark.base.client.config.screen.widgets.SocialButton;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.zeta.module.ZetaCategory;

public class QuarkConfigHomeScreen extends AbstractQScreen {

	private static final CubeMap CUBE_MAP = new CubeMap(new ResourceLocation(Quark.MOD_ID, "textures/misc/panorama/panorama"));
	private static final PanoramaRenderer PANORAMA = new PanoramaRenderer(CUBE_MAP);
	private float time;

	public QuarkConfigHomeScreen(Screen parent) {
		super(parent);
	}

	@Override
	protected void init() {
		super.init();

		final int perLine = 3;
		boolean addExternal = ExternalConfigHandler.instance.hasAny();

		int pad = 10;
		int vpad = 23;
		int bWidth = 120;
		int left = width / 2 - ((bWidth + pad) * perLine / 2) + 4;
		int vStart = 70;

		int i = 0;

		List<ZetaCategory> categories = Quark.ZETA.modules.getInhabitedCategories();

		int catCount = categories.size() + 1;
		if(addExternal)
			catCount++;

		boolean shiftedLeft = false;
		int useLeft = left;

		for(ZetaCategory category : categories) {
			if(!shiftedLeft && catCount - i < perLine) {
				useLeft = width / 2 - ((bWidth + pad) * (catCount - i) / 2);
				shiftedLeft = true;
			}

			int x = useLeft + (bWidth + pad) * (i % perLine);
			int y = vStart + (i / perLine) * vpad;

			IConfigCategory configCategory = IngameConfigHandler.INSTANCE.getConfigCategory(category);
			Component comp = componentFor(configCategory);

			Button icon = new IconButton(x, y, bWidth - 20, 20, comp, category.icon.get(), categoryLink(configCategory));
			Button checkbox = new CheckboxButton(x + bWidth - 20, y, IngameConfigHandler.INSTANCE.getCategoryEnabledObject(category));

			addRenderableWidget(icon);
			addRenderableWidget(checkbox);

			if(category.requiredMod != null && !ModList.get().isLoaded(category.requiredMod)) {
				icon.active = false;
				checkbox.active = false;
			}

			i++;
		}

		IConfigCategory cat = IngameConfigHandler.INSTANCE.getConfigCategory(null);
		addRenderableWidget(new Button(useLeft + (bWidth + pad) * (i % perLine), vStart + (i / perLine) * vpad, bWidth, 20, componentFor(cat), categoryLink(cat)));
		i++;

		if(addExternal) {
			cat = ExternalConfigHandler.instance.mockCategory;
			addRenderableWidget(new Button(useLeft + (bWidth + pad) * (i % perLine), vStart + (i / perLine) * vpad, bWidth, 20, componentFor(cat), categoryLink(cat)));
		}

		bWidth = 200;
		addRenderableWidget(new Button(width / 2 - bWidth / 2, height - 30, bWidth, 20, Component.translatable("quark.gui.config.save"), this::commit));

		vStart = height - 55;
		bWidth = 20;
		pad = 5;
		left = (width - (bWidth + pad) * 5) / 2;
		addRenderableWidget(new SocialButton(left, vStart, Component.translatable("quark.gui.config.social.website"), 0x48ddbc, 0, webLink("https://quarkmod.net")));
		addRenderableWidget(new SocialButton(left + bWidth + pad, vStart, Component.translatable("quark.gui.config.social.discord"), 0x7289da, 1, webLink("https://discord.gg/vm")));
		addRenderableWidget(new SocialButton(left + (bWidth + pad) * 2, vStart, Component.translatable("quark.gui.config.social.patreon"), 0xf96854, 2, webLink("https://patreon.com/vazkii")));
		addRenderableWidget(new SocialButton(left + (bWidth + pad) * 3, vStart, Component.translatable("quark.gui.config.social.forum"), 0xb650d8, 3, webLink("https://forum.violetmoon.org")));
		addRenderableWidget(new SocialButton(left + (bWidth + pad) * 4, vStart, Component.translatable("quark.gui.config.social.twitter"), 0x1da1f2, 4, webLink("https://twitter.com/VazkiiMods")));
	}

	private static Component componentFor(IConfigCategory c) {
		MutableComponent comp = Component.translatable("quark.category." + c.getName());

		if(c.isDirty())
			comp.append(Component.translatable("*").withStyle(ChatFormatting.GOLD));

		return comp;
	}

	public void commit(Button button) {
		IngameConfigHandler.INSTANCE.commit();
		returnToParent(button);
	}

	@Override
	public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
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
		} else renderBackground(mstack);

		int boxWidth = 400;
		fill(mstack, width / 2 - boxWidth / 2, 0, width / 2 + boxWidth / 2, this.height, 0x66000000);
		fill(mstack, width / 2 - boxWidth / 2 - 1, 0, width / 2 - boxWidth / 2, this.height, 0x66999999); // nice
		fill(mstack, width / 2 + boxWidth / 2, 0, width / 2 + boxWidth / 2 + 1, this.height, 0x66999999);

		super.render(mstack, mouseX, mouseY, partialTicks);

		drawCenteredString(mstack, font, ChatFormatting.BOLD + I18n.get("quark.gui.config.header", WordUtils.capitalizeFully(Quark.MOD_ID)), width / 2, 15, 0x48ddbc);
		drawCenteredString(mstack, font, I18n.get("quark.gui.config.subheader1", ChatFormatting.LIGHT_PURPLE, ContributorRewardHandler.featuredPatron, ChatFormatting.RESET), width / 2, 28, 0x9EFFFE);
		drawCenteredString(mstack, font, I18n.get("quark.gui.config.subheader2"), width / 2, 38, 0x9EFFFE);
	}

}
