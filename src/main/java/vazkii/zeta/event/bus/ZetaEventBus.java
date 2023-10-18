package vazkii.zeta.event.bus;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A polymorphic event bus, associated with an "event root" type.
 * <p>
 * Subscribers are only permitted to subscribe to events that *directly* implement the event root.
 * If A implements the event root, and B extends A, you can subscribe to A, but not B.
 * <p>
 * When firing an event, its superclass hierarchy will be walked until locating a direct implementer
 * of the event root interface. Listeners for *that* will be invoked. (If you fire B, it will get
 * passed to listeners looking for A.)
 * <p>
 * This constructions allows you to API/impl split your events.
 * A might be a "common" event, and B might be a Forge-only implementation of the event.
 */
/*
 * TODO: No support for generic events (like forge's RegistryEvent<T>). It would be nice.
 * TODO: No support for Consumer events (like forge's addListener).
 * TODO: It'd also be nice to subscribe to non-direct implementors, for loader-only events
 */
public class ZetaEventBus<E> {
	private final Class<? extends Annotation> subscriberAnnotation;
	private final Class<? extends E> eventRoot;

	private final Map<Class<? extends E>, Listeners> listenerMap = new HashMap<>();
	private final Map<Class<?>, Class<? extends E>> directImplCache = new HashMap<>(); //Optimization for .fire()

	/**
	 * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
	 * @param eventRoot The superinterface of all events fired on this bus.
	 */
	public ZetaEventBus(Class<? extends Annotation> subscriberAnnotation, Class<? extends E> eventRoot) {
		Preconditions.checkArgument(eventRoot.isInterface(), "Event roots should be an interface");

		this.subscriberAnnotation = subscriberAnnotation;
		this.eventRoot = eventRoot;
	}

	/**
	 * If the parameter is a Class: subscribes all static methods from it (and its superclasses) to the event bus.
	 * Otherwise, subscribes all non-static methods on that object (and its superclasses) to the event bus.
	 *   (Note that the event bus will hold a reference to this object.)
	 */
	public ZetaEventBus<E> subscribe(@NotNull Object target) {
		Preconditions.checkNotNull(target, "null passed to subscribe");

		Object receiver;
		Class<?> owningClazz;
		if(target instanceof Class<?> clazz) {
			receiver = null;
			owningClazz = clazz;
		} else {
			receiver = target;
			owningClazz = target.getClass();
		}

		streamAnnotatedMethods(owningClazz, receiver == null).forEach(m -> getListenersFor(m).subscribe(receiver, owningClazz, m));
		return this;
	}

	/**
	 * If the parameter is a Class: unsubscribes all static methods from it (and its superclasses) from the event bus.
	 * Otherwise, unsubscribes all non-static methods on that object (and its superclasses) from the event bus.
	 */
	public ZetaEventBus<E> unsubscribe(@NotNull Object target) {
		Preconditions.checkNotNull(target, "null passed to unsubscribe");

		Object receiver;
		Class<?> owningClazz;
		if(target instanceof Class<?> clazz) {
			receiver = null;
			owningClazz = clazz;
		} else {
			receiver = target;
			owningClazz = target.getClass();
		}

		streamAnnotatedMethods(owningClazz, receiver == null).forEach(m -> getListenersFor(m).unsubscribe(receiver, owningClazz, m));
		return this;
	}

	/**
	 * Fires an event on the event bus. Each subscriber will be visited in order.
	 */
	public <T extends E> T fire(@NotNull T event) {
		Class<? extends E> directImpl = directImplCache.computeIfAbsent(event.getClass(), this::findDirectImpl);
		Listeners subs = listenerMap.get(directImpl);
		if(subs != null)
			subs.doFire(event);

		return event;
	}

	/**
	 * Grabs methods from this class (and its superclasses, recursively) that are annotated with this bus's
	 * annotation; and of the requested staticness.
	 */
	private Stream<Method> streamAnnotatedMethods(Class<?> owningClazz, boolean wantStatic) {
		return Arrays.stream(owningClazz.getMethods())
			.filter(m -> m.isAnnotationPresent(subscriberAnnotation) && ((m.getModifiers() & Modifier.STATIC) != 0) == wantStatic);
	}

	/**
	 * Picks out the "Foo" in "void handleFoo(Foo event)", and gets/creates the Listeners corresponding to that type.
	 */
	@SuppressWarnings("unchecked")
	private Listeners getListenersFor(Method method) {
		if(method.getParameterCount() != 1)
			throw arityERR(method);

		Class<?> eventType = method.getParameterTypes()[0];
		if(!Arrays.asList(eventType.getInterfaces()).contains(eventRoot))
			throw typeERR(method);

		return listenerMap.computeIfAbsent((Class<? extends E>) eventType, __ -> new Listeners());
	}

