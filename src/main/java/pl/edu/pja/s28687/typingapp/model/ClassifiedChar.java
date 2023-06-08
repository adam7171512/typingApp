package pl.edu.pja.s28687.typingapp.model;

/**
 * This class represents a character with its classification (correct, incorrect, extra, omitted, missing).
 */
public class ClassifiedChar {
    public char character;
    public InputClassification classification;

    public ClassifiedChar(char character, InputClassification classification) {
        this.character = character;
        this.classification = classification;
    }
}
