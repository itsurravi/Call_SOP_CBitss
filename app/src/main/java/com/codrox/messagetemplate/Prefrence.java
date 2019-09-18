package com.codrox.messagetemplate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class Prefrence {

    private static final String started = "started";
    private static final String username = "username";
    private static final String password = "password";
    private static final String login = "login";
    private static final String record_path = "audio_path";

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Context c;

    public Prefrence(Context c) {
        this.c = c;
        sp = c.getSharedPreferences("SOP", Context.MODE_PRIVATE);
        ed = sp.edit();
    }

    public boolean checkstart()
    {
        return sp.getBoolean(started, false);
    }

    public void setStart()
    {
        ed.putBoolean(started, true);
        ed.commit();
    }

    public void setData(String a, String b)
    {
        ed.putBoolean(login, true);
        ed.putString(username, a);
        ed.putString(password, b);
        ed.commit();
    }

    public boolean loggedIn()
    {
        return sp.getBoolean(login, false);
    }

    public void setAudioPath(String path)
    {
        ed.putString(record_path, path);
        ed.commit();
    }

    public String getAudioPath() {
        return sp.getString(record_path, null);
    }
}
