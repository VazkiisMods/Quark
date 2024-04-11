package org.violetmoon.quark.base.util;

/**
 * @author WireSegal
 *         Created at 11:08 AM on 8/25/19.
 */
@FunctionalInterface
public interface TriFunction<R, T, U, V> {
	R apply(T t, U u, V v);
}
