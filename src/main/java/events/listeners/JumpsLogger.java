package events.listeners;

import events.EEventType;
import events.GameEvent;
import events.IEventListener;

public class JumpsLogger implements IEventListener {
    @Override
    public void onEvent(GameEvent event) {
        if (event.type.equals(EEventType.PIECE_JUMP)) {
            String jumpDescription = (String) event.data;
            System.out.println("Move: " + jumpDescription);
        }
    }
}
