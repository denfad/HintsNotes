package ru.denfad.hintsnotes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.GsonBuilder;

import ru.denfad.hintsnotes.dao.hintDao;
import ru.denfad.hintsnotes.models.Hint;


public class MainActivity extends AppCompatActivity {


    public hintDao hintsDao = hintDao.getInstance();
    public ImageButton nextTimer;
    public MainActivity.Timer timer;
    public boolean isActive = false;
    public TextView timerText, timerText2;
    public int activeTimer = 0;
    public TextView textDisplay, textDisplay2;
    public TextView titleText, titleText2;
    public LinearLayout piece1, piece2;
    public String savingListHint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        savingListHint=intent.getStringExtra("savingListHint");

        titleText = findViewById(R.id.name1);
        nextTimer = findViewById(R.id.nextTimer);
        textDisplay = findViewById(R.id.textDisplay1);
        timerText = findViewById(R.id.timerDisplay1);

        titleText2 = findViewById(R.id.name2);
        textDisplay2 = findViewById(R.id.textDisplay2);
        timerText2 = findViewById(R.id.timerDisplay2);
        piece1 = findViewById(R.id.piece);
        piece2 = findViewById(R.id.ll1);


        //проверка на запущенность таймера
        if (isActive == false) {
            //присвоение полям дисплея значений
            textDisplay.setText(hintsDao.getHint(activeTimer).getText());
            titleText.setText(hintsDao.getHint(activeTimer).getTitle());
            timer = new Timer(hintsDao.getHint(activeTimer));
            //запуск таймера, наследника thread
            timer.start();
            isActive = true;
        }

        //конопка переключения на следущий таймер
        nextTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timer.cancel();
                timerText.setTextColor(Color.WHITE);
                isActive = false;
                activeTimer++;
                if (activeTimer < hintsDao.getMapSize()) {
                    anim();
                    timer = new Timer(hintsDao.getHint(activeTimer));
                    timer.start();
                    isActive = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Презентация окончена", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(getApplicationContext(), HintListActivity.class);
                    intent1.putExtra("savingListHint",savingListHint);
                    startActivity(intent1);
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplicationContext(), HintListActivity.class);
        intent1.putExtra("savingListHint",savingListHint);
        startActivity(intent1);
        if(isActive){
            timer.cancel();
        }

    }

    public class Timer extends CountDownTimer {
        Hint hint;

        public Timer(Hint hint) {
            super(hint.getMillisInFuture(), 1000);
            this.hint = hint;

        }

        @Override
        public void onTick(long l) {
            if (l < 0.5 * hint.getMillisInFuture()) {
                timerText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            int sec = Long.valueOf(l / 1000).intValue();
            int mm = sec / 60;
            int ss = sec % 60;
            String text = String.format("%02d:%02d", mm, ss);
            Log.e("Я выполняю " + hint.getTitle(), "мне его осталось выполнять " + l);
            timerText.setText(text);
        }

        @Override
        public void onFinish() {

            Log.e("Я закончил выполнять " + hint.getTitle(), "таймер остановлен!");
            Toast.makeText(getApplicationContext(), "Время слайда окончено", Toast.LENGTH_SHORT).show();
            isActive = false;
            timerText.setTextColor(Color.RED);
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

//Указываем длительность вибрации в миллисекундах,
//в нашем примере будет вибро-сигнал длительностью в 2 секунды
            vibrator.vibrate(2000);
        }


    }

    public void anim() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animat_main);
        piece1.startAnimation(animation);
        textReplacer();
        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animat_back);
                piece1.startAnimation(animation2);
                textDisplay.setText(hintsDao.getHint(activeTimer).getText());
                titleText.setText(hintsDao.getHint(activeTimer).getTitle());
            }
        }.start();
    }


    public void textReplacer(){
        textDisplay2.setText(hintsDao.getHint(activeTimer).getText());
        titleText2.setText(hintsDao.getHint(activeTimer).getTitle());
    }
}
