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
import android.widget.Button;

import java.util.Calendar;

import nyu.tandon.cs9033.gameralarm.R;

/**
 * Created by oily on 11/1/2015.
 */
public class NormalAlarmActivity extends Activity {
    private MediaPlayer player;
    private static  final int snoozeID = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_alarm);

        Button btnSnooze = (Button) findViewById(R.id.snoozeAlarm);
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NormalAlarmActivity.this.player.stop();
                Calendar calNow = Calendar.getInstance();
                Calendar alarmCal = (Calendar) calNow.clone();
                alarmCal.set(Calendar.MINUTE, calNow.get(Calendar.MINUTE)+1);
                Intent intent = new Intent(NormalAlarmActivity.this, AlarmReceiver.class);
                intent.putExtra("Mode", 0); //should pass the alarm to the receiver
                PendingIntent pendingIntent = PendingIntent.getBroadcast(NormalAlarmActivity.this, snoozeID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Long alarmTime = alarmCal.getTimeInMillis()>=System.currentTimeMillis()? alarmCal.getTimeInMillis(): alarmCal.getTimeInMillis()+24*3600*1000;
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
                finish();
            }
        });

        Button btnOff = (Button) findViewById(R.id.offAlarm);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NormalAlarmActivity.this.player.stop();
                finish();
            }
        });

        player =  MediaPlayer.create(this, R.raw.ringtone1);
        player.setLooping(true);
        player.start();
    }
}
