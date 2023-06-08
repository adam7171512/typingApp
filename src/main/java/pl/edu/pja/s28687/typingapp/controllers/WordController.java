package pl.edu.pja.s28687.typingapp.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import pl.edu.pja.s28687.typingapp.model.WordManager;

/**
 * This class is responsible for handling the input from the user.
 * It implements the ChangeListener interface, which allows it to be notified
 * when the input changes. It observes the input field (which is invisible).
 * Based on changes of this field and on state of WordManager object,
 * it decides calls appropriate methods of the WordManager.
 */
public class WordController implements ChangeListener<String> {

    private WordManager wordManager;

    public void setWordManager(WordManager wordManager) {
        this.wordManager = wordManager;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String oldString, String newString) {
        if (!wordManager.inGame() || wordManager.isPaused()) {
            return;
        }
        if (oldString.length() > newString.length()) {
            wordManager.removeCharacter();
            return;
        }
        char c = newString.charAt(newString.length() - 1);
        if (c == ' ') {
            wordManager.nextWord();
            return;
        }
        wordManager.addCharacter(c);
    }
}
