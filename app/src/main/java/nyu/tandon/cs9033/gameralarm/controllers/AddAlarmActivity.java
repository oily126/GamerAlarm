package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import nyu.tandon.cs9033.gameralarm.R;

/**
 * Created by Zhe Wang on 10/30/2015.
 */
public class AddAlarmActivity extends Activity{
    Button setButton;
    Button buttonCancelAlarm;
    Button funMode;
    Button normalMode;
    Button trickMode;
    //toggle buttons for weekdays
    Button mon;
    Button tues;
    Button wed;
    Button thur;
    Button fri;
    Button sat;
    Button sun;
    TextView textAlarmPrompt;
    TimePicker timePicker;

    Calendar alarmCal;
    final static int REQUEST_CODE_1 = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addalarm);
        //time picker
        timePicker = (TimePicker)findViewById(R.id.picker);
        textAlarmPrompt = (TextView)findViewById(R.id.alarmprompt);
        //SetAlarm button
        setButton = (Button)findViewById(R.id.setalarmbutton);
        setButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               //need to be done

            }});

        //cancel button
        buttonCancelAlarm = (Button)findViewById(R.id.cancel);
        buttonCancelAlarm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                cancelAlarm();
            }});

        //fun mode button
        funMode = (Button) findViewById(R.id.funmode);
        funMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //for the first demo, just start the ball game
                Intent intent = new Intent(AddAlarmActivity.this, BallGameActivity.class);
                startActivity(intent);
            }
        });
        //need to set the normal mode button and trick mode button in the future
        normalMode = (Button) findViewById(R.id.normalmode);

        trickMode = (Button) findViewById(R.id.trickmode);
    }

    private void SetAlarm(int dayOfWeek){
        Calendar calNow = Calendar.getInstance();
        alarmCal = (Calendar) calNow.clone();

        alarmCal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        alarmCal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        alarmCal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        alarmCal.set(Calendar.SECOND, 0);
        alarmCal.set(Calendar.MILLISECOND, 0);

        Long alarmTime = alarmCal.getTimeInMillis();
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_1, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 24*60*60*1000, pendingIntent);
    }


    private void cancelAlarm(){
        //need to be done


    }
}
