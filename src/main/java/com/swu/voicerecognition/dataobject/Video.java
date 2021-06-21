package com.swu.voicerecognition.dataobject;

public class Video {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.id
     *
     * @mbg.generated Tue Mar 23 21:57:31 CST 2021
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.title
     *
     * @mbg.generated Tue Mar 23 21:57:31 CST 2021
     */
    private String title;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column video.subtitles
     *
     * @mbg.generated Tue Mar 23 21:57:31 CST 2021
     */
    private String subtitles;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.id
     *
     * @return the value of video.id
     *
     * @mbg.generated Tue Mar 23 21:57:31 CST 2021
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.id
     *
     * @param id the value for video.id
     *
     * @mbg.generated Tue Mar 23 21:57:31 CST 2021
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.title
     *
     * @return the value of video.title
     *
     * @mbg.generated Tue Mar 23 21:57:31 CST 2021
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.title
     *
     * @param title the value for video.title
     *
     * @mbg.generated Tue Mar 23 21:57:31 CST 2021
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column video.subtitles
     *
     * @return the value of video.subtitles
     *
     * @mbg.generated Tue Mar 23 21:57:31 CST 2021
     */
    public String getSubtitles() {
        return subtitles;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column video.subtitles
     *
     * @param subtitles the value for video.subtitles
     *
     * @mbg.generated Tue Mar 23 21:57:31 CST 2021
     */
    public void setSubtitles(String subtitles) {
        this.subtitles = subtitles == null ? null : subtitles.trim();
    }


    @Override
    public String toString() {
        return "video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subtitles='" + subtitles + '\'' +
                '}';
    }
}