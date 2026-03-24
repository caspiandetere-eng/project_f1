package com.example.project_f1;

public class ExpandableCardItem {
    public final String title;
    public final String description;
    public final int imageRes;
    public final String accentHex;

    public ExpandableCardItem(String title, String description, int imageRes, String accentHex) {
        this.title = title;
        this.description = description;
        this.imageRes = imageRes;
        this.accentHex = accentHex;
    }
}
