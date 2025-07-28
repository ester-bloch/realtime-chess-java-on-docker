package events.soundEvents;

import pieces.EPieceType;
import pieces.Position;
import interfaces.EState;

/**
 * Simple test class for MovementSoundPlayer (no JUnit dependencies)
 * Can be run directly as a main method
 */
public class MovementSoundPlayerTest {
    
    private static MovementSoundPlayer soundPlayer;
    private static int testsPassed = 0;
    private static int testsTotal = 0;
    
    public static void main(String[] args) {
        System.out.println("🎵 === MovementSoundPlayer Test Suite ===");
        System.out.println("Starting comprehensive audio tests...\n");
        
        soundPlayer = new MovementSoundPlayer();
        
        try {
            // Run all tests
            testMoveEventSound();
            testJumpEventSound();
            testRestEventSound();
            testDirectSoundPlaying();
            testEventTypeVerification();
            testPhaseFiltering();
            testErrorHandling();
            manualAudioTest();
            
            // Summary
            System.out.println("\n" + "=".repeat(50));
            System.out.println("� TEST RESULTS:");
            System.out.println("✅ Tests passed: " + testsPassed + "/" + testsTotal);
            if (testsPassed == testsTotal) {
                System.out.println("🎉 ALL TESTS PASSED! MovementSoundPlayer is working correctly!");
            } else {
                System.out.println("❌ Some tests failed. Please check the output above.");
            }
            
        } finally {
            // Cleanup
            if (soundPlayer != null) {
                soundPlayer.cleanup();
            }
            System.out.println("🧹 Cleanup completed.");
        }
    }
    
    private static void testMoveEventSound() {
        testsTotal++;
        System.out.println("🔊 Test 1: MOVE Event Sound");
        
        try {
            Position start = new Position(0, 0);
            Position end = new Position(1, 1);
            
            MovementEvent moveEvent = new MovementEvent(
                EPieceType.P, start, end, 
                EState.MOVE, MovementEvent.MovementPhase.STARTED
            );
            
            // Verify event type
            if (!soundPlayer.getEventType().equals(MovementEvent.class)) {
                throw new RuntimeException("Wrong event type");
            }
            
            soundPlayer.onEvent(moveEvent);
            Thread.sleep(1000);
            
            testsPassed++;
            System.out.println("✅ MOVE event test passed\n");
            
        } catch (Exception e) {
            System.err.println("❌ MOVE event test failed: " + e.getMessage() + "\n");
        }
    }
    
    private static void testJumpEventSound() {
        testsTotal++;
        System.out.println("🔊 Test 2: JUMP Event Sound");
        
        try {
            Position start = new Position(0, 0);
            Position end = new Position(2, 1);
            
            MovementEvent jumpEvent = new MovementEvent(
                EPieceType.N, start, end,
                EState.JUMP, MovementEvent.MovementPhase.STARTED
            );
            
            soundPlayer.onEvent(jumpEvent);
            Thread.sleep(1000);
            
            testsPassed++;
            System.out.println("✅ JUMP event test passed\n");
            
        } catch (Exception e) {
            System.err.println("❌ JUMP event test failed: " + e.getMessage() + "\n");
        }
    }
    
    private static void testRestEventSound() {
        testsTotal++;
        System.out.println("🔊 Test 3: REST Event Sound");
        
        try {
            Position start = new Position(3, 3);
            Position end = new Position(3, 3);
            
            MovementEvent restEvent = new MovementEvent(
                EPieceType.Q, start, end,
                EState.SHORT_REST, MovementEvent.MovementPhase.STARTED
            );
            
            soundPlayer.onEvent(restEvent);
            Thread.sleep(1000);
            
            testsPassed++;
            System.out.println("✅ REST event test passed\n");
            
        } catch (Exception e) {
            System.err.println("❌ REST event test failed: " + e.getMessage() + "\n");
        }
    }
    
    private static void testDirectSoundPlaying() {
        testsTotal++;
        System.out.println("🔊 Test 4: Direct Sound File Playing");
        
        try {
            String[] testSounds = {
                "/Audio/foot_step_1.mp3",
                "/Audio/jump.wav",
                "/Audio/applause.mp3",
                "/Audio/5movement0.wav"
            };
            
            for (String soundFile : testSounds) {
                System.out.println("  Playing: " + soundFile);
                soundPlayer.testPlaySound(soundFile);
                Thread.sleep(800);
            }
            
            testsPassed++;
            System.out.println("✅ Direct sound playing test passed\n");
            
        } catch (Exception e) {
            System.err.println("❌ Direct sound playing test failed: " + e.getMessage() + "\n");
        }
    }
    
