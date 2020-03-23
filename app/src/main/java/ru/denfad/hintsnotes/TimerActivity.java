package ru.denfad.hintsnotes;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.denfad.hintsnotes.dao.hintDao;
import ru.denfad.hintsnotes.models.Hint;


public class TimerActivity extends AppCompatActivity {
    public hintDao hintsDao = hintDao.getInstance();
    public Button nextTimer;
    public Timer timer;
    public boolean isActive=false;
    public Long workingtime=0L;
    public TextView timerText;
    public int activeTimer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        timerText=findViewById(R.id.textDisplay1);

        timer = new Timer(hintsDao.getHint(activeTimer));
        timer.start();


        nextTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActive == false) {
                    timer = new Timer(hintsDao.getHint(activeTimer));
                    timer.start();
                    isActive=true;
                }

            }
        });

    }



    public class Timer extends CountDownTimer {
        Hint hint;

        public Timer( Hint hint) {
            super(hint.getMillisInFuture(), 1000);
            this.hint=hint;
        }

        @Override
        public void onTick(long l) {
            int sec = Long.valueOf(l / 1000).intValue();
            int mm = sec / 60;
            int ss = sec % 60;
            String text = String.format("%02d:%02d", mm, ss);
            Log.e("Я выполняю " + hint.getTitle(), "мне его осталось выполнять "+ l);
            timerText.setText(text);
        }

        @Override
        public void onFinish() {
            Log.e("Я закончил выполнять " + hint.getTitle(), "таймер остановлен!");
            Toast.makeText(getApplicationContext(),"Время первого слайда окончено", Toast.LENGTH_SHORT).show();
            activeTimer++;
            isActive=false;
        }



    }
}
