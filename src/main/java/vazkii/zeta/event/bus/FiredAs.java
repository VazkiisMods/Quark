package vazkii.zeta.event.bus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When the annotee is fired on a ZetaEventBus, it will trigger listeners
 * corresponding to the type in this annotation, not itself.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FiredAs {
	Class<?> value();
}
