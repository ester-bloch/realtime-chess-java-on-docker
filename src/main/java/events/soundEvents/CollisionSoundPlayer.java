package events.soundEvents;

import utils.LogUtils;

public class CollisionSoundPlayer implements IEventListener2<CollisionEvent>  {
    
    @Override
    public void onEvent(CollisionEvent event) {
        String soundFile = getSoundFileForCollision(event);
        playSoundAsync(soundFile);
        
        LogUtils.logDebug("Playing collision sound: " + soundFile + 
                         " for collision between " + event.getPiece1Type() + 
                         " and " + event.getPiece2Type());
    }
    
    @Override
    public Class<CollisionEvent> getEventType() {
        return CollisionEvent.class;
    }
    
    private String getSoundFileForCollision(CollisionEvent event) {
        switch (event.getCollisionType()) {
            case PIECE_TO_PIECE:
                return "/sounds/collision/piece_hit.wav";
    case PIECE_TO_BOARD_EDGE:
                return "/sounds/collision/edge_hit.wav";
            case JUMP_LANDING:
                return "/sounds/collision/jump_land.wav";
            default:
                return "/sounds/collision/default_hit.wav";
        }
    }
    
    private void playSoundAsync(String soundFile) {
        // Implementation using javax.sound.sampled or other audio library
        new Thread(() -> {
            try {
                // TODO: Implement actual sound playing logic
                System.out.println("Playing sound: " + soundFile);
            } catch (Exception e) {
                System.err.println("Failed to play sound: " + soundFile);
                LogUtils.logDebug("Failed to play sound: " + soundFile + " - " + e.getMessage());
            }
        }).start();
    }
}
