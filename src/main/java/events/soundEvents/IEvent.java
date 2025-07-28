package events.soundEvents;

public interface IEvent {
    /**
     * Gets the timestamp when the event occurred
     * 
     * @return timestamp in nanoseconds
     */
    long getTimestamp();

    /**
     * Gets the type of event
     * 
     * @return event type as string
     */
    String getEventType();
}
