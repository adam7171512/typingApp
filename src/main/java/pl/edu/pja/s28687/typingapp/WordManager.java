package pl.edu.pja.s28687.typingapp;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WordManager {
    private final TextView view;
    private final List<Word> currentWords;
    private final List<Word> completedWords;
    private String text;
    private Iterator<Word> iterator;
    private Word currentWord;
    private int charIndex = 0;
    private int wordIndex = 0;
    private int gameTime = 30;
    private int timeLeft;
    private boolean paused = false;
    private ScheduledExecutorService executorService;
    private GameView gameView;

    public WordManager(GameView gameView, TextView view) {
        this.gameView = gameView;
        this.view = view;
        this.currentWords = new ArrayList<>();
        this.completedWords = new ArrayList<>();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void restartGame(){
        clear();
        start();
    }

    public void start(){
        StringBuilder sb = new StringBuilder();
        LanguageController.get30Words().forEach(word -> sb.append(word).append(" "));
        setText(sb.toString());
        startCountDown();
    }

    private void startCountDown(){
        timeLeft = gameTime;
        executorService.scheduleAtFixedRate(() -> {
            if (paused) return;
            timeLeft--;
            Platform.runLater(() -> gameView.updateTime(timeLeft));
//            view.setTimeLeft(timeLeft);
            if (timeLeft == 0){
                Platform.runLater(this::finishTest);
                executorService.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void setText(String text){
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

    private void notifyView(){
        view.setCurrentWordIndex(wordIndex);
        view.setCurrentCharIndex(charIndex);
        view.setWords(currentWords);
    }

    public ClassifiedChar addCharacter(char c){
        charIndex++;
        ClassifiedChar classifiedChar =  currentWord.addChar(c);
        notifyView();
        return classifiedChar;
    }

    public void removeCharacter(){
        if (charIndex > 0)
        {
            charIndex--;
        }
        currentWord.removeLastChar();
        notifyView();
    }

    public void nextWord(){
        currentWord.complete();

        if (iterator.hasNext()){
            wordIndex++;
            charIndex = 0;
            currentWord = iterator.next();
            currentWord.start();
            notifyView();
        }
        else {
            notifyView();
            view.newFeed();
//            TestResult testResult = new TestResult(words);
//            view.testFinished(testResult);
//            String resultsToSave = testResult.getWordsWPMs();
//
//            try {
//                BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt", false));
//                writer.write(resultsToSave);
//                writer.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
            completedWords.addAll(currentWords);
            currentWords.clear();
            StringBuilder sb = new StringBuilder();
            LanguageController.get30Words().forEach(word -> sb.append(word).append(" "));
            setText(sb.toString());
        }
    }

    private void clear(){
        currentWords.clear();
        completedWords.clear();
        if (! executorService.isShutdown()){
            executorService.shutdown();
        }
        charIndex = 0;
        wordIndex = 0;
        timeLeft = 0;
        executorService = Executors.newSingleThreadScheduledExecutor();
        view.clear();
        paused = false;
    }

    private double getAccuracy() {
        double sum = 0;
        for (Word w : currentWords){
            sum += w.getAccuracy();
        }
        return sum / currentWords.size();
    }


    private int getInputCharacters(){
        int sum = 0;
        for (Word w : currentWords){
            sum += w.getInputChars();
        }
        return sum;
    }

    private int getCorrectCharacters(){
        int sum = 0;
        for (Word w : currentWords){
            sum += w.getCorrectChars();
        }
        return sum;
    }

    private int getIncorrectCharacters(){
        int sum = 0;
        for (Word w : currentWords){
            sum += w.getIncorrectChars();
        }
        return sum;
    }

    private int getMissedCharacters(){
        int sum = 0;
        for (Word w : currentWords){
            sum += w.getMissedChars();
        }
        return sum;
    }

    private int getExtraCharacters(){
        int sum = 0;
        for (Word w : currentWords){
            sum += w.getExtraChars();
        }
        return sum;
    }

    private LineChart<Number, Number> buildChartData(){
        double time = 0;
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number,Number> lineChart =
                new LineChart<>(xAxis, yAxis);
        XYChart.Series<Number, Number> wpmSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> avgWpmSeries = new XYChart.Series<>();
        xAxis.setLabel("Time");
        wpmSeries.setName("WPM");
        avgWpmSeries.setName("Avg WPM");
        wpmSeries.getData().add(new XYChart.Data<>(time, 0));
        int n = 0;
        for (Word w : currentWords){
            time += w.getTime() / 1000.0;
            wpmSeries.getData().add(new XYChart.Data<>(time, w.getWPM()));
            avgWpmSeries.getData().add(new XYChart.Data<>(time, ++n * 60 / time));
        }

        lineChart.getData().add(wpmSeries);
        lineChart.getData().add(avgWpmSeries);
        return lineChart;
    }

    public void finishTest(){
        if (! executorService.isShutdown()){
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
        System.out.println("setting game time to " + time);
        gameTime = time;
    }

    public boolean inGame() {
        return timeLeft > 0;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        gameView.setPaused(paused);

    }

    public boolean isPaused() {
        return paused;
    }
}
