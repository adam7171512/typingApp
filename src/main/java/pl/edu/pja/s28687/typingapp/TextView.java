package pl.edu.pja.s28687.typingapp;

import javafx.animation.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;

import java.util.*;

import static pl.edu.pja.s28687.typingapp.InputClassification.CORRECT;

public class TextView {

    private final TextField textField;
    private final FlowPane flowPane;
    private List<TextFlow> words;
    private int wordIndex;
    private int charIndex;
    private Iterator<TextFlow> iterator;
    private boolean waveRunning = false;
    private boolean inGame = false;
    private Thread waveThread;

    public TextView(WordController wordController) {
        this.flowPane = new FlowPane();
        this.flowPane.setPrefWidth(800);
        this.flowPane.setHgap(10);
        this.textField = new TextField();
        this.textField.setPrefWidth(0);
        this.textField.setOpacity(0);
        this.textField.textProperty().addListener(wordController);
        this.words = new ArrayList<>();
        flowPane.setAlignment(Pos.CENTER);
    }

    private void playWave() {
//        List<Text> letters = words.stream().flatMap(w -> w.getChildren().stream().map(n -> (Text) n)).toList();
//        ParallelTransition parallelTransition = new ParallelTransition();
//        double delay = 0;
//        for (int i = 0; i < letters.size(); i++) {
//            TranslateTransition up = new TranslateTransition(Duration.millis(300), letters.get(i));
//            up.setByY(-10);
//            TranslateTransition down = new TranslateTransition(Duration.millis(300), letters.get(i));
//            down.setByY(10);
//            SequentialTransition sequentialTransition = new SequentialTransition(new PauseTransition(Duration.millis(delay)), up, down);
//            parallelTransition.getChildren().add(sequentialTransition);
//            delay += 100;
//        }
//        iterator = words.iterator();
         waveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                waveRunning = true;
                if (inGame && iterator.hasNext()) {
                    TextFlow word = iterator.next();
                    List<Text> chars = word.getChildren().stream().map(n -> (Text) n).toList();
                    ParallelTransition parallelTransition = new ParallelTransition();
                    double wordDelay = 0;
                    for (int i = 0; i < chars.size(); i++) {
                        if (chars.get(i).getText().equals(" "))
                            continue;
                        TranslateTransition up = new TranslateTransition(Duration.millis(300), chars.get(i));
                        up.setByY(-10);
                        TranslateTransition down = new TranslateTransition(Duration.millis(300), chars.get(i));
                        down.setByY(10);
                        SequentialTransition sequentialTransition = new SequentialTransition(new PauseTransition(Duration.millis(wordDelay)), up, down);
                        parallelTransition.getChildren().add(sequentialTransition);
                        wordDelay += 100;
                    }
                    parallelTransition.play();
                    parallelTransition.setOnFinished(e -> {
                        parallelTransition.stop();
                        if (!iterator.hasNext()) {
                            iterator = words.iterator();
                        }
                        if (inGame) {
                            playWave();
                        }
                        else {
                            waveRunning = false;
                        }
                    });
                }
            }
        });
        waveThread.start();

