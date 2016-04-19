/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [26/03/2016, 23:11:34 (GMT)]
 */
package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.quark.vanity.client.emotes.base.EmoteBase;
import vazkii.quark.vanity.client.emotes.base.ModelAccessor;

public class EmoteFacepalm extends EmoteBase {

	public EmoteFacepalm(EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
		super(player, model, armorModel, armorLegsModel);
	}

	@Override
	public Timeline getTimeline(EntityPlayer player, ModelBiped model) {
		Timeline timeline = Timeline.createSequence()
				.beginParallel()
				.push(Tween.to(model, ModelAccessor.HEAD_X, 1000F).target(0.2F))
				.push(Tween.to(model, ModelAccessor.HEAD_Y, 1000F).target(0F))
				.push(Tween.to(model, ModelAccessor.HEAD_Z, 1000F).target(0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_X, 400F).target(-PI_F + 0.8F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_Z, 400F).target(-1F))
				.end()
				.push(Tween.to(model, ModelAccessor.HEAD_Y, 300F).target(0.05F))
				.push(Tween.to(model, ModelAccessor.HEAD_Y, 300F).target(-0.05F).repeatYoyo(4, 0F))
				.push(Tween.to(model, ModelAccessor.HEAD_Y, 300F).target(0F))
				.beginParallel()
				.push(Tween.to(model, ModelAccessor.HEAD_X, 500F).target(0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_X, 600F).target(0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_Z, 600F).target(0F))
				.end();

		return timeline;
	}

	@Override
	public boolean usesBodyPart(int part) {
		return part == ModelAccessor.HEAD || part == ModelAccessor.LEFT_ARM;
	}

}
