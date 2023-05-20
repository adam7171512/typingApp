package pl.edu.pja.s28687.typingapp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Word {
    private final String sourceWord;
    private final List<Character> typedWord = new ArrayList<>();
    private final List<ClassifiedChar> classifiedChars = new ArrayList<>();
    private long startTime;
    private long finishTime;
    private int correctChars;
    private int incorrectChars;
    private int extraChars;
    private int missedChars;

    public Word(String sourceWord){
        this.sourceWord = sourceWord;
        prepareChars();
    }

    private void prepareChars(){
        for (int i = 0; i < sourceWord.length(); i++){
            ClassifiedChar classifiedChar = new ClassifiedChar(sourceWord.charAt(i), InputClassification.MISSING_CHAR);
            classifiedChars.add(classifiedChar);
        }
    }

    public void start(){
        startTime = System.currentTimeMillis();
    }

    public void complete(){

        finishTime = System.currentTimeMillis();
        if (startTime == 0) {
            startTime = finishTime;
        }

        List<ClassifiedChar> omittedChars = classifiedChars
                        .stream()
                        .filter(c -> c.classification == InputClassification.MISSING_CHAR).toList();
        omittedChars.forEach(c -> c.classification = InputClassification.OMITTED_CHAR);
        missedChars = omittedChars.size();
        System.out.println("Word : " + sourceWord + " wpm : " + getWPM());
        System.out.println("Accuracy : " + getAccuracy());
    }

    public BigDecimal getWPM(){
        if (finishTime == 0 || startTime == 0 || finishTime == startTime)
            return BigDecimal.ZERO;
        return BigDecimal.valueOf(60000)
                .divide(BigDecimal.valueOf(finishTime - startTime), RoundingMode.FLOOR)
                .setScale(2, RoundingMode.FLOOR);
    }

    public double getAccuracy(){
        return correctChars / (double) ( getInputChars() + missedChars);
    }

    public int getInputChars(){
        return correctChars + incorrectChars + extraChars;
    }

    public int getCorrectChars(){
        return correctChars;
    }

    public int getIncorrectChars(){
        return incorrectChars;
    }

    public int getExtraChars(){
        return extraChars;
    }

    public int getMissedChars(){
        return missedChars;
    }

    public ClassifiedChar addChar(char c){
        ClassifiedChar classifiedChar;
        if (startTime == 0) {
            start();
        }

        typedWord.add(c);

        if (typedWord.size() <= sourceWord.length()){
            classifiedChar = classifiedChars.get(typedWord.size() - 1);
            if (c == sourceWord.charAt(typedWord.size() - 1)){
                classifiedChar.classification = InputClassification.CORRECT;
                correctChars++;
            } else {
                classifiedChar.classification = InputClassification.INCORRECT;
                incorrectChars++;
            }
        } else {
            classifiedChar = new ClassifiedChar(c, InputClassification.EXTRA_CHAR);
            classifiedChars.add(classifiedChar);
            extraChars++;
        }
        return classifiedChar;
    }

    public ClassifiedChar getLastChar(){
        return classifiedChars.get(classifiedChars.size() - 1);
    }

    public void removeLastChar(){
        if (typedWord.size() > 0) {
            typedWord.remove(typedWord.size() - 1);
            if (typedWord.size() >= sourceWord.length()) {
                classifiedChars.remove(classifiedChars.size() - 1);
            } else {
                classifiedChars.get(typedWord.size()).classification = InputClassification.MISSING_CHAR;
            }
        }
    }

    public List<ClassifiedChar> getClassifiedChars(){
        return classifiedChars;
    }

    public long getTime(){
        return finishTime - startTime;
    }

    public long getFinishTime(){
        return finishTime;
    }

    public String getSourceWord(){
        return sourceWord;
    }

}
