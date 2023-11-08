package org.violetmoon.quark.content.tweaks.client.emote;

import aurelienribon.tweenengine.Timeline;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.violetmoon.quark.base.Quark;

@OnlyIn(Dist.CLIENT)
public class TemplateSourcedEmote extends EmoteBase {

	private static final boolean DEOBF = Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.NAMING.get()).orElse("").equals("mcp");

	public TemplateSourcedEmote(EmoteDescriptor desc, Player player, HumanoidModel<?> model, HumanoidModel<?> armorModel, HumanoidModel<?> armorLegsModel) {
		super(desc, player, model, armorModel, armorLegsModel);

		if(shouldLoadTimelineOnLaunch()) {
			Quark.LOG.debug("Loading emote " + desc.getTranslationKey());
			desc.template.readAndMakeTimeline(desc, player, model);
		}
	}
	
	public boolean shouldLoadTimelineOnLaunch() {
		return DEOBF;
	}

	@Override
	public Timeline getTimeline(Player player, HumanoidModel<?> model) {
		return desc.template.getTimeline(desc, player, model);
	}

	@Override
	public boolean usesBodyPart(int part) {
		return desc.template.usesBodyPart(part);
	}

}
