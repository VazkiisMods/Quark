package vazkii.zeta.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that ZetaEventBus looks for when calling "subscribe".
 *
 * Targeted methods must be "public" and must take exactly one parameter of a class that *directly* implements IZetaEvent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ZetaSubscribe {
}
