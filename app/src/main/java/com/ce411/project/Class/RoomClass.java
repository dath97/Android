package com.ce411.project.Class;

public class RoomClass {
    private String name;
    private String description;
    private boolean allow = false;
    public RoomClass(String name,boolean allow,String dcrp){
        this.name = name;
        this.allow = allow;
        this.description = dcrp;
    }
    public RoomClass(){};
    public void setName(String name){
        this.name = name;
    }
    public void setAllow(boolean allow){
        this.allow = allow;
    }
    public String getName(){
        return name;
    }
    public boolean getAllow(){
        return allow;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
}
