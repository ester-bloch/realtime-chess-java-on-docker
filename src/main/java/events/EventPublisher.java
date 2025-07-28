package events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventPublisher {
    private static final EventPublisher instance = new EventPublisher();

    private final Map<EEventType, List<IEventListener>> listenersMap = new HashMap<>();

    private EventPublisher() {}

    public static EventPublisher getInstance() {
        return instance;
    }

    public void subscribe(EEventType topic, IEventListener listener) {
        listenersMap.computeIfAbsent(topic, k -> new ArrayList<>()).add(listener);
    }

    public void unsubscribe(EEventType topic, IEventListener listener) {
        List<IEventListener> listeners = listenersMap.get(topic);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                listenersMap.remove(topic);
            }
        }
    }

    public void publish(EEventType topic, GameEvent event) {
        List<IEventListener> listeners = listenersMap.get(topic);
        if (listeners != null) {
            for (IEventListener listener : listeners) {
                listener.onEvent(event);
            }
        }
    }
}
