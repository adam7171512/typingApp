package pl.edu.pja.s28687.typingapp;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

public class HotkeyController implements EventHandler<KeyEvent> {

    private WordManager wordManager;

    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public void setWordManager(WordManager wordManager) {
        this.wordManager = wordManager;
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        System.out.println(pressedKeys);
        if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            pressedKeys.add(keyEvent.getCode());
        } else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
            pressedKeys.remove(keyEvent.getCode());
        }
        if (pressedKeys.containsAll(Set.of(KeyCode.TAB, KeyCode.ENTER))) {
            wordManager.restartGame();
        }
        else if (wordManager.inGame() && pressedKeys.containsAll(Set.of(KeyCode.CONTROL, KeyCode.SHIFT, KeyCode.P))){
            wordManager.setPaused(!wordManager.isPaused());
        }
        else if (wordManager.inGame() && pressedKeys.contains(KeyCode.ESCAPE)){
            this.wordManager.finishTest();
        }
    }
}
