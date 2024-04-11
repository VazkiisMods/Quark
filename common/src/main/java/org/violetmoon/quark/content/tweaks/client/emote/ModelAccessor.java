/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:37:50 (GMT)]
 */
package org.violetmoon.quark.content.tweaks.client.emote;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.Map;
import java.util.WeakHashMap;

import aurelienribon.tweenengine.TweenAccessor;

public class ModelAccessor implements TweenAccessor<HumanoidModel<?>> {

	public static final ModelAccessor INSTANCE = new ModelAccessor();

	private static final int ROT_X = 0;
	private static final int ROT_Y = 1;
	private static final int ROT_Z = 2;

	protected static final int MODEL_PROPS = 3;
	protected static final int BODY_PARTS = 7;
	protected static final int STATE_COUNT = MODEL_PROPS * BODY_PARTS;

	public static final int HEAD = 0;
	public static final int BODY = MODEL_PROPS;
	public static final int RIGHT_ARM = 2 * MODEL_PROPS;
	public static final int LEFT_ARM = 3 * MODEL_PROPS;
	public static final int RIGHT_LEG = 4 * MODEL_PROPS;
	public static final int LEFT_LEG = 5 * MODEL_PROPS;
	public static final int MODEL = 6 * MODEL_PROPS;

	public static final int HEAD_X = HEAD + ROT_X;
	public static final int HEAD_Y = HEAD + ROT_Y;
	public static final int HEAD_Z = HEAD + ROT_Z;
	public static final int BODY_X = BODY + ROT_X;
	public static final int BODY_Y = BODY + ROT_Y;
	public static final int BODY_Z = BODY + ROT_Z;
	public static final int RIGHT_ARM_X = RIGHT_ARM + ROT_X;
	public static final int RIGHT_ARM_Y = RIGHT_ARM + ROT_Y;
	public static final int RIGHT_ARM_Z = RIGHT_ARM + ROT_Z;
	public static final int LEFT_ARM_X = LEFT_ARM + ROT_X;
	public static final int LEFT_ARM_Y = LEFT_ARM + ROT_Y;
	public static final int LEFT_ARM_Z = LEFT_ARM + ROT_Z;
	public static final int RIGHT_LEG_X = RIGHT_LEG + ROT_X;
	public static final int RIGHT_LEG_Y = RIGHT_LEG + ROT_Y;
	public static final int RIGHT_LEG_Z = RIGHT_LEG + ROT_Z;
	public static final int LEFT_LEG_X = LEFT_LEG + ROT_X;
	public static final int LEFT_LEG_Y = LEFT_LEG + ROT_Y;
	public static final int LEFT_LEG_Z = LEFT_LEG + ROT_Z;

	public static final int MODEL_X = MODEL + ROT_X;
	public static final int MODEL_Y = MODEL + ROT_Y;
	public static final int MODEL_Z = MODEL + ROT_Z;

	private final Map<HumanoidModel<?>, float[]> MODEL_VALUES = new WeakHashMap<>();

	public void resetModel(HumanoidModel<?> model) {
		MODEL_VALUES.remove(model);
	}

	@Override
	public int getValues(HumanoidModel<?> target, int tweenType, float[] returnValues) {
		int axis = tweenType % MODEL_PROPS;
		int bodyPart = tweenType - axis;

		if(bodyPart == MODEL) {
			if(!MODEL_VALUES.containsKey(target)) {
				returnValues[0] = 0;
				return 1;
			}

			float[] values = MODEL_VALUES.get(target);
			returnValues[0] = values[axis];
			return 1;
		}

		ModelPart model = getBodyPart(target, bodyPart);
		if(model == null)
			return 0;

		switch(axis) {
		case ROT_X -> returnValues[0] = model.xRot;
		case ROT_Y -> returnValues[0] = model.yRot;
		case ROT_Z -> returnValues[0] = model.zRot;
		}

		return 1;
	}

	private ModelPart getBodyPart(HumanoidModel<?> model, int part) {
		return switch(part) {
		case HEAD -> model.head;
		case BODY -> model.body;
		case RIGHT_ARM -> model.rightArm;
		case LEFT_ARM -> model.leftArm;
		case RIGHT_LEG -> model.rightLeg;
		case LEFT_LEG -> model.leftLeg;
		default -> null;
		};
	}

	@Override
	public void setValues(HumanoidModel<?> target, int tweenType, float[] newValues) {
		int axis = tweenType % MODEL_PROPS;
		int bodyPart = tweenType - axis;

		if(bodyPart == MODEL) {
			float[] values = MODEL_VALUES.computeIfAbsent(target, k -> new float[MODEL_PROPS]);

			values[axis] = newValues[0];

			return;
		}

		ModelPart model = getBodyPart(target, bodyPart);
		messWithModel(target, model, axis, newValues[0]);
	}

	private void messWithModel(HumanoidModel<?> biped, ModelPart part, int axis, float val) {
		setPartAxis(part, axis, val);
		if(biped instanceof PlayerModel)
			messWithPlayerModel((PlayerModel<?>) biped, part, axis, val);
	}

	private void messWithPlayerModel(PlayerModel<?> biped, ModelPart part, int axis, float val) {
		if(part == biped.head) {
			setPartAxis(biped.hat, axis, val);
		} else if(part == biped.leftArm)
			setPartAxis(biped.leftSleeve, axis, val);
		else if(part == biped.rightArm)
			setPartAxis(biped.rightSleeve, axis, val);
		else if(part == biped.leftLeg)
			setPartAxis(biped.leftPants, axis, val);
		else if(part == biped.rightLeg)
			setPartAxis(biped.rightPants, axis, val);
		else if(part == biped.body)
			setPartAxis(biped.jacket, axis, val);
	}

	private void setPartAxis(ModelPart part, int axis, float val) {
		if(part == null)
			return;

		switch(axis) {
		case ROT_X -> part.xRot = val;
		case ROT_Y -> part.yRot = val;
		case ROT_Z -> part.zRot = val;
		}
	}

}
