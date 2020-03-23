package ru.denfad.hintsnotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.GsonBuilder;


import java.util.Arrays;
import java.util.List;

import ru.denfad.hintsnotes.dao.hintDao;
import ru.denfad.hintsnotes.models.Hint;

public class HintListActivity extends AppCompatActivity {

    private ListView hintList;
    public hintDao hintsDao = hintDao.getInstance();
    public HintAdapter adapter;

    public SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hint_list);

        //конструкторы сохранения заметок
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HintListActivity.this);
        GsonBuilder gsonBuilder = new GsonBuilder();

        //создание листа из заметок
        hintList=findViewById(R.id.hintList);
        adapter = new HintAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, hintsDao.getHints());
        hintList.setAdapter(adapter);

        //проверка на наличие сохраненных заметок
        if(!sharedPreferences.getString("save","[]").equals("[]") && hintsDao.getAllHints().isEmpty()){
            Log.i("Save hints", sharedPreferences.getString("save","[]"));
            hintsDao.addAllHints(Arrays.asList(gsonBuilder.create().fromJson(sharedPreferences.getString("save","[]"),Hint[].class)));
        }


        //кнопка добавления заметки
        ImageButton addHint= findViewById(R.id.addHint);
        addHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("hint",hintsDao.getMapSize());
                startActivity(intent);
            }
        });

        //кнопка включения таймера
        ImageButton play= findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hintsDao.getMapSize()>0){
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
                else Toast.makeText(getApplicationContext(),"Нет заметок!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //сохранение заметок при остановке активите
    @Override
    protected void onStop() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        String hints = gsonBuilder.create().toJson(hintsDao.getAllHints());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("save", hints);
        Log.i("Save hints", hints);
        editor.apply();
        super.onStop();
    }

    //сохранение заметок при уничтожении активити
    @Override
    protected void onDestroy() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        String hints = gsonBuilder.create().toJson(hintsDao.getAllHints());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("save", hints);
        Log.i("Save hints", hints);
        editor.apply();
        super.onDestroy();
    }


    //внутренний класс адаптера для просмотра всех заметок
    private class HintAdapter extends ArrayAdapter<Hint>{


        public HintAdapter(@NonNull Context context, int resource, @NonNull List<Hint> objects) {
            super(context, resource, objects);
        }


        //создание адаптера
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent){
            final Hint hint = getItem(position);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hint, null);

            HintHolder holder = new HintHolder();
            holder.title = convertView.findViewById(R.id.title);
            holder.settings = convertView.findViewById(R.id.settings);
            holder.delete=convertView.findViewById(R.id.delete);



            holder.title.setText(hint.getTitle());
            convertView.setTag(holder);

            //установка слушателя на кнопку редактирования заметки
            holder.settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), SettingsActivity.class);
                    intent.putExtra("hint",hint.getPosition());
                    startActivity(intent);
                }
            });

            //установка слушателя на кнопку удаления заметки
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.remove(hint);
                    hintsDao.deleteHint(hint);

                }
            });



            return convertView;
        }
    }

    //внутренний класс фрагмента заметки для инициализации его объектов (кнопок и полей)
    private static  class HintHolder {
        public TextView title;
        public ImageButton settings;
        public ImageButton delete;

    }

}
