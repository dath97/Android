package com.ce411.project.Class;


import java.util.Date;

public class HistoryClass {
    private boolean status = false;
    private String date;
    public HistoryClass(String date,boolean status){
        this.status = status;
        this.date = date;
    }
    public HistoryClass(){}

    public boolean isStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public void setStatus(boolean status){
        this.status = status;
    }
    public void setDate(String date){
        this.date = date;
    }
}
