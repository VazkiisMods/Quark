package aurelienribon.tweenengine;

import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.equations.Sine;

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
