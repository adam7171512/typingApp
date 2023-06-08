package pl.edu.pja.s28687.typingapp.model;

import javafx.application.Platform;
import pl.edu.pja.s28687.typingapp.controllers.LanguageController;
import pl.edu.pja.s28687.typingapp.view.GameView;
import pl.edu.pja.s28687.typingapp.view.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class that coordinates the game. It keeps track of current words loaded, current word, passes input to the current
 * word and updates the view.
 */
public class WordManager {
    private final TextView view;
    private final List<Word> currentWords;
    private final List<Word> completedWords;
    private final GameView gameView;
    private String text;
    private Iterator<Word> iterator;
    private Word currentWord;
    private int charIndex = 0;
    private int wordIndex = 0;
    private int gameTime = 30;
    private int timeLeft;
    private boolean paused = false;
    private ScheduledExecutorService executorService;

    public WordManager(GameView gameView, TextView view) {
        this.gameView = gameView;
        this.view = view;
        this.currentWords = new ArrayList<>();
        this.completedWords = new ArrayList<>();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void restartGame() {
        clear();
        start();
    }

    public void start() {
        StringBuilder sb = new StringBuilder();
        LanguageController.get30Words().forEach(word -> sb.append(word).append(" "));
        setText(sb.toString());
        startCountDown();
    }

    private void startCountDown() {
        timeLeft = gameTime;
        executorService.scheduleAtFixedRate(() -> {
            if (paused) return;
            timeLeft--;
            Platform.runLater(() -> gameView.updateTime(timeLeft));
            if (timeLeft == 0) {
                Platform.runLater(this::finishTest);
                executorService.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void setText(String text) {
        this.text = text;
        wordIndex = 0;
        charIndex = 0;

        processText();
        iterator = currentWords.iterator();
        currentWord = iterator.next();
    }

    private void processText() {
        List<String> words = List.of(text.split(" "));
        for (String word : words) {
            Word w = new Word(word);
            this.currentWords.add(w);
        }
        notifyView();
    }

    private void notifyView() {
        view.setCurrentWordIndex(wordIndex);
        view.setCurrentCharIndex(charIndex);
        view.setTextFlowWords(currentWords);
    }

    public ClassifiedChar addCharacter(char c) {
        charIndex++;
        ClassifiedChar classifiedChar = currentWord.addChar(c);
        notifyView();
        return classifiedChar;
    }

    public void removeCharacter() {
        if (charIndex > 0) {
            charIndex--;
        }
        currentWord.removeLastChar();
        notifyView();
    }

    public void nextWord() {
        currentWord.complete();

        if (iterator.hasNext()) {
            wordIndex++;
            charIndex = 0;
            currentWord = iterator.next();
            currentWord.start();
            notifyView();
        } else {
            notifyView();
            view.newFeed();
            completedWords.addAll(currentWords);
            currentWords.clear();
            StringBuilder sb = new StringBuilder();
            LanguageController.get30Words().forEach(word -> sb.append(word).append(" "));
            setText(sb.toString());
        }
    }

    private void clear() {
        currentWords.clear();
        completedWords.clear();
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
        charIndex = 0;
        wordIndex = 0;
        timeLeft = 0;
        executorService = Executors.newSingleThreadScheduledExecutor();
        view.clear();
        paused = false;
    }

    public void finishTest() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
        timeLeft = 0;
        List<Word> currentlyCompletedWords = new ArrayList<>();
        currentlyCompletedWords.addAll(completedWords);
        currentlyCompletedWords.addAll(currentWords.stream().filter(w -> w.getFinishTime() != 0).toList());
        TestResult testResult = new TestResult(currentlyCompletedWords);
        this.currentWords.clear();
        this.completedWords.clear();

        view.testFinished(testResult);
        testResult.saveWpmReport();
    }

    public void setGameTime(Integer time) {
        gameTime = time;
    }

    public boolean inGame() {
        return timeLeft > 0;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        gameView.setPaused(paused);

    }
}
