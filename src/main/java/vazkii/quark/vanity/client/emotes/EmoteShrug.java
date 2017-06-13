/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 23:21:47 (GMT)]
 */
package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.quark.vanity.client.emotes.base.EmoteBase;
import vazkii.quark.vanity.client.emotes.base.EmoteDescriptor;
import vazkii.quark.vanity.client.emotes.base.ModelAccessor;

public class EmoteShrug extends EmoteBase {

	public EmoteShrug(EmoteDescriptor desc, EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
		super(desc, player, model, armorModel, armorLegsModel);
	}

	@Override
	public Timeline getTimeline(EntityPlayer player, ModelBiped model) {
		Timeline timeline = Timeline.createParallel()
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_Z, 400F).target(0.7F).repeatYoyo(1, 0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_Z, 400F).target(-0.7F).repeatYoyo(1, 0F))
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_X, 400F).target(-0.7F).repeatYoyo(1, 0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_X, 400F).target(-0.7F).repeatYoyo(1, 0F));

		return timeline;
	}

	@Override
	public boolean usesBodyPart(int part) {
		return part == ModelAccessor.RIGHT_ARM || part == ModelAccessor.LEFT_ARM;
	}

}