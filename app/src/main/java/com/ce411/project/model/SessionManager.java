package com.ce411.project.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
public class SessionManager {
    private SharedPreferences prefs;
    private Context t;
    public SessionManager(Context cntx) {
        // TODO Auto-generated constructor stub
        t = cntx;
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }
    public void setUsername(String username) {
        prefs.edit().putBoolean("IS_USER_LOGIN", true).apply();
        prefs.edit().putString("username", username).apply();
    }
    public void setToken(String token) {
        prefs.edit().putString("token", token).apply();
    }
    public String getToken() {
        return prefs.getString("token", "");
    }
    public String getUsername() {
        return prefs.getString("username", "");
    }
    public void logout(){
        String token = prefs.getString("token", "");
        prefs.edit().clear().apply();
        prefs.edit().putBoolean("IS_USER_LOGIN",false).apply();
        prefs.edit().putString("token", token).apply();
    }
    public boolean isUserLoggedIn(){
        return prefs.getBoolean("IS_USER_LOGIN", false);
    }
}