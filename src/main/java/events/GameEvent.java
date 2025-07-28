package events;

public class GameEvent {
    public final EEventType type;
    public final Object data;

    public GameEvent(EEventType type, Object data) {
        this.type = type;
        this.data = data;
    }
}