	/**
	 * Walks the superclass/superinterface hierarchy until finding a class that *directly* implements this bus's event root.
	 * Used to "normalize" events, so firing `FabricMyEvent` will locate subscribers for `MyEvent`.
	 */
	private Class<? extends E> findDirectImpl(Class<?> clazz) {
		return findDirectImpl2(clazz).orElseThrow(() -> findImplERR(clazz));
	}

	@SuppressWarnings("unchecked")
	private Optional<Class<? extends E>> findDirectImpl2(Class<?> clazz) {
		//does it directly implement the interface?
		Set<Class<?>> interfaces = Set.of(clazz.getInterfaces());
		if(interfaces.contains(eventRoot))
			return Optional.of((Class<? extends E>) clazz);

		//recurse into interfaces
		for(Class<?> itf : interfaces) {
			Optional<Class<? extends E>> recurse = findDirectImpl2(itf);
			if(recurse.isPresent())
				return recurse;
		}

		//recurse into superclass
		Class<?> superclass = clazz.getSuperclass();
		if(superclass != null && superclass != Object.class)
			return findDirectImpl2(superclass);
		else
			return Optional.empty();
	}

	/**
	 * Mildly overengineered since I want method dispatching to hopefully be low-overhead... don't mind me
	 * MethodHandle is magic free performance right
	 * Pausefrogeline
	 */
	private class Listeners {
		private record Subscriber(@Nullable Object receiver, Class<?> owningClazz, Method method) {
			@Override
			public boolean equals(Object object) {
				if(this == object) return true;
				if(object == null || getClass() != object.getClass()) return false;
				Subscriber that = (Subscriber) object;
				return receiver == that.receiver && //<-- object identity compare
					Objects.equals(owningClazz, that.owningClazz) &&
					Objects.equals(method, that.method);
			}

			@Override
			public int hashCode() {
				return System.identityHashCode(receiver) + owningClazz.hashCode() + method.hashCode();
			}

			MethodHandle unreflect() {
				MethodHandle handle;
				try {
					handle = MethodHandles.publicLookup().unreflect(method);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				//fill in the "this" parameter
				if(receiver != null)
					handle = handle.bindTo(receiver);
				return handle;
			}
		}

		private final Map<Subscriber, MethodHandle> handles = new LinkedHashMap<>();

		void subscribe(@Nullable Object receiver, Class<?> owningClazz, Method method) {
			try {
				handles.computeIfAbsent(new Subscriber(receiver, owningClazz, method), Subscriber::unreflect);
			} catch (Exception e) {
				throw unreflectERR(method, e);
			}
		}

		void unsubscribe(@Nullable Object receiver, Class<?> owningClazz, Method method) {
			handles.remove(new Subscriber(receiver, owningClazz, method));
		}

		//just hoisting the instanceof out of the loop.. No profiling just vibes <3
		void doFire(E event) {
			try {
				if(event instanceof Cancellable cancellable)
					doFireCancellable(cancellable);
				else
					doFireNonCancellable(event);
			} catch (Throwable e) {
				throw new RuntimeException("Exception while firing event " + event + ": ", e);
			}
		}

		void doFireCancellable(Cancellable event) throws Throwable {
			for(MethodHandle handle : handles.values()) {
				handle.invoke(event);
				if(event.isCancelled()) break;
			}
		}

		void doFireNonCancellable(E event) throws Throwable {
			for(MethodHandle handle : handles.values())
				handle.invoke(event);
		}
	}

	private RuntimeException findImplERR(Class<?> clazz) {
		return new RuntimeException("Couldn't find a direct implementer of " + eventRoot.getName() +
			" in " + clazz.getName() + "'s type hierarchy."); //You might just need to fix findDirectImpl2.
	}

	private RuntimeException arityERR(Method method) {
		return methodProblem("Method annotated with @" + subscriberAnnotation.getSimpleName() +
			" should take 1 parameter.", method, null);
	}

	private RuntimeException typeERR(Method method) {
		return methodProblem("Method annotated with @" + subscriberAnnotation.getSimpleName() +
			" should take a *direct* implementor of " + eventRoot.getSimpleName() + ".", method, null);
	}

	private RuntimeException unreflectERR(Method method, Throwable cause) {
		return methodProblem("Exception unreflecting a @" + subscriberAnnotation.getSimpleName() +
			" method, is it public?", method, cause);
	}

	private static RuntimeException methodProblem(String problem, Method method, @Nullable Throwable cause) {
		return new RuntimeException("%s%nMethod class:%s%nMethod name:%s".formatted(
			problem, method.getDeclaringClass().getName(), method.getName()), cause);
	}
}
