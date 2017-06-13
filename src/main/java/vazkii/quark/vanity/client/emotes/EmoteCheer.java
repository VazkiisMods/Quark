/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 23:08:47 (GMT)]
 */
package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.quark.vanity.client.emotes.base.EmoteBase;
import vazkii.quark.vanity.client.emotes.base.EmoteDescriptor;
import vazkii.quark.vanity.client.emotes.base.ModelAccessor;

public class EmoteCheer extends EmoteBase {

	public EmoteCheer(EmoteDescriptor desc, EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
		super(desc, player, model, armorModel, armorLegsModel);
	}

	@Override
	public Timeline getTimeline(EntityPlayer player, ModelBiped model) {
		Timeline timeline = Timeline.createSequence()
				.beginParallel()
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_X, 200F).target(-PI_F + 0.1F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_X, 200F).target(-PI_F + 0.1F))
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_Z, 200F).target(-0.2F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_Z, 200F).target(0.2F))
				.end()
				.beginParallel()
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_X, 150F).target(-PI_F + 0.5F).repeatYoyo(11, 0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_X, 150F).target(-PI_F + 0.5F).repeatYoyo(11, 0F))
				.end()
				.beginParallel()
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_Z, 400F).target(0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_Z, 400F).target(0F))
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_X, 400F).target(0F))
				.push(Tween.to(model, ModelAccessor.LEFT_ARM_X, 400F).target(0F))
				.end();

		return timeline;
	}

	@Override
	public boolean usesBodyPart(int part) {
		return part == ModelAccessor.RIGHT_ARM || part == ModelAccessor.LEFT_ARM;
	}
}