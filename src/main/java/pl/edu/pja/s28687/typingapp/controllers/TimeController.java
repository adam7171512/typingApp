package pl.edu.pja.s28687.typingapp.controllers;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import pl.edu.pja.s28687.typingapp.model.WordManager;

public class TimeController implements ChangeListener<Integer>, InvalidationListener {

    private WordManager wordManager;

    public void setWordManager(WordManager wordManager) {
        this.wordManager = wordManager;
    }

    @Override
    public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
        System.out.println("TimeController: " + t1);
        wordManager.setGameTime(t1);
    }

    @Override
    public void invalidated(Observable observable) {
    }
}
