package events.soundEvents;

public interface IEventListener2<T extends IEvent> {
     /**
     * Called when an event is published
     * @param event the event that occurred
     */
    void onEvent(T event);
    
    /**
     * Gets the type of events this listener handles
     * @return the event class type
     */
    Class<T> getEventType();
}
