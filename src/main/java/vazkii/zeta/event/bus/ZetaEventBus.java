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
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A polymorphic event bus.
 * <p>
 * If an event type B is annotated with @FiredAs(A), firing B on the event bus
 * will trigger event listeners for A. This is legal if B extends A.
 * <p>
 * This construction allows you to API/impl split your events.
 * here, A might be a "common" event, and B might be a Forge-only implementation of the event.
 */
/*
 * TODO: No support for generic events (like forge's RegistryEvent<T>). It would be nice.
 * TODO: No support for Consumer events (like forge's addListener).
 * TODO: loader-only event subscribing? (subscribe to ForgeZWhatever directly)
 */
public class ZetaEventBus<E> {
	private final Class<? extends Annotation> subscriberAnnotation;
	private final Class<? extends E> eventRoot;
	private final @Nullable Logger logSpam;

	private final Map<Class<? extends E>, Listeners> listenerMap = new HashMap<>();
	private final Map<Class<?>, Class<? extends E>> firedAsCache = new HashMap<>(); //Optimization for .fire()

	/**
	 * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
	 * @param eventRoot The superinterface of all events fired on this bus.
	 */
	public ZetaEventBus(Class<? extends Annotation> subscriberAnnotation, Class<? extends E> eventRoot, @Nullable Logger logSpam) {
		Preconditions.checkArgument(eventRoot.isInterface(), "Event roots should be an interface");

		this.subscriberAnnotation = subscriberAnnotation;
		this.eventRoot = eventRoot;
		this.logSpam = logSpam;
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
		Class<? extends E> firedAs = firedAsCache.computeIfAbsent(event.getClass(), this::getFiredAs);
		Listeners subs = listenerMap.get(firedAs);
		if(subs != null) {
			if(logSpam != null) {
				logSpam.info("Dispatching {} (as {}) to {} listener{}",
					logspamSimpleName(event.getClass()),
					logspamSimpleName(firedAs),
					subs.size(), subs.size() > 1 ? "s" : "");
			}

			subs.doFire(event);
		}

		return event;
	}

	//this is really silly
	private String logspamSimpleName(Class<?> clazz) {
		String[] split = clazz.getName().split("\\.");
		return split[split.length - 1];
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
		if(!eventRoot.isAssignableFrom(eventType))
			throw typeERR(method);

		return listenerMap.computeIfAbsent((Class<? extends E>) eventType, __ -> new Listeners());
	}

	/**
	 * If this event type is @FiredAs something else, gets that type.
	 */
	@SuppressWarnings("unchecked")
	private Class<? extends E> getFiredAs(Class<?> clazz) {
		//hacky fix for "fire(new IEvent() { ... })"
		//note - this can't handle lambdas (arrow functions)
		//apparently it's possible to reify types from lambdas, but Forge needs a
		//whole library to do it... sun.misc.Unsafe is involved... it's a mess
		if(clazz.isAnonymousClass())
			return getFiredAs(clazz.getInterfaces()[0]);

		FiredAs annot = clazz.getAnnotation(FiredAs.class);
		if(annot == null) {
			//safety: this method's only called from fire(), which statically checks this invariant
			return (Class<? extends E>) clazz;
		}

		Class<?> firedAs = annot.value();
		if(!eventRoot.isAssignableFrom(firedAs) || !firedAs.isAssignableFrom(clazz))
			throw weirdFiredAsERR(clazz, firedAs);

		return (Class<? extends E>) firedAs;
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

		int size() {
			return handles.size();
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
				if(event.isCanceled()) break;
			}
		}

		void doFireNonCancellable(E event) throws Throwable {
			for(MethodHandle handle : handles.values())
				handle.invoke(event);
		}
	}

	private RuntimeException weirdFiredAsERR(Class<?> clazz, Class<?> firedAs) {
		return new RuntimeException(firedAs.getName() + " should be a subtype of " + clazz.getName() +
			", which should be a subtype of this bus's event root, " + eventRoot.getName() + ".");
	}

	private RuntimeException arityERR(Method method) {
		return methodProblem("Method annotated with @" + subscriberAnnotation.getSimpleName() +
			" should take 1 parameter.", method, null);
	}

	private RuntimeException typeERR(Method method) {
		return methodProblem("Method annotated with @" + subscriberAnnotation.getSimpleName() +
			" should take an implementor of " + eventRoot.getSimpleName() + ".", method, null);
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
