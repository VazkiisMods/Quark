/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [26/03/2016, 21:37:50 (GMT)]
 */
package vazkii.quark.vanity.client.emotes.base;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import vazkii.aurelienribon.tweenengine.TweenAccessor;

public class ModelAccessor implements TweenAccessor<ModelBiped> {

	public static final ModelAccessor INSTANCE = new ModelAccessor();

	private static final int ROT_X = 0;
	private static final int ROT_Y = 1;
	private static final int ROT_Z = 2;

	public static final int HEAD = 0 * 3;
	public static final int BODY = 1 * 3;
	public static final int RIGHT_ARM = 2 * 3;
	public static final int LEFT_ARM = 3 * 3;
	public static final int RIGHT_LEG = 5 * 3;
	public static final int LEFT_LEG = 6 * 3;

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

	public static final int STATE_COUNT = LEFT_LEG_Z;

	@Override
	public int getValues(ModelBiped target, int tweenType, float[] returnValues) {
		int axis = tweenType % 3;
		int bodyPart = tweenType / 3 * 3;
		ModelRenderer model = getBodyPart(target, bodyPart);
		if(model == null)
			return 0;

		if(axis == ROT_X)
			returnValues[0] = model.rotateAngleX;
		else if(axis == ROT_Y)
			returnValues[0] = model.rotateAngleY;
		else returnValues[0] = model.rotateAngleZ;

		return 1;
	}

	private ModelRenderer getBodyPart(ModelBiped model, int part) {
		switch(part) {
		case HEAD : return model.bipedHead;
		case BODY : return model.bipedBody;
		case RIGHT_ARM : return model.bipedRightArm;
		case LEFT_ARM : return model.bipedLeftArm;
		case RIGHT_LEG : return model.bipedRightLeg;
		case LEFT_LEG : return model.bipedLeftLeg;
		}
		return null;
	}

	@Override
	public void setValues(ModelBiped target, int tweenType, float[] newValues) {
		int axis = tweenType % 3;
		int bodyPart = tweenType / 3 * 3;
		ModelRenderer model = getBodyPart(target, bodyPart);
		messWithModel(target, model, axis, newValues[0]);
	}

	private void messWithModel(ModelBiped biped, ModelRenderer part, int axis, float val) {
		if(part == null)
			return;

		if(axis == ROT_X)
			part.rotateAngleX = val;
		else if(axis == ROT_Y)
			part.rotateAngleY = val;
		else if(axis == ROT_Z)
			part.rotateAngleZ = val;


		if(part == biped.bipedHead)
			messWithModel(biped, biped.bipedHeadwear, axis, val);
	}

}

