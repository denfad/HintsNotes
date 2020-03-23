package ru.denfad.hintsnotes.models;

public class Hint {
    private Integer position;
    private String title;
    private String text;

    private Long millisInFuture;

    public Hint(int position, String title, String text, Long millisInFuture) {
        this.position=position;
        this.title = title;
        this.text = text;
        this.millisInFuture = millisInFuture;
    }

    public Hint() {
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getMillisInFuture() {
        return millisInFuture;
    }

    public void setMillisInFuture(Long millisInFuture) {
        this.millisInFuture = millisInFuture;
    }
}
