package com.ce411.project.Class;

public class NotificationClass {
    private String title;
    private String description;
    private String content;
    public NotificationClass(String title,String description, String content){
        this.title = title;
        this.description = description;
        this.content = content;
    }
    public NotificationClass(){}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
