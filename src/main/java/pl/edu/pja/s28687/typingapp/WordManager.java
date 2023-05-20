package pl.edu.pja.s28687.typingapp;

import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WordManager {
    private final TextView view;
    private List<Word> words;
    private String text;
    private Iterator<Word> iterator;
    private Word currentWord;
    private int charIndex = 0;
    private int wordIndex = 0;

    public WordManager(TextView view) {
        this.view = view;
        this.words = new ArrayList<>();
    }

    public void setText(String text){
        this.text = text;
        processText();
        iterator = words.iterator();
        currentWord = iterator.next();
    }

    private void processText() {
        List<String> words = List.of(text.split(" "));
        for (String word : words) {
            Word w = new Word(word);
            this.words.add(w);
        }
        notifyView();
    }

    private void notifyView(){
        view.setCurrentWordIndex(wordIndex);
        view.setCurrentCharIndex(charIndex);
        view.setWords(words);
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
        wordIndex++;
        charIndex = 0;
        if (iterator.hasNext()){
            currentWord = iterator.next();
            currentWord.start();
            notifyView();
        }
        else {
            TestResult testResult = new TestResult(words);
            view.testFinished(testResult);
//            System.out.println("Current WPM : " + currentWord.getWPM());
//            System.out.println("Current Accuracy : " + currentWord.getAccuracy());
//            System.out.println("text wpm : " + getWPM());
//            System.out.println("text accuracy : " + getAccuracy());
//            System.out.println("input chars : " + getInputCharacters());
//            System.out.println("correct chars : " + getCorrectCharacters());
//            System.out.println("correct chars % " + getCorrectCharacters() * 100 / (double) (getMissedCharacters() + getInputCharacters()));
//            System.out.println("incorrect chars : " + getIncorrectCharacters());
//            System.out.println("missed chars : " + getMissedCharacters());
//            System.out.println("extra chars : " + getExtraCharacters());
//            view.sendChart(buildChartData());
        }

    }

    private double getAccuracy() {
        double sum = 0;
        for (Word w : words){
            sum += w.getAccuracy();
        }
        return sum / words.size();
    }


    private int getInputCharacters(){
        int sum = 0;
        for (Word w : words){
            sum += w.getInputChars();
        }
        return sum;
    }

    private int getCorrectCharacters(){
        int sum = 0;
        for (Word w : words){
            sum += w.getCorrectChars();
        }
        return sum;
    }

    private int getIncorrectCharacters(){
        int sum = 0;
        for (Word w : words){
            sum += w.getIncorrectChars();
        }
        return sum;
    }

    private int getMissedCharacters(){
        int sum = 0;
        for (Word w : words){
            sum += w.getMissedChars();
        }
        return sum;
    }

    private int getExtraCharacters(){
        int sum = 0;
        for (Word w : words){
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
        for (Word w : words){
            time += w.getTime() / 1000.0;
            wpmSeries.getData().add(new XYChart.Data<>(time, w.getWPM()));
            avgWpmSeries.getData().add(new XYChart.Data<>(time, ++n * 60 / time));
        }

        lineChart.getData().add(wpmSeries);
        lineChart.getData().add(avgWpmSeries);
        return lineChart;
    }
}
