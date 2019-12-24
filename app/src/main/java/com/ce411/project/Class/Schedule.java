package com.ce411.project.Class;

public class Schedule {
    private String time;
    private String room;
    public Schedule(){}
    public Schedule(String time, String room){
        this.time = time;
        this.room = room;
    }

    public String getRoom() {
        return room;
    }

    public String getTime() {
        return time;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
