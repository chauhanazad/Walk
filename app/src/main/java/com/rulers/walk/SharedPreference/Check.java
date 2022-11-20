package com.rulers.walk.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Check {
    public static SharedPreferences spf;
    public static SharedPreferences.Editor ed;
    public static SharedPreferences createSharedPreference(Context c)
    {
        spf=c.getSharedPreferences("pedometer",MODE_PRIVATE);
        ed=spf.edit();
        return spf;

    }
    public static void writeString(String key, String value)
    {
        ed.putString(key,value);
        ed.commit();

    }

    public static void writeInt(String key, int value)
    {
        ed.putInt(key,value);
        ed.commit();
    }

    public static void writeFloat(String key, float value)
    {
        ed.putFloat(key,value);
        ed.commit();
    }

    public static void writeLong(String key, Long value)
    {
        ed.putLong(key,value);
        ed.commit();
    }
}
