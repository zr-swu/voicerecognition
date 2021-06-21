package com.swu.voicerecognition.controller.viewobject;

/**
 * @author zr
 * @create 2021-03-24-10:03
 */
public class videoVO {

    private Integer id;

    private String title;

    int bg;//在视频当中的起始时间
    int ed;//在视频当中的终止时间


    String content;//句子内容

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "videoVO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", bg=" + bg +
                ", ed=" + ed +
                ", content='" + content + '\'' +
                '}';
    }
}
