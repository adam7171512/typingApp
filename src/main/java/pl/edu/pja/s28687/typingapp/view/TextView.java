package pl.edu.pja.s28687.typingapp.view;

import javafx.animation.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.util.Pair;
import pl.edu.pja.s28687.typingapp.controllers.WordController;
import pl.edu.pja.s28687.typingapp.model.ClassifiedChar;
import pl.edu.pja.s28687.typingapp.model.TestResult;
import pl.edu.pja.s28687.typingapp.model.Word;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static pl.edu.pja.s28687.typingapp.model.InputClassification.CORRECT;

/**
 * This class is responsible for displaying the text to be typed, its animation and final result of the test.
 * Each letter is represented by a Text object, allowing for individual animation.
 * Words are represented by TextFlow objects, which are added to the FlowPane.
 * The wave animation is implemented by translating each character up and down.
 * The TextField is used to capture user input. It is invisible.
 */
public class TextView {

    private final TextField textField;
    private final FlowPane flowPane;
    private int currentWordIndex;
    private int currentCharIndex;
    private Iterator<? super TextFlow> iterator;
    private boolean waveRunning = false;
    private boolean inGame = false;

    public TextView(WordController wordController) {
        this.flowPane = new FlowPane();
        this.flowPane.setPrefWidth(800);
        this.flowPane.setHgap(10);
        this.textField = new TextField();
        this.textField.setPrefWidth(0);
        this.textField.setOpacity(0);
        this.textField.textProperty().addListener(wordController);
        flowPane.setAlignment(Pos.CENTER);
    }

    private void playWave() {
        Thread waveThread = new Thread(() -> {
            waveRunning = true;
            if (inGame && iterator.hasNext()) {
                TextFlow word = (TextFlow) iterator.next();
                List<Text> chars = word.getChildren().stream().map(n -> (Text) n).toList();
                ParallelTransition parallelTransition = new ParallelTransition();
                double wordDelay = 0;
                for (Text aChar : chars) {
                    if (aChar.getText().equals(" "))
                        continue; // skip spaces
                    TranslateTransition up = new TranslateTransition(Duration.millis(300), aChar);
                    up.setByY(-10);
                    TranslateTransition down = new TranslateTransition(Duration.millis(300), aChar);
                    down.setByY(10);
                    SequentialTransition sequentialTransition = new SequentialTransition(
                            new PauseTransition(Duration.millis(wordDelay)),
                            up,
                            down
                    );
                    parallelTransition.getChildren().add(sequentialTransition);
                    wordDelay += 100;
                }
                parallelTransition.play();
                parallelTransition.setOnFinished(e -> {
                    parallelTransition.stop();
                    List<Node> flowWords = this.flowPane.getChildren();
                    if (inGame && !iterator.hasNext()) {
                        iterator = flowWords.iterator();
                        playWave();
                    }
                    else {
                        waveRunning = false;
                    }
                });
            }
        });
        waveThread.start();
    }

    public void setTextFlowWords(List<Word> words) {
        textField.requestFocus();
        inGame = true;

        List<Node> flowWords = this.flowPane.getChildren();

        // when new words are set (loaded)
        if (flowWords.size() == 0) {
            for (Word w : words) {
                TextFlow textFlowWord = convertWordToTextFlow(w);
                flowWords.add(textFlowWord);
            }
            iterator = flowWords.iterator();
            if (!waveRunning)
                playWave();
        }

        //updates last words
        for (int i = 0; i < 2; i++) {
            int wIndex = currentWordIndex - i;
            if (wIndex < 0) break;
            Word w = words.get(wIndex);
            TextFlow textFlowWord = convertWordToTextFlow(w);
            flowWords.set(wIndex, textFlowWord);
        }

        // underscore current char
        Text currentChar =
                (Text) ((TextFlow) flowWords
                .get(currentWordIndex))
                .getChildren()
                .get(currentCharIndex);
        currentChar.setStyle(currentChar.getStyle() + "; -fx-underline: true");
        if (currentCharIndex == 0) {
            return;
        }

        // jumping animation if character is correct and color animation if typed word is correct
        ClassifiedChar lastChar = words.get(currentWordIndex).getClassifiedChars().get(currentCharIndex - 1);
        Text visibleLastChar =
                (Text) ((TextFlow) flowWords
                        .get(currentWordIndex))
                        .getChildren()
                        .get(currentCharIndex - 1);

        if (lastChar.classification == CORRECT) {
            jumpUp(visibleLastChar);
        }
        if (
                words.get(currentWordIndex)
                        .getClassifiedChars()
                        .stream()
                        .allMatch(c -> c.classification == CORRECT)
        ) {
            correctWordAnimation((TextFlow) flowWords.get(currentWordIndex));
        }
    }

    private TextFlow convertWordToTextFlow(Word w) {
        List<Text> letters = w.getClassifiedChars().stream().map(this::processClassifiedChar).toList();
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-font-size: 25");
        textFlow.getChildren().addAll(letters);
        textFlow.getChildren().add(new Text(" "));
        return textFlow;
    }

    private void jumpUp(Text charText) {
        TranslateTransition up = new TranslateTransition(Duration.millis(100), charText);
        up.setByY(-4);
        up.setAutoReverse(true);
        up.setCycleCount(2);
        up.play();
    }

    private void correctWordAnimation(TextFlow word) {
        wordAnimation(word, Color.MAGENTA, Color.GREEN);
    }

    private void wordAnimation(TextFlow word, Color color1, Color color2) {
        for (Node letter : word.getChildren()) {
            Transition transition = new FillTransition(
                    Duration.millis(1000),
                    (Text) letter,
                    color1,
                    color2);
            transition.play();
        }
    }

    // converts ClassifiedChar to Text with proper color
    private Text processClassifiedChar(ClassifiedChar c) {
        Text text = new Text();
        switch (c.classification) {
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
        this.currentWordIndex = wordIndex;
    }

    public void setCurrentCharIndex(int charIndex) {
        this.currentCharIndex = charIndex;
    }

    public void sendChart(LineChart<Number, Number> buildChartData) {
        flowPane.getChildren().add(buildChartData);
    }

    public void clear() {
        inGame = false;
        flowPane.getChildren().clear();
        textField.clear();
        currentWordIndex = 0;
        currentCharIndex = 0;
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
        stats.add(new Pair<>("Accuracy", (int) (accuracy * 100) + " %"));
        stats.add(new Pair<>("WPM", String.valueOf(wpm)));

        tableView.setItems(FXCollections.observableArrayList(stats));


        hBox.getChildren().add(tableView);

        results.getChildren().add(hBox);

        flowPane.getChildren().add(results);
    }

    public void newFeed() {
        flowPane.getChildren().clear();
    }
}
