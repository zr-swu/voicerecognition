package com.swu.voicerecognition.entity;

/**
 * @author zr
 * @create 2021-03-23-20:30
 */

public class video {
    private Integer id;
    private String title;
    private String subtitles;//字幕

    public video() {
    }

    public video(Integer id, String title, String subtitles) {
        this.id = id;
        this.title = title;
        this.subtitles = subtitles;
    }



}
