package com.swu.voicerecognition.entity;

/**
 * @author zr
 * @create 2021-03-22-13:37
 */
public class Sentence {
    int bg;//在视频当中的起始时间
    int ed;//在视频当中的终止时间
    String content;//句子内容


    public Sentence() {
    }

    public Sentence(int bg, int ed, String content) {
        this.bg = bg;
        this.ed = ed;
        this.content = content;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "句子内容:"+this.getContent() +"\t视频中句子开始时间:"+this.getBg()+"\t视频中句子结束时间"+this.getEd();
    }
}
