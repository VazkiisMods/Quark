package vazkii.aurelienribon.tweenengine;

import vazkii.aurelienribon.tweenengine.equations.Back;
import vazkii.aurelienribon.tweenengine.equations.Bounce;
import vazkii.aurelienribon.tweenengine.equations.Circ;
import vazkii.aurelienribon.tweenengine.equations.Cubic;
import vazkii.aurelienribon.tweenengine.equations.Elastic;
import vazkii.aurelienribon.tweenengine.equations.Expo;
import vazkii.aurelienribon.tweenengine.equations.Linear;
import vazkii.aurelienribon.tweenengine.equations.Quad;
import vazkii.aurelienribon.tweenengine.equations.Quart;
import vazkii.aurelienribon.tweenengine.equations.Quint;
import vazkii.aurelienribon.tweenengine.equations.Sine;

/**
 * Collection of miscellaneous utilities.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TweenUtils {
	private static TweenEquation[] easings;

	/**
	 * Takes an easing name and gives you the corresponding TweenEquation.
	 * You probably won't need this, but tools will love that.
	 *
	 * @param easingName The name of an easing, like "Quad.INOUT".
	 * @return The parsed equation, or null if there is no match.
	 */
	public static TweenEquation parseEasing(String easingName) {
		if (easings == null) {
			easings = new TweenEquation[] {Linear.INOUT,
					Quad.IN, Quad.OUT, Quad.INOUT,
					Cubic.IN, Cubic.OUT, Cubic.INOUT,
					Quart.IN, Quart.OUT, Quart.INOUT,
					Quint.IN, Quint.OUT, Quint.INOUT,
					Circ.IN, Circ.OUT, Circ.INOUT,
					Sine.IN, Sine.OUT, Sine.INOUT,
					Expo.IN, Expo.OUT, Expo.INOUT,
					Back.IN, Back.OUT, Back.INOUT,
					Bounce.IN, Bounce.OUT, Bounce.INOUT,
					Elastic.IN, Elastic.OUT, Elastic.INOUT
			};
		}

		for (TweenEquation easing : easings) {
			if (easingName.equals(easing.toString()))
				return easing;
		}

		return null;
	}
}