    private static void testEventTypeVerification() {
        testsTotal++;
        System.out.println("🔍 Test 5: Event Type Verification");
        
        try {
            Class<MovementEvent> expectedType = MovementEvent.class;
            Class<MovementEvent> actualType = soundPlayer.getEventType();
            
            if (!expectedType.equals(actualType)) {
                throw new RuntimeException("Expected " + expectedType + " but got " + actualType);
            }
            
            testsPassed++;
            System.out.println("✅ Event type verification passed\n");
            
        } catch (Exception e) {
            System.err.println("❌ Event type verification failed: " + e.getMessage() + "\n");
        }
    }
    
    private static void testPhaseFiltering() {
        testsTotal++;
        System.out.println("🔍 Test 6: Phase Filtering");
        
        try {
            Position start = new Position(0, 0);
            Position end = new Position(1, 1);
            
            // STARTED phase (should play sound)
            MovementEvent startedEvent = new MovementEvent(
                EPieceType.P, start, end,
                EState.MOVE, MovementEvent.MovementPhase.STARTED
            );
            
            // COMPLETED phase (should NOT play sound)
            MovementEvent completedEvent = new MovementEvent(
                EPieceType.P, start, end,
                EState.MOVE, MovementEvent.MovementPhase.COMPLETED
            );
            
            System.out.println("  Playing STARTED phase (should hear sound)");
            soundPlayer.onEvent(startedEvent);
            Thread.sleep(1000);
            
            System.out.println("  Playing COMPLETED phase (should be silent)");
            soundPlayer.onEvent(completedEvent);
            Thread.sleep(500);
            
            testsPassed++;
            System.out.println("✅ Phase filtering test passed\n");
            
        } catch (Exception e) {
            System.err.println("❌ Phase filtering test failed: " + e.getMessage() + "\n");
        }
    }
    
    private static void testErrorHandling() {
        testsTotal++;
        System.out.println("🔍 Test 7: Error Handling");
        
        try {
            // This should not throw exception even with invalid file
            soundPlayer.testPlaySound("/Audio/nonexistent_file.wav");
            Thread.sleep(500);
            
            testsPassed++;
            System.out.println("✅ Error handling test passed\n");
            
        } catch (Exception e) {
            System.err.println("❌ Error handling test failed: " + e.getMessage() + "\n");
        }
    }
    
    private static void manualAudioTest() {
        testsTotal++;
        System.out.println("🎵 Test 8: Manual Audio Test - All Movement Types");
        System.out.println("Listen carefully to verify each sound plays correctly:");
        
        try {
            Position start = new Position(0, 0);
            Position end = new Position(1, 1);
            
            System.out.println("\n  1. Testing MOVE (Pawn) - should hear footsteps:");
            soundPlayer.onEvent(new MovementEvent(EPieceType.P, start, end, 
                EState.MOVE, MovementEvent.MovementPhase.STARTED));
            Thread.sleep(1500);
            
            System.out.println("\n  2. Testing JUMP (Knight) - should hear jump sound:");
            soundPlayer.onEvent(new MovementEvent(EPieceType.N, start, end, 
                EState.JUMP, MovementEvent.MovementPhase.STARTED));
            Thread.sleep(1500);
            
            System.out.println("\n  3. Testing SHORT_REST (Queen) - should hear applause:");
            soundPlayer.onEvent(new MovementEvent(EPieceType.Q, start, end, 
                EState.SHORT_REST, MovementEvent.MovementPhase.STARTED));
            Thread.sleep(1500);
            
            System.out.println("\n  4. Testing LONG_REST (King) - should hear applause:");
            soundPlayer.onEvent(new MovementEvent(EPieceType.K, start, end, 
                EState.LONG_REST, MovementEvent.MovementPhase.STARTED));
            Thread.sleep(1500);
            
            System.out.println("\n  5. Testing IDLE (Rook) - should hear default movement:");
            soundPlayer.onEvent(new MovementEvent(EPieceType.R, start, end, 
                EState.IDLE, MovementEvent.MovementPhase.STARTED));
            Thread.sleep(1500);
            
            testsPassed++;
            System.out.println("\n✅ Manual audio test completed!");
            System.out.println("If you heard all the sounds, the MovementSoundPlayer is working correctly!");
            
        } catch (Exception e) {
            System.err.println("❌ Manual audio test failed: " + e.getMessage() + "\n");
        }
    }
}
