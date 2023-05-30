package pl.edu.pja.s28687.typingapp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageControllerTest {

    @Test
    void testGet30Words() {
        List<String> words = LanguageController.get30Words();
        System.out.println(words);
        assertEquals(30, words.size());
    }

}