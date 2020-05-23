package ru.denfad.hintsnotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ru.denfad.hintsnotes.models.Hint;

public class LecturesListActivity extends AppCompatActivity {
    public ListView listView;
    public SharedPreferences sharedPreferencesLectures;
    public List<String> lectures = new ArrayList<>();
    public ImageButton addLecture;
    public ImageButton settings;
    public ArrayAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.lecture_list);

        listView= findViewById(R.id.lecture_list);
        addLecture=findViewById(R.id.addLecture);
        settings = findViewById(R.id.settings);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });


        sharedPreferencesLectures = PreferenceManager.getDefaultSharedPreferences(LecturesListActivity.this);
        GsonBuilder gsonBuilder = new GsonBuilder();


        if(!sharedPreferencesLectures.getString("lectures","[]").equals("[]")){
            Log.i("Get lectures", sharedPreferencesLectures.getString("lectures","[]"));
            lectures.addAll(Arrays.asList(gsonBuilder.create().fromJson(sharedPreferencesLectures.getString("lectures","[]"), String[].class)));
        }

        //создание адаптера

        adapter = new LectureAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,lectures);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),HintListActivity.class);
                intent.putExtra("savingListHint",adapterView.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                openSiteDeleteDialog(adapterView.getItemAtPosition(i).toString());
                return true;
            }
        });

        addLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSiteEditDialog();
            }
        });
    }

    private void openSiteDeleteDialog(final String lectureName) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        final AlertDialog aboutDialog = new AlertDialog.Builder(
                LecturesListActivity.this).setMessage(getResources().getString(R.string.delete_lecture_message))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lectures.remove(lectureName);
                        SharedPreferences.Editor editor = sharedPreferencesLectures.edit();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        editor.putString("lectures", gsonBuilder.create().toJson(lectures));
                        adapter.notifyDataSetChanged();
                    }

                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                }).create();

        aboutDialog.show();

        ((TextView) aboutDialog.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void openSiteEditDialog() {
        final EditText input = new EditText(LecturesListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        final AlertDialog aboutDialog = new AlertDialog.Builder(
                LecturesListActivity.this).setMessage(getResources().getString(R.string.create_lecture))
                .setView(input)
                .setPositiveButton(getResources().getString(R.string.create), new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lectures.add(input.getText().toString());
                        SharedPreferences.Editor editor = sharedPreferencesLectures.edit();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        editor.putString("lectures", gsonBuilder.create().toJson(lectures));
                    }

                }).create();

        aboutDialog.show();

        ((TextView) aboutDialog.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onStop() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        SharedPreferences.Editor editor = sharedPreferencesLectures.edit();
        editor.putString("lectures",gsonBuilder.create().toJson(lectures));
        Log.i("Save lectures", gsonBuilder.create().toJson(lectures));
        editor.apply();
        super.onStop();
    }

    //сохранение заметок при уничтожении активити
    @Override
    protected void onDestroy() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        SharedPreferences.Editor editor = sharedPreferencesLectures.edit();
        editor.putString("lectures", gsonBuilder.create().toJson(lectures));
        Log.i("Save lectures", gsonBuilder.create().toJson(lectures));
        editor.apply();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.finishAffinity();
    }

    public class LectureAdapter extends ArrayAdapter<String>{

        public LectureAdapter(@NonNull Context context, int resource,@NonNull List<String> objects) {
            super(context, resource,objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
            final String lecture = getItem(position);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lecture, null);

            LectureHolder holder = new LectureHolder();
            holder.title = convertView.findViewById(R.id.text1);
            holder.hintsNumber = convertView.findViewById(R.id.hintsNumber);

            holder.title.setText(lecture);

            GsonBuilder gsonBuilder = new GsonBuilder();
            if(!sharedPreferencesLectures.getString(lecture,"[]").equals("[]")) {
                List<Hint> hints = Arrays.asList(gsonBuilder.create().fromJson(sharedPreferencesLectures.getString(lecture, "[]"), Hint[].class));
                holder.hintsNumber.setText(getResources().getString(R.string.hints)+" "+hints.size());
            }
            else holder.hintsNumber.setText(getResources().getString(R.string.no_notes));

            convertView.setTag(holder);

            return convertView;
        }
    }

    //внутренний класс фрагмента заметки для инициализации его объектов (кнопок и полей)
    private static  class LectureHolder {
        public TextView title, hintsNumber;


    }

    private void setLocale() {
        String lang = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("locale", "ru");
        Configuration config;
        Log.d("locale", lang);
        switch (lang) {
            case "ru":
                config = new Configuration();
                config.locale = new Locale("ru");
                getResources().updateConfiguration(config, null);
                break;
            case "en":
                config = new Configuration();
                config.locale = new Locale("en");
                getResources().updateConfiguration(config, null);
                break;
            case "de":
                config = new Configuration();
                config.locale = new Locale("de");
                getResources().updateConfiguration(config, null);
                break;
        }
    }

}
