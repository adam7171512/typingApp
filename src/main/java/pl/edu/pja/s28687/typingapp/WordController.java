package pl.edu.pja.s28687.typingapp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class WordController implements ChangeListener<String> {

    private WordManager wordManager;

    public WordController() {
    }

    public void setWordManager(WordManager wordManager) {
        this.wordManager = wordManager;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String oldString, String newString) {
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
