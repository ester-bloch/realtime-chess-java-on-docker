package events.soundEvents;

import utils.LogUtils;

import javax.sound.sampled.*;

import events.EEventType;
import events.GameEvent;
import events.IEventListener;
import pieces.EPieceType;
import pieces.Position;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Plays movement sounds when movement events occur
 * Uses the actual audio files from the Audio resources directory
 */
public class MovementSoundPlayer implements IEventListener2<MovementEvent> , IEventListener{
    
    private static final Map<String, Clip> audioCache = new HashMap<>();
    private static final Random random = new Random();
    
    @Override
    public void onEvent(MovementEvent event) {
        // if (event.getPhase() == MovementEvent.MovementPhase.STARTED) {
            String soundFile = getSoundFileForMovement(event);
            playSoundAsync(soundFile);
            
            LogUtils.logDebug("Playing movement sound: " + soundFile + 
                             " for " + event.getPieceType() + " " + event.getMovementType());
        // }
    }
    
    @Override
    public Class<MovementEvent> getEventType() {
        return MovementEvent.class;
    }
    
    /**
     * Determines which sound file to play based on the movement event
     */
    private String getSoundFileForMovement(MovementEvent event) {
        switch (event.getMovementType()) {
            case MOVE:
                // Use foot step sounds for regular movement
                // return random.nextBoolean() ? "5movement0.wav" : "/Audio/foot_step_2.mp3";
                return  "/Audio/5movement0.wav";
            case JUMP:
                // Use jump sound for jumps
                return "/Audio/jump.wav";
            case SHORT_REST:
            case LONG_REST:
                // Use applause for rest completion
                return "/Audio/applause.mp3";
            default:
                // Default movement sound
                return "/Audio/5movement0.wav";
        }
    }
    
    /**
     * Plays sound asynchronously to avoid blocking the game thread
     */
    private void playSoundAsync(String soundFile) {
        new Thread(() -> {
            try {
                playSound(soundFile);
            } catch (Exception e) {
                System.err.println("Failed to play sound: " + soundFile + " - " + e.getMessage());
                LogUtils.logDebug("Failed to play sound: " + soundFile + " - " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Actually plays the sound using Java Sound API
     */
    private void playSound(String soundFile) throws Exception {
        System.out.println(soundFile);
        // Check cache first for better performance
        Clip clip = audioCache.get(soundFile);
        
        if (clip == null) {
            // Load sound from resources
            InputStream audioStream = getClass().getResourceAsStream(soundFile);
            if (audioStream == null) {
                throw new RuntimeException("Sound file not found: " + soundFile);
            }
            
            BufferedInputStream bufferedStream = new BufferedInputStream(audioStream);
            AudioInputStream audioInputStream;
            
            // Handle different audio formats
            try {
                audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);
            } catch (UnsupportedAudioFileException e) {
                // If direct loading fails, try to convert the format
                AudioInputStream originalStream = AudioSystem.getAudioInputStream(bufferedStream);
                AudioFormat originalFormat = originalStream.getFormat();
                
                // Convert to PCM format for better compatibility
                AudioFormat pcmFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    originalFormat.getSampleRate(),
                    16,
                    originalFormat.getChannels(),
                    originalFormat.getChannels() * 2,
                    originalFormat.getSampleRate(),
                    false
                );
                
                audioInputStream = AudioSystem.getAudioInputStream(pcmFormat, originalStream);
            }
            
            // Create and configure clip
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            
            // Cache the clip for reuse (but limit cache size)
            if (audioCache.size() < 10) {
                audioCache.put(soundFile, clip);
            }
        }
        
        // Play the sound
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0); // Reset to beginning
        clip.start();
        
        LogUtils.logDebug("Successfully played sound: " + soundFile);
    }
    
    /**
     * Cleanup method to release audio resources
     */
    public void cleanup() {
        for (Clip clip : audioCache.values()) {
            if (clip != null && clip.isOpen()) {
                clip.close();
            }
        }
        audioCache.clear();
        LogUtils.logDebug("MovementSoundPlayer cleanup completed");
    }
    
    /**
     * Manually play a specific sound for testing
     */
    public void testPlaySound(String soundFile) {
        playSoundAsync(soundFile);
    }

    @Override
    public void onEvent(GameEvent event) {
        onEvent(new MovementEvent(EPieceType.B, new Position(0,0), new Position(1,1)));

    }
}
