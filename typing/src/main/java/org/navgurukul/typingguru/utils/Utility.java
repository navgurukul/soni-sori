package org.navgurukul.typingguru.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utility {

    public static int calculateWPM(long elapsedTime, List<String> list) {
        long second = elapsedTime/1000;
        int size = list.size();
        //wpm formula
        // (x characters/5)/1m = y wpm
        int wpm = (int) ((((double)size/5)/second)*60);
        return wpm;
    }

    public static int calculateWPM(long elapsedTime, ArrayList<java.util.ArrayList<String>> list) {
        long second = elapsedTime/1000;
        int size = 0;
        for (ArrayList<String> l : list) {
            size = size + l.size();
        }
        //wpm formula
        // (x characters/5)/1m = y wpm
        int wpm = (int) ((((double)size/5)/second)*60);
        return wpm;
    }

    public static ArrayList<String> generateRandomWordList(ArrayList<String> list) {
        Random r = new Random();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            String s = list.get(r.nextInt(list.size()));
            result.add(s);
        }
        return result;
    }
}
