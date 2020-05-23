package ru.denfad.hintsnotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    public RadioGroup radioGroup;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Button dark = findViewById(R.id.button_dark);
        Button light = findViewById(R.id.button_light);
        ImageButton back = findViewById(R.id.backToLectures);
        radioGroup = findViewById(R.id.radio_group);

        checkLocale();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Configuration config;
                switch(checkedId){
                    case R.id.radio_ru:
                        config = new Configuration();
                        config.locale = new Locale("ru");
                        getResources().updateConfiguration(config, null);
                        PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("locale","ru").apply();
                        break;
                    case R.id.radio_en:
                        config = new Configuration();
                        config.locale = new Locale("en");
                        getResources().updateConfiguration(config, null);
                        PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("locale","en").apply();
                        break;
                    case R.id.radio_de:
                        config = new Configuration();
                        config.locale = new Locale("de");
                        getResources().updateConfiguration(config, null);
                        PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("locale","de").apply();
                        break;
                }
            }
        });
        dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putInt("theme",AppCompatDelegate.MODE_NIGHT_YES).apply();

            }
        });

        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putInt("theme",AppCompatDelegate.MODE_NIGHT_NO).apply();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LecturesListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkLocale() {
        String lang = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("locale", "ru");
        Log.d("locale", lang);
        switch (lang) {
            case "ru":
                radioGroup.check(R.id.radio_ru);
                break;
            case "en":
                radioGroup.check(R.id.radio_en);
                break;
            case "de":
                radioGroup.check(R.id.radio_de);
                break;
        }
    }

}
