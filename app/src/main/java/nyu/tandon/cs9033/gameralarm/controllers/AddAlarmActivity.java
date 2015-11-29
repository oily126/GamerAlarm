package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nyu.tandon.cs9033.gameralarm.AlarmDatabaseHelper;
import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.models.Alarm;

/**
 * Created by Zhe Wang on 10/30/2015.
 */
public class AddAlarmActivity extends Activity{
    public final static String EXTRA_MESSAGE = "nyu.tandon.cs9033.gameralarm.controllers.AddAlarmActivity.Message";
    public final static String TAG = "AddAlarmActivity";
    Button setButton;
    Button buttonCancelAlarm;
    Button funMode;
    Button normalMode;
    Button trickMode;
    //toggle buttons for weekdays
    ToggleButton mon, tues, wed, thur, fri, sat, sun;
    TextView textAlarmPrompt;
    TimePicker timePicker;
    Set<Integer> weekdays;
    int mode = 0;
    boolean isRepeat = false;

    final static int REQUEST_CODE_1 = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addalarm);
        weekdays = new HashSet<>();
        //time picker
        timePicker = (TimePicker)findViewById(R.id.picker);
        textAlarmPrompt = (TextView)findViewById(R.id.alarmprompt);

        //set listener for the weekday buttons:
        addListenerOnToggleButton();

        if (MainActivity.bgPic != null) {
            ((RelativeLayout) findViewById(R.id.addAlarmView)).setBackground(MainActivity.bgPic);
        }

        //SetAlarm button
        setButton = (Button)findViewById(R.id.setalarmbutton);
        setButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Alarm alarm = createAlarm();
                AlarmDatabaseHelper db = new AlarmDatabaseHelper(AddAlarmActivity.this);
                int alarmID = (int) db.addAlarm(alarm);
                mode = alarm.getMode();
                ArrayList<Integer> days = alarm.getWeekBitmap();
                if(days.size() ==0 ){
                    setAlarm(alarmID*10, 0);
                }
                for(int day: days){
                    Log.i(AddAlarmActivity.TAG, String.valueOf(day));
                    setAlarm(alarmID * 10 + day, day);
                }
                db.close();
                setResult(RESULT_OK);
                finish();
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
                mode = 10;
//                Intent intent = new Intent(AddAlarmActivity.this, BallGameActivity.class);
//                startActivity(intent);
            }
        });
        //need to set the normal mode button and trick mode button in the future
        normalMode = (Button) findViewById(R.id.normalmode);
        normalMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for the first demo, just start the normal mode
                mode = 0;
//                Intent intent = new Intent(AddAlarmActivity.this, NormalAlarmActivity.class);
//                startActivity(intent);
            }
        });

        trickMode = (Button) findViewById(R.id.trickmode);


    }

    private Alarm createAlarm(){

        int time = timePicker.getCurrentHour()*100 + timePicker.getCurrentMinute();
        //mode =
        isRepeat = weekdays.isEmpty()? false:true;
        Alarm alarm = new Alarm(time, isRepeat, weekdays, mode, "default", true);
        return alarm;
    }

    private void setAlarm(int id, int dayOfWeek){
        Calendar calNow = Calendar.getInstance();
        Calendar alarmCal = (Calendar) calNow.clone();

        if(dayOfWeek !=0)
            alarmCal.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        alarmCal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        alarmCal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        alarmCal.set(Calendar.SECOND, 0);
        alarmCal.set(Calendar.MILLISECOND, 0);

        Long alarmTime = alarmCal.getTimeInMillis()>=System.currentTimeMillis()? alarmCal.getTimeInMillis(): alarmCal.getTimeInMillis()+24*3600*1000;
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("Mode", mode); //should pass the alarm to the receiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i(AddAlarmActivity.TAG, "the alarmtime is " +String.valueOf(alarmTime));
        Log.i(AddAlarmActivity.TAG, "current time is " + String.valueOf(System.currentTimeMillis()));
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        if(isRepeat)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 24*60*60*1000, pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }


    private void cancelAlarm(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //set listener for togglebuttons from mon to sun
    private  void addListenerOnToggleButton(){
        mon = (ToggleButton) findViewById(R.id.monday);
        mon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    weekdays.add(2);
                }
                else{
                    weekdays.remove(2);
                }
            }
        });
        tues = (ToggleButton) findViewById(R.id.tuesday);
        tues.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    weekdays.add(3);
                }
                else{
                    weekdays.remove(3);
                }
            }
        });

        wed = (ToggleButton) findViewById(R.id.wednesday);
        wed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    weekdays.add(4);
                }
                else{
                    weekdays.remove(4);
                }
            }
        });

        thur = (ToggleButton) findViewById(R.id.thursday);
        thur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    weekdays.add(5);
                }
                else{
                    weekdays.remove(5);
                }
            }
        });

        fri = (ToggleButton) findViewById(R.id.friday);
        fri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weekdays.add(6);
                } else {
                    weekdays.remove(6);
                }
            }
        });

        sat = (ToggleButton) findViewById(R.id.saturday);
        sat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weekdays.add(7);
                } else {
                    weekdays.remove(7);
                }
            }
        });

        sun = (ToggleButton) findViewById(R.id.sunday);
        sun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    weekdays.add(1);
                }
                else{
                    weekdays.remove(1);
                }
            }
        });
    }
}
