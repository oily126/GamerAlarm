package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;
import java.util.Calendar;

import nyu.tandon.cs9033.gameralarm.R;

/**
 * Created by oily on 11/1/2015.
 */
public class NormalAlarmActivity extends Activity {
    private MediaPlayer player = null;
    private static  final int snoozeID = -1;
    private String path = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_alarm);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (getIntent().hasExtra("ringtone")) path = getIntent().getStringExtra("ringtone");
        Button btnSnooze = (Button) findViewById(R.id.snoozeAlarm);
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NormalAlarmActivity.this.player.stop();
                NormalAlarmActivity.this.player.release();
                NormalAlarmActivity.this.player = null;
                Calendar calNow = Calendar.getInstance();
                Calendar alarmCal = (Calendar) calNow.clone();
                alarmCal.set(Calendar.MINUTE, calNow.get(Calendar.MINUTE) + 1);
                Intent intent = new Intent(NormalAlarmActivity.this, AlarmReceiver.class);
                intent.putExtra("Mode", 0); //should pass the alarm to the receiver
                intent.putExtra("ringtone", NormalAlarmActivity.this.path);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(NormalAlarmActivity.this, snoozeID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Long alarmTime = alarmCal.getTimeInMillis() >= System.currentTimeMillis() ? alarmCal.getTimeInMillis() : alarmCal.getTimeInMillis() + 24 * 3600 * 1000;
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
                finish();
                NormalAlarmActivity.this.finishAffinity();
            }
        });

        Button btnOff = (Button) findViewById(R.id.offAlarm);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NormalAlarmActivity.this.player.stop();
                NormalAlarmActivity.this.player.release();
                NormalAlarmActivity.this.player = null;
                finish();
                NormalAlarmActivity.this.finishAffinity();
            }
        });

        createMediaPlayer(path);
        player.setLooping(true);
        player.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player == null) {
            if (path == null) path = "1";
            createMediaPlayer(path);
            player.setLooping(true);
        }
        player.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    private void createMediaPlayer(String path) {
        try {
            int id = Integer.parseInt(path);
            if (id == 1) {
                player = MediaPlayer.create(this, R.raw.ringtone1);
            } else {
                if (id == 2) {
                    player = MediaPlayer.create(this, R.raw.ringtone2);
                }
            }
        } catch (NumberFormatException e) {
            try {
                player = new MediaPlayer();
                player.setDataSource(path);
                player.prepare();
            } catch (IOException e1) {
                player = MediaPlayer.create(this, R.raw.ringtone1);
            }
        }
    }
}
