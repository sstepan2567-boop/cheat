package com.cheat.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal reflection based event bus. Listeners are registered per object; every method
 * annotated with {@link EventHandler} that takes a single {@link Event} subtype is invoked
 * when an event of that type is posted.
 *
 * <p>The class hierarchy is walked so that an overridden base handler (for example
 * {@code Module.onTick}) is still discovered and dispatches to the override.
 */
public final class EventBus {

    private record Listener(Object target, Method method) {}

    private final Map<Class<?>, List<Listener>> listeners = new ConcurrentHashMap<>();

    public void register(Object target) {
        Class<?> clazz = target.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(EventHandler.class)) {
                    continue;
                }
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length != 1 || !Event.class.isAssignableFrom(parameters[0])) {
                    continue;
                }
                method.setAccessible(true);
                listeners.computeIfAbsent(parameters[0], type -> new ArrayList<>())
                        .add(new Listener(target, method));
            }
            clazz = clazz.getSuperclass();
        }
    }

    public void unregister(Object target) {
        listeners.values().forEach(list -> list.removeIf(listener -> listener.target() == target));
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> T post(T event) {
        List<Listener> registered = listeners.get(event.getClass());
        if (registered == null) {
            return event;
        }
        for (Listener listener : new ArrayList<>(registered)) {
            // Disabled modules do not receive events; infrastructure modules always do.
            if (listener.target() instanceof com.cheat.module.Module module
                    && !module.isInfrastructure() && !module.isEnabled()) {
                continue;
            }
            try {
                listener.method().invoke(listener.target(), event);
            } catch (ReflectiveOperationException | RuntimeException exception) {
                System.err.println("[cheat] listener " + listener.method().getDeclaringClass().getName()
                        + "." + listener.method().getName() + " failed: " + exception);
            }
        }
        return event;
    }
}
