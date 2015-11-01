package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nyu.tandon.cs9033.gameralarm.R;

/**
 * Created by oily on 11/1/2015.
 */
public class NormalAlarmActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_alarm);

        Button btnSnooze = (Button) findViewById(R.id.snoozeAlarm);
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnOff = (Button) findViewById(R.id.offAlarm);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
