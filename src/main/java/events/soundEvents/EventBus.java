package events.soundEvents;

import utils.LogUtils;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central event bus for publishing and subscribing to events
 * Thread-safe implementation using concurrent collections
 */
public class EventBus {
    private static EventBus instance;
    private final Map<Class<? extends IEvent>, List<IEventListener<? extends IEvent>>> listeners;

    private EventBus() {
        this.listeners = new ConcurrentHashMap<>();
    }

    /**
     * Gets the singleton instance of EventBus
     * 
     * @return the EventBus instance
     */
    public static synchronized EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    /**
     * Subscribes a listener to events of a specific type
     * 
     * @param listener the listener to subscribe
     */
    public <T extends IEvent> void subscribe(IEventListener<T> listener) {
        Class<T> eventType = listener.getEventType();
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add((IEventListener<? extends IEvent>) listener);

        LogUtils.logDebug("Subscribed listener for event type: " + eventType.getSimpleName());
    }

    /**
     * Unsubscribes a listener from events
     * 
     * @param listener the listener to unsubscribe
     */
    public <T extends IEvent> void unsubscribe(IEventListener<T> listener) {
        Class<T> eventType = listener.getEventType();
        List<IEventListener<? extends IEvent>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
            LogUtils.logDebug("Unsubscribed listener for event type: " + eventType.getSimpleName());
        }
    }

    /**
     * Publishes an event to all subscribed listeners
     * 
     * @param event the event to publish
     */
    @SuppressWarnings("unchecked")
    public <T extends IEvent> void publish(T event) {
        Class<? extends IEvent> eventType = event.getClass();
        List<IEventListener<? extends IEvent>> eventListeners = listeners.get(eventType);

        if (eventListeners != null && !eventListeners.isEmpty()) {
            LogUtils.logDebug(
                    "Publishing event: " + event.getEventType() + " to " + eventListeners.size() + " listeners");

            for (IEventListener<? extends IEvent> listener : eventListeners) {
                try {
                    ((IEventListener<T>) listener).onEvent(event);
                } catch (Exception e) {
                    System.err.println("Error processing event " + event.getEventType() + ": " + e.getMessage());
                    LogUtils.logDebug("Error processing event " + event.getEventType() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Gets the number of listeners for a specific event type
     * 
     * @param eventType the event type
     * @return number of listeners
     */
    public int getListenerCount(Class<? extends IEvent> eventType) {
        List<IEventListener<? extends IEvent>> eventListeners = listeners.get(eventType);
        return eventListeners != null ? eventListeners.size() : 0;
    }
}
