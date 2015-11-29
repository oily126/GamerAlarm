package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nyu.tandon.cs9033.gameralarm.R;

/**
 * Created by oily on 11/1/2015.
 */
public class NormalAlarmActivity extends Activity {
    private MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_alarm);

        Button btnSnooze = (Button) findViewById(R.id.snoozeAlarm);
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NormalAlarmActivity.this.player.stop();
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
