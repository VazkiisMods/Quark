/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [26/03/2016, 21:37:30 (GMT)]
 */
package vazkii.quark.vanity.client.emotes.base;

import net.minecraft.client.model.ModelBiped;

public class EmoteState {

	float[] states = new float[0];
	EmoteBase emote;

	public EmoteState(EmoteBase emote) {
		this.emote = emote;
	}

	public void save(ModelBiped model) {
		float[] values = new float[1];
		for(int i = 0; i < ModelAccessor.STATE_COUNT; i++) {
			ModelAccessor.INSTANCE.getValues(model, i, values);
			states[i] = values[0];
		}
	}

	public void load(ModelBiped model) {
		if(states.length == 0) {
			states = new float[ModelAccessor.STATE_COUNT];
			return;
		} else {
			float[] values = new float[1];
			for(int i = 0; i < ModelAccessor.STATE_COUNT; i++) {
				values[0] = states[i];

				int part = i / 3 * 3;
				if(emote.usesBodyPart(part))
					ModelAccessor.INSTANCE.setValues(model, i, values);
			}
		}
	}

}

