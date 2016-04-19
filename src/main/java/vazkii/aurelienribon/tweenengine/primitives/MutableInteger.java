package vazkii.aurelienribon.tweenengine.primitives;

import vazkii.aurelienribon.tweenengine.TweenAccessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class MutableInteger extends Number implements TweenAccessor<MutableInteger> {
	private int value;

	public MutableInteger(int value) {
		this.value = value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override public int intValue() {return value;}
	@Override public long longValue() {return value;}
	@Override public float floatValue() {return value;}
	@Override public double doubleValue() {return value;}

	@Override
	public int getValues(MutableInteger target, int tweenType, float[] returnValues) {
		returnValues[0] = target.value;
		return 1;
	}

	@Override
	public void setValues(MutableInteger target, int tweenType, float[] newValues) {
		target.value = (int) newValues[0];
	}
}
