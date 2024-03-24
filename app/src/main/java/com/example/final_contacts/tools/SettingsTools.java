package com.example.final_contacts.tools;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.example.final_contacts.R;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Scanner;

public final class SettingsTools implements Serializable {
    private static SettingsTools settings;

    public SettingsTools() {

    }

    public static SettingsTools settings() {
        if (settings == null) {
            settings = new SettingsTools();
        }
        return settings;
    }

    public static void saveSettings(Activity activity) {
        Gson gson = new Gson();
        String json = gson.toJson(settings());
        try {
            FileOutputStream output = activity.openFileOutput("settings.json", Context.MODE_PRIVATE);
            output.write(json.getBytes());
        } catch (Exception e) {
            Log.e("EBE1FC", "save: ", e);
        }
    }

    public static void loadSettings(Activity activity) {
        Gson gson = new Gson();
        try {
            FileInputStream input = activity.openFileInput("settings.json");
            Scanner scanner = new Scanner(input);
            String json = scanner.nextLine();
            settings = gson.fromJson(json, SettingsTools.class);
        } catch (Exception e) {
            Log.e("EBE1FC", "load: ", e);
        }
    }

    public static void setLocale(Activity activity) {
        Locale locale = new Locale(settings().locale);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
    }

    public int theme = R.style.Theme_Dark;
    public String locale = Locale.ENGLISH.toString();
}
