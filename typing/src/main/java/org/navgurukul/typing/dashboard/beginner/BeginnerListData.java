package org.navgurukul.typing.dashboard.beginner;

import java.util.ArrayList;
import java.util.List;

public class BeginnerListData {
    private static ArrayList<BeginnerListData> tempData = new ArrayList<>();
    private String title;
    private int wpm;
    private String key;

    static {
        tempData.add(new BeginnerListData("J, F, and Space", "JF"));
        tempData.add(new BeginnerListData("U, R, and K  Keys", "UR"));
        tempData.add(new BeginnerListData("D, E, and I  Keys", "DE"));
        tempData.add(new BeginnerListData("C, G, and N  Keys", "CG"));
        tempData.add(new BeginnerListData("Beginner Review 1", "PRACT1"));
        tempData.add(new BeginnerListData("T, S, and L Keys", "TSL"));
        tempData.add(new BeginnerListData("O, B, and A Keys", "OBA"));
        tempData.add(new BeginnerListData("V, H, and M Keys", "VHM"));
    }


    public BeginnerListData() {

    }

    public BeginnerListData(String title, String key) {
        this.title = title;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWpm() {
        return wpm;
    }

    public void setWpm(int wpm) {
        this.wpm = wpm;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static ArrayList<BeginnerListData> getBeginnerData() {
        return tempData;
    }
}
