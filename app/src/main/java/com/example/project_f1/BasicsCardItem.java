package com.example.project_f1;

public class BasicsCardItem {
    public final String title;
    public final String generalText;
    public final String detailedText;
    public final int imageRes;
    public final String accentHex;
    public final String videoUrl;

    public BasicsCardItem(String title, String generalText, String detailedText,
                          int imageRes, String accentHex, String videoUrl) {
        this.title = title;
        this.generalText = generalText;
        this.detailedText = detailedText;
        this.imageRes = imageRes;
        this.accentHex = accentHex;
        this.videoUrl = videoUrl;
    }
}
