package pl.edu.pja.s28687.typingapp;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TestResult {
    private final List<Word> words;

    public TestResult(List<Word> words){
        this.words = words;
    }

    public double getAccuracy() {
        return getCorrectCharacters() / (double) (getInputCharacters() + getMissedCharacters());
    }

    public int getWPM() {
        BigDecimal sum = BigDecimal.ZERO;
        for (Word w : words){
            sum = sum.add(w.getWPM());
        }
        return (sum.divide(BigDecimal.valueOf(words.size()), RoundingMode.FLOOR)).intValue();
    }

    public int getInputCharacters(){
        int sum = 0;
        for (Word w : words){
            sum += w.getInputChars();
        }
        return sum;
    }

    public int getCorrectCharacters(){
        int sum = 0;
        for (Word w : words){
            sum += w.getCorrectChars();
        }
        return sum;
    }

    public int getIncorrectCharacters(){
        int sum = 0;
        for (Word w : words){
            sum += w.getIncorrectChars();
        }
        return sum;
    }

    public int getMissedCharacters(){
        int sum = 0;
        for (Word w : words){
            sum += w.getMissedChars();
        }
        return sum;
    }

    public int getExtraCharacters(){
        int sum = 0;
        for (Word w : words){
            sum += w.getExtraChars();
        }
        return sum;
    }

    public LineChart<Number, Number> buildChart(){
        double time = 0;
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number,Number> lineChart =
                new LineChart<>(xAxis, yAxis);
        XYChart.Series<Number, Number> wpmSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> avgWpmSeries = new XYChart.Series<>();
        xAxis.setLabel("Time");
        wpmSeries.setName("WPM");
        avgWpmSeries.setName("Average WPM");
        wpmSeries.getData().add(new XYChart.Data<>(time, 0));
        BigDecimal wpmSum = BigDecimal.ZERO;
        int n = 0;
        for (Word w : words){
            time += w.getTime() / 1000.0;
            wpmSeries.getData().add(new XYChart.Data<>(time, w.getWPM()));
            wpmSum = wpmSum.add(w.getWPM());
            avgWpmSeries.getData().add(new XYChart.Data<>(time, wpmSum.divide(BigDecimal.valueOf(++n), RoundingMode.FLOOR)));
        }

        lineChart.getData().add(wpmSeries);
        lineChart.getData().add(avgWpmSeries);
        return lineChart;
    }

    public String getWordsWPMs() {
    StringBuilder sb = new StringBuilder();
        for (Word w : words) {
            sb
                    .append(w.getSourceWord())
                    .append(" -> WPM :  ")
                    .append(w.getWPM().intValue())
                    .append("\n");
        }
        return sb.toString();
    }
}
