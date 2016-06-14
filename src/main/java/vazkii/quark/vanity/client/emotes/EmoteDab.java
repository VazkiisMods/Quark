/**
 * This class was created by <big_Xplosion>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [12/06/2016, 16:23:39 (CEST)]
 */
package vazkii.quark.vanity.client.emotes;

import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.quark.vanity.client.emotes.base.EmoteBase;
import vazkii.quark.vanity.client.emotes.base.ModelAccessor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;

public class EmoteDab extends EmoteBase {

	public EmoteDab(EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
		super(player, model, armorModel, armorLegsModel);
	}

	@Override
	public Timeline getTimeline(EntityPlayer player, ModelBiped model) {
		Timeline timeline = Timeline.createSequence()
				.beginParallel()
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_X, 500F).target(-PI_F))
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_Z, 500F).target(PI_F * -0.3F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_X, 500F).target(-PI_F + 0.8F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_Z, 500F).target(-1F))
				.push(Tween.to(model, ModelAccessor.HEAD_X, 500F).target(0.3F))
				.push(Tween.to(model, ModelAccessor.HEAD_Y, 500F).target(-0.3F))
				.end()
				.pushPause(2500F)
				.beginParallel()
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_X, 500F).target(0F))
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_Z, 500F).target(0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_X, 500F).target(0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_Z, 500F).target(0F))
				.push(Tween.to(model, ModelAccessor.HEAD_X, 500F).target(0F))
				.push(Tween.to(model, ModelAccessor.HEAD_Y, 500F).target(0F))
				.end();
		return timeline;
	}

	@Override
	public boolean usesBodyPart(int part) {
		return part == ModelAccessor.HEAD || part == ModelAccessor.LEFT_ARM || part == ModelAccessor.RIGHT_ARM;
	}
}