//        parallelTransition.play();
//        parallelTransition.setOnFinished(e -> {
//            parallelTransition.stop();
//            playWave();
//        });
    }

    public void setWords(List<Word> words) {
        textField.requestFocus();
        inGame = true;
        if (words.size() != this.words.size()) {
            for (Word w : words) {
                List<Text> letters = w.getClassifiedChars().stream().map(this::processClassifiedChar).toList();
                TextFlow textFlow = new TextFlow();
                textFlow.setStyle("-fx-font-size: 25");
                textFlow.getChildren().addAll(letters);
                textFlow.getChildren().add(new Text(" "));
                this.words.add(textFlow);
            }
            iterator = this.words.iterator();
            if (!waveRunning)
                playWave();
        }

        for (int i = 0; i < 2; i++){
            int wIndex = wordIndex - i;
            if (wIndex < 0) break;
            Word w = words.get(wIndex);
            List<Text> currentWordChars = w.getClassifiedChars().stream().map(this::processClassifiedChar).toList();
            TextFlow currentWord = new TextFlow();
            currentWord.setStyle("-fx-font-size: 25");
            currentWord.getChildren().addAll(currentWordChars);
            currentWord.getChildren().add(new Text(" "));
            this.words.set(wIndex, currentWord);
        }

        flowPane.getChildren().clear();
        flowPane.getChildren().addAll(this.words);
        Text currentChar = (Text) this.words.get(wordIndex).getChildren().get(charIndex);

//        List<Text> currentWordChars = words.get(wordIndex).getClassifiedChars().stream().map(this::processClassifiedChar).toList();
//        Text currentChar = (Text) currentWord.getChildren().get(charIndex);
        currentChar.setStyle(currentChar.getStyle() + "; -fx-underline: true");
        if (charIndex == 0){

            return;
        }
        ClassifiedChar lastChar = words.get(wordIndex).getClassifiedChars().get(charIndex - 1);
        if (lastChar.classification == CORRECT){
            jumpUp((Text) this.words.get(wordIndex).getChildren().get(charIndex - 1));
        }
        if (words.get(wordIndex).getClassifiedChars().stream().allMatch(c -> c.classification == CORRECT)){
            correctWordAnimation(this.words.get(wordIndex));
        }
    }

    private void jumpUp(Text charText) {
        TranslateTransition up = new TranslateTransition(Duration.millis(100), charText);
        up.setByY(-4);
        up.setAutoReverse(true);
        up.setCycleCount(2);
        up.play();
    }

    private void correctWordAnimation(TextFlow word){
        wordAnimation(word, Color.MAGENTA, Color.GREEN);
    }

    private void wordAnimation(TextFlow word, Color color1, Color color2){
        for (Node letter : word.getChildren()){
            Transition transition = new FillTransition(
                    Duration.millis(1000),
                    (Text) letter,
                    color1,
                    color2);
            transition.play();
        }
    }

    private Text processClassifiedChar(ClassifiedChar c){
        Text text = new Text();
        switch (c.classification){
            case CORRECT:
                text.setStyle("-fx-fill: green");
                break;
            case INCORRECT:
                text.setStyle("-fx-fill: red");
                break;
            case MISSING_CHAR:
                text.setStyle("-fx-fill: gray");
                break;
            case EXTRA_CHAR:
                text.setStyle("-fx-fill: orange");
                break;
            case OMITTED_CHAR:
                text.setStyle("-fx-fill: black");
                break;
        }
        text.setText(String.valueOf(c.character));
        return text;
    }

    public TextField getTextField() {
        return textField;
    }

    public FlowPane getFlowPane() {
        return flowPane;
    }

    public void setCurrentWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public void setCurrentCharIndex(int charIndex) {
        this.charIndex = charIndex;
    }

    public void sendChart(LineChart<Number, Number> buildChartData) {
        flowPane.getChildren().add(buildChartData);
    }

    public void clear(){
        inGame = false;
        flowPane.getChildren().clear();
        words.clear();
        textField.clear();
        wordIndex = 0;
        charIndex = 0;
    }

    public void testFinished(TestResult testResult) {
        clear();

        int correct = testResult.getCorrectCharacters();
        int incorrect = testResult.getIncorrectCharacters();
        int missed = testResult.getMissedCharacters();
        int extra = testResult.getExtraCharacters();
        int input = testResult.getInputCharacters();
        double accuracy = testResult.getAccuracy();
        int wpm = testResult.getWPM();
        LineChart<Number, Number> chart = testResult.buildChart();

        flowPane.getChildren().add(chart);


        VBox results = new VBox();
        results.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        results.setAlignment(Pos.CENTER);
        Text resultText = new Text("Test results");
        resultText.setStyle("-fx-font-size: 30");
        results.getChildren().add(resultText);


        HBox hBox = new HBox();
        hBox.getChildren().add(chart);

        TableView<Pair<String, String>> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: transparent");
        TableColumn<Pair<String, String>, String> column1 = new TableColumn<>("Character statistics");
        column1.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getKey()));

        TableColumn<Pair<String, String>, String> column2 = new TableColumn<>("Value");
        column2.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue()));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        List<Pair<String, String>> stats = new ArrayList<>();
        stats.add(new Pair<>("Total Input", String.valueOf(input)));
        stats.add(new Pair<>("Omitted", String.valueOf(missed)));
        stats.add(new Pair<>("Correct", String.valueOf(correct)));
        stats.add(new Pair<>("Blunders", String.valueOf(incorrect)));
        stats.add(new Pair<>("Redundant", String.valueOf(extra)));
        stats.add(new Pair<>("Accuracy", String.valueOf((int) (accuracy * 100)) + " %"));
        stats.add(new Pair<>("WPM", String.valueOf(wpm)));

        tableView.setItems(FXCollections.observableArrayList(stats));


        hBox.getChildren().add(tableView);

        results.getChildren().add(hBox);

        flowPane.getChildren().add(results);

        //
    }

    public void newFeed() {
        words.clear();
    }
}
