package ru.denfad.hintsnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.denfad.hintsnotes.models.Hint;

public class LecturesListActivity extends AppCompatActivity {
    public ListView listView;
    public SharedPreferences sharedPreferences;
    public List<String> lectures = new ArrayList<>();
    public ImageButton addLecture;
    public ArrayAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_list);

        listView= findViewById(R.id.lecture_list);
        addLecture=findViewById(R.id.addLecture);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LecturesListActivity.this);
        GsonBuilder gsonBuilder = new GsonBuilder();


        if(!sharedPreferences.getString("lectures","[]").equals("[]")){
            Log.i("Get lectures", sharedPreferences.getString("lectures","[]"));
            lectures.addAll(Arrays.asList(gsonBuilder.create().fromJson(sharedPreferences.getString("lectures","[]"), String[].class)));
        }

        //создание адаптера
        adapter = new ArrayAdapter<String>(
                this, R.layout.lecture,lectures);
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
                LecturesListActivity.this).setMessage("Вы точно хотите удалить лекцию / доклад?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lectures.remove(lectureName);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        editor.putString("lectures", gsonBuilder.create().toJson(lectures));
                        adapter.notifyDataSetChanged();
                    }

                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
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
                LecturesListActivity.this).setMessage("Введите названеи новой лекции или доклада")
                .setView(input)
                .setPositiveButton("Создать", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lectures.add(input.getText().toString());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lectures",gsonBuilder.create().toJson(lectures));
        Log.i("Save lectures", gsonBuilder.create().toJson(lectures));
        editor.apply();
        super.onStop();
    }

    //сохранение заметок при уничтожении активити
    @Override
    protected void onDestroy() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lectures", gsonBuilder.create().toJson(lectures));
        Log.i("Save lectures", gsonBuilder.create().toJson(lectures));
        editor.apply();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }
}
