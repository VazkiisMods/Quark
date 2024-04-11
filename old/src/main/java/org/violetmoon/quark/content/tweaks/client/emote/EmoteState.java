/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:37:30 (GMT)]
 */
package org.violetmoon.quark.content.tweaks.client.emote;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;

public class EmoteState {

	private float[] states = new float[0];
	private final EmoteBase emote;

	public EmoteState(EmoteBase emote) {
		this.emote = emote;
	}

	public void save(HumanoidModel<?> model) {
		float[] values = new float[1];
		for(int i = 0; i < ModelAccessor.STATE_COUNT; i++) {
			ModelAccessor.INSTANCE.getValues(model, i, values);
			states[i] = values[0];
		}
	}

	public void load(HumanoidModel<?> model) {
		if(states.length == 0) {
			states = new float[ModelAccessor.STATE_COUNT];
		} else {
			float[] values = new float[1];
			for(int i = 0; i < ModelAccessor.STATE_COUNT; i++) {
				values[0] = states[i];

				int part = (i / ModelAccessor.MODEL_PROPS) * ModelAccessor.MODEL_PROPS;
				if(emote.usesBodyPart(part))
					ModelAccessor.INSTANCE.setValues(model, i, values);
			}
		}
	}

	public void rotateAndOffset(PoseStack stack, Player player) {
		if(states.length == 0)
			return;

		float rotX = states[ModelAccessor.MODEL_X];
		float rotY = states[ModelAccessor.MODEL_Y];
		float rotZ = states[ModelAccessor.MODEL_Z];

		float height = player.getBbHeight();

		stack.translate(0, height / 2, 0);

		if(rotY != 0)
			stack.mulPose(Axis.YP.rotation(rotY));
		if(rotX != 0)
			stack.mulPose(Axis.XP.rotation(rotX));
		if(rotZ != 0)
			stack.mulPose(Axis.ZP.rotation(rotZ));

		stack.translate(0, -height / 2, 0);
	}
}
