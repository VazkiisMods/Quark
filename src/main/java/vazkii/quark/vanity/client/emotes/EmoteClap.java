/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [26/03/2016, 23:11:00 (GMT)]
 */
package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.quark.vanity.client.emotes.base.EmoteBase;
import vazkii.quark.vanity.client.emotes.base.ModelAccessor;

public class EmoteClap extends EmoteBase {

	public EmoteClap(EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
		super(player, model, armorModel, armorLegsModel);
	}

	@Override
	public Timeline getTimeline(EntityPlayer player, ModelBiped model) {
		Timeline timeline = Timeline.createSequence()
				.beginParallel()
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_X, 400F).target(-PI_F / 2))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_X, 400F).target(-PI_F / 2))
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_Y, 400F).target(-PI_F / 4 + 0.25F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_Y, 400F).target(PI_F / 4 - 0.25F))
				.end()
				.beginParallel()
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_Y, 100F).target(-PI_F / 4 + 0.4F).repeatYoyo(11, 0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_Y, 100F).target(PI_F / 4 - 0.4F).repeatYoyo(11, 0F))
				.end()
				.beginParallel()
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_X, 400F).target(0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_X, 400F).target(0F))
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_Y, 400F).target(0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_Y, 400F).target(0F))
				.end();

		return timeline;
	}

	@Override
	public boolean usesBodyPart(int part) {
		return part == ModelAccessor.RIGHT_ARM || part == ModelAccessor.LEFT_ARM;
	}

}