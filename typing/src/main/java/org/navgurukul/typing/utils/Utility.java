package org.navgurukul.typing.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Utility {
    private static Map<String, ArrayList<ArrayList<String>>> dataMap = new HashMap<>();
    static {
        init();
    }
    public static void init() {
        dataMap.put("JF", getJF());
        dataMap.put("UR", getUR());
        dataMap.put("DE", getDE());
        dataMap.put("CG", getCG());
        dataMap.put("PRACT1", getPRACT1());
        dataMap.put("TSL", getTSL());
        dataMap.put("OBA", getOBA());
        dataMap.put("VHM", getVHM());
    }

    public static ArrayList<ArrayList<String>> getDataByKey(String key) {
        return dataMap.get(key);
    }

    private static ArrayList<ArrayList<String>> getJF() {
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        list.add(getJList());
        list.add(getFList());
        list.add(getFJList());
        list.add(getFJSpaceList());
        return list;
    }

    private static ArrayList<ArrayList<String>> getUR() {
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        list.add(getUList());
        list.add(getRList());
        list.add(getKList());
        list.add(getURKList());
        return list;
    }

    private static ArrayList<ArrayList<String>> getDE() {
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, new String[] { "d", "d", "d","d", "d", "d", "d","d"});
        list.add(list1);
        ArrayList<String> list2 = new ArrayList<>();
        Collections.addAll(list2, new String[] { "e", "e", "e","e", "e", "e", "e","e"});
        list.add(list2);
        ArrayList<String> list3 = new ArrayList<>();
        Collections.addAll(list3, new String[] { "i", "i", "i","i", "i", "i", "i","i"});
        list.add(list3);
        ArrayList<String> list4 = new ArrayList<>();
        Collections.addAll(list4, new String[] { "e", "i", "j"," ", "u", "d", " ","f"});
        list.add(list4);
        return list;
    }

    private static ArrayList<ArrayList<String>> getCG() {
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, new String[] { "c", "c", "c","c", "c", "c", "c","c"});
        list.add(list1);
        ArrayList<String> list2 = new ArrayList<>();
        Collections.addAll(list2, new String[] { "g", "g", "g","g", "g", "g", "g","g"});
        list.add(list2);
        ArrayList<String> list3 = new ArrayList<>();
        Collections.addAll(list3, new String[] { "n", "n", "n","n", "n", "n", "n","n"});
        list.add(list3);
        ArrayList<String> list4 = new ArrayList<>();
        Collections.addAll(list4, new String[] { "e", "i", "c"," ", "g", "d", " ","n"});
        list.add(list4);
        return list;
    }

    private static ArrayList<ArrayList<String>> getPRACT1() {
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, new String[] { "j", "j", "j"," ", "f", "f", "f"," ", "u", "u", "u", " ", "r" , "r", "r", " ",
                "k", "k","k", " ", "d", "d", "d"});
        list.add(list1);
        ArrayList<String> list2 = new ArrayList<>();
        Collections.addAll(list2, new String[] { "e", "e", "e"," ", "i", "i", "i"," ", "c", "c", "c", " ", "g" , "g", "g", " ",
                "n", "n","n", " ", "j", "j", "j"});
        list.add(list2);
        ArrayList<String> list3 = new ArrayList<>();
        Collections.addAll(list3, new String[] { "j"," ", "f", " ", "u"," ","r", " ","k"," ", "d"," ", "e"," ","i"," ", "c"," ", "g"," ", "n", " ","j"});
        list.add(list3);
        ArrayList<String> list4 = new ArrayList<>();
        Collections.addAll(list4, new String[] { "r"," ", "d"," ", "k"," ","u"," ", "f"," ", "e"," ", "j"," ","i"," ", "g"," ", "n"," ", "c", " ","r"});
        list.add(list4);
        return list;
    }

    private static ArrayList<ArrayList<String>> getTSL() {
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, new String[] { "t", "t", "t","t", "t", "t", "t","t"});
        list.add(list1);
        ArrayList<String> list2 = new ArrayList<>();
        Collections.addAll(list2, new String[] { "s", "s", "s"," ", "s", "g", "f","s"});
        list.add(list2);
        ArrayList<String> list3 = new ArrayList<>();
        Collections.addAll(list3, new String[] { "l", "l", "t"," ", "u", "l", "l","l"});
        list.add(list3);
        ArrayList<String> list4 = new ArrayList<>();
        Collections.addAll(list4, new String[] { "t", "s", "l"," ", "f", "l", "s","l"});
        list.add(list4);
        return list;
    }

    private static ArrayList<ArrayList<String>> getOBA() {
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, new String[] { "o", "o", "o","o", "o", "o", "o","o"});
        list.add(list1);
        ArrayList<String> list2 = new ArrayList<>();
        Collections.addAll(list2, new String[] { "b", "b", "b"," ", "l", "g", "f","s"});
        list.add(list2);
        ArrayList<String> list3 = new ArrayList<>();
        Collections.addAll(list3, new String[] { "a", "a", "t"," ", "o", "a", "u","l"});
        list.add(list3);
        ArrayList<String> list4 = new ArrayList<>();
        Collections.addAll(list4, new String[] { "t", "s", "a"," ", "f", "b", "s","o"});
        list.add(list4);
        return list;
    }

    private static ArrayList<ArrayList<String>> getVHM() {
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, new String[] { "v", "v", "v","v", "v", "v", "v","v"});
        list.add(list1);
        ArrayList<String> list2 = new ArrayList<>();
        Collections.addAll(list2, new String[] { "m", "m", "m"," ", "m", "m", "f","m"});
        list.add(list2);
        ArrayList<String> list3 = new ArrayList<>();
        Collections.addAll(list3, new String[] { "h", "h", "t"," ", "o", "h", "u","l"});
        list.add(list3);
        ArrayList<String> list4 = new ArrayList<>();
        Collections.addAll(list4, new String[] { "t", "v", "a"," ", "m", "b", "h","o"});
        list.add(list4);
        return list;
    }

    private static ArrayList<String> getJList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add("j");
        }
        return list;
    }

    private static ArrayList<String> getFList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add("f");
        }
        return list;
    }

    private static ArrayList<String> getFJList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("f");list.add("f");list.add("f");
        list.add("j");list.add("f");list.add("f");
        list.add("f");list.add("j");
        return list;
    }

    private static ArrayList<String> getFJSpaceList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("f");list.add("f");list.add(" ");
        list.add("j");list.add("f");list.add(" ");
        list.add("f");list.add("j");
        return list;
    }

    private static ArrayList<String> getUList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add("u");
        }
        return list;
    }

    private static ArrayList<String> getRList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add("r");
        }
        return list;
    }

    private static ArrayList<String> getKList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add("k");
        }
        return list;
    }

    private static ArrayList<String> getURKList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("u");list.add("r");list.add("f");
        list.add("j");list.add("k");list.add("f");
        list.add("u");list.add("j");
        return list;
    }

    public static int calculateWPM(double elapsedTime, ArrayList<java.util.ArrayList<String>> list) {
        double second = elapsedTime/1000.0;
        int size = 0;
        for (ArrayList<String> l : list) {
            size = size + l.size();
        }
        //wpm formula
        // (x characters/5)/1m = y wpm
        int wpm = (int) ((((double)size/5)/second)*60);
        return wpm;
    }
}
