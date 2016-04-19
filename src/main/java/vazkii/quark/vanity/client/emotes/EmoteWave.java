/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [26/03/2016, 21:54:37 (GMT)]
 */
package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.quark.vanity.client.emotes.base.EmoteBase;
import vazkii.quark.vanity.client.emotes.base.ModelAccessor;

public class EmoteWave extends EmoteBase {

	public EmoteWave(EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
		super(player, model, armorModel, armorLegsModel);
	}

	@Override
	public Timeline getTimeline(EntityPlayer player, ModelBiped model) {
		Timeline timeline = Timeline.createSequence()
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_X, 200F).target(-PI_F * 0.9F))
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_Z, 200F).target(PI_F * -0.3F).repeatYoyo(5, 0F))
				.push(Tween.to(model, ModelAccessor.RIGHT_ARM_X, 200F).target(0F));

		return timeline;
	}

	@Override
	public boolean usesBodyPart(int part) {
		return part == ModelAccessor.RIGHT_ARM_X;
	}

}
