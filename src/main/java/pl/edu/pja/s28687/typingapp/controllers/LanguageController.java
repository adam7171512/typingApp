package pl.edu.pja.s28687.typingapp.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.*;
import java.util.*;

public class LanguageController implements ChangeListener<String> {

    private static final String DICT_PATH = "dictionary/";
    private static final List<String> languages;
    private static String currentLanguage = "english";

    static {
        languages = getTxtFilesFromDir(DICT_PATH).stream()
                .map(File::getName)
                .map(s -> s.substring(0, s.length() - 4))
                .toList();
    }

    private static List<File> getTxtFilesFromDir(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        return Arrays.stream(listOfFiles).filter(file -> file.getName().endsWith(".txt")).toList();
    }

    public static List<String> getLanguages() {
        return languages;
    }

    public static List<String> get30Words() {
        File file = new File(DICT_PATH + currentLanguage + ".txt");
        List<String> words = new ArrayList<>();
        Set<Integer> used = new HashSet<>();


        BufferedReader br = null;
        int lines = 0;
        try {
            br = new BufferedReader(new FileReader(file));
            while (br.readLine() != null) {
                lines++;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (used.size() <= 30) {
            int random = (int) (Math.random() * lines);
            used.add(random);
        }
        List<Integer> lineNumbers = new ArrayList<>(used);
        Collections.sort(lineNumbers);

        Iterator<Integer> iterator = lineNumbers.iterator();
        Integer lineToRead = iterator.next();
        int n = 0;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (iterator.hasNext() && words.size() < 30) {
            try {
                assert br != null;
                String line = br.readLine();
                if (line == null) break;
                if (n == lineToRead) {
                    words.add(line);
                    lineToRead = iterator.next();
                }
                n++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collections.shuffle(words);
        return words;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
        if (currentLanguage.equals(t1)) return;
        currentLanguage = t1;
    }
}
