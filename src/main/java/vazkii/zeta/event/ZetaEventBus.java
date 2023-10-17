package vazkii.zeta.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

/**
 * A slightly unusual event bus.
 * When firing an event, Zeta will look for the first superclass that *directly* implements IZetaEvent.
 * Say you have this structure:
 *
 * <pre>
 * abstract class MyEvent implements IZetaEvent {
 *   void register(); //or whatever
 * }
 * class ForgeMyEvent extends MyEvent {
 *   //impl for Forge
 * }
 * class FabricMyEvent extends MyEvent {
 *   //impl for Fabric
 * }
 * </pre>
 *
 * Subscribers are only permitted to @ZetaSubscribe to MyEvent, because it directly implements IZetaEvent.
 * Subscribers are *not* permitted to @ZetaSubscribe to ForgeMyEvent. An exception will be thrown.
 * However, if you call .fire() using a ForgeMyEvent, listeners for MyEvent will be invoked.
 * This allows events to be backed by different classes on different platforms.
 * <p>
 * TODO: There is currently no support for generic events (like forge's RegistryEvent<T>). It would be nice.
 *
 * TODO: It'd also be nice to subscribe to non-direct implementors, for loader-only events
 */
public class ZetaEventBus {
	private final Map<Class<? extends IZetaEvent>, List<MethodHandle>> listeners = new HashMap<>();
	private final Map<Class<?>, Class<? extends IZetaEvent>> directImplCache = new HashMap<>();

	/**
	 * Subscribes all non-static methods on this object (and its superclasses) to the event bus.
	 * Note that the event bus will permanently hold a reference to this object.
	 */
	public ZetaEventBus register(Object receiver) {
		register(receiver, receiver.getClass());
		return this;
	}

	/**
	 * Subscribes all static methods inside this class (and its superclasses) to the event bus.
	 */
	public ZetaEventBus register(Class<?> clazz) {
		register(null, clazz);
		return this;
	}

	public <T extends IZetaEvent> T fire(T event) {
		Class<? extends IZetaEvent> directImpl = directImplCache.computeIfAbsent(event.getClass(), this::findDirectImpl);
		List<MethodHandle> handles = listeners.get(directImpl);
		if(handles != null)
			doFire(event, handles);

		return event;
	}

	private void register(@Nullable Object receiver, Class<?> clazz) {
		Class<?> cursor = clazz;
		boolean nullReceiver = receiver == null;

		do {
			for(Method method : cursor.getDeclaredMethods()) {
				if(!method.isAnnotationPresent(ZetaSubscribe.class))
					continue;

				boolean isStatic = (method.getModifiers() & Modifier.STATIC) != 0;
				if(isStatic == nullReceiver)
					addListener(receiver, method);
			}
			cursor = cursor.getSuperclass();
		} while(cursor != null && cursor != Object.class);
	}

	private void addListener(@Nullable Object receiver, Method method) {
		if(method.getParameterCount() != 1)
			problem("Method annotated with @ZetaSubscribe should take one parameter.", receiver, method, null);

		Class<?> eventType = method.getParameterTypes()[0];
		if(!Arrays.asList(eventType.getInterfaces()).contains(IZetaEvent.class))
			problem("Method annotated with @ZetaSubscribe should take a /direct/ implementor of IZetaEvent.", receiver, method, null);

		@SuppressWarnings("unchecked")
		Class<? extends IZetaEvent> checkedEventType = (Class<? extends IZetaEvent>) eventType;

		MethodHandle handle;
		try {
			handle = MethodHandles.publicLookup().unreflect(method);
			if(receiver != null) handle = handle.bindTo(receiver);
		} catch (Exception e) {
			problem("Exception unreflecting a @ZetaSubscribe method, is it public?", receiver, method, e);
			handle = null; //javac doesn't know problem() diverges
		}

		listeners.computeIfAbsent(checkedEventType, __ -> new ArrayList<>()).add(handle);
	}

	private void problem(String problem, @Nullable Object receiver, Method method, @Nullable Throwable cause) {
		throw new RuntimeException("%s%nReceiver:%s%nMethod class:%s%nMethod name:%s".formatted(
			problem, receiver, method.getDeclaringClass().getName(), method.getName()), cause);
	}

	@SuppressWarnings("unchecked")
	private Class<? extends IZetaEvent> findDirectImpl(Class<?> clazz) {
		Class<?> cursor = clazz;
		do {
			if(Arrays.asList(cursor.getInterfaces()).contains(IZetaEvent.class))
				return (Class<? extends IZetaEvent>) cursor;

			cursor = cursor.getSuperclass();
		} while(cursor != null && cursor != Object.class);

		throw new RuntimeException("Class " + clazz.getName() + " does not implement ZetaEvent");
	}

	private void doFire(IZetaEvent event, List<MethodHandle> handles) {
		if(event instanceof Cancellable cancellable)
			doFireCancellable(cancellable, handles);
		else
			doFireNonCancellable(event, handles);
	}

	private void doFireCancellable(Cancellable event, List<MethodHandle> handles) {
		try {
			for(MethodHandle handle : handles) {
				handle.invoke(event);
				if(event.isCancelled()) break;
			}
		} catch (Throwable e) {
			throw new RuntimeException("Exception while firing event " + event + ": ", e);
		}
	}

	private void doFireNonCancellable(IZetaEvent event, List<MethodHandle> handles) {
		try {
			for(MethodHandle handle : handles)
				handle.invoke(event);
		} catch (Throwable e) {
			throw new RuntimeException("Exception while firing event " + event + ": ", e);
		}
	}
}
