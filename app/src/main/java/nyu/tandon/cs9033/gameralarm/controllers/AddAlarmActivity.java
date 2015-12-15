package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import nyu.tandon.cs9033.gameralarm.AlarmDatabaseHelper;
import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.models.Alarm;
import nyu.tandon.cs9033.gameralarm.models.QuizQuestions;
import nyu.tandon.cs9033.gameralarm.views.FunModePreviewFragment;
import nyu.tandon.cs9033.gameralarm.views.QuizModePreviewFragment;

/**
 * Created by Zhe Wang on 10/30/2015.
 */
public class AddAlarmActivity extends AppCompatActivity{
    public final static String EXTRA_MESSAGE = "nyu.tandon.cs9033.gameralarm.controllers.AddAlarmActivity.Message";
    public final static String TAG = "AddAlarmActivity";
    public final static String ALARM_ACTION = "nyu.tandon.cs9033.gameralarm.Alarm";
    Button setButton;
    Button buttonCancelAlarm;
    Button funMode;
    Button normalMode;
    Button trickMode;
    Button ringtoneBtn;
    //toggle buttons for weekdays
    ToggleButton mon, tues, wed, thur, fri, sat, sun;
    //TextView textAlarmPrompt;
    TimePicker timePicker;
    Set<Integer> weekdays;
    int mode = 0;
    boolean isRepeat = false;
    private FunModePreviewFragment funModePreviewFragment;
    String ringtone = "1";
    private int TIME_LIMIT;
    private int SCORE_LIMIT;
    private QuizModePreviewFragment quizModePreviewFragment;
    private int QUESTION_NO;
    private int RIGHT_NO;
    private LinearLayout previewLinearLayout;
    private LinearLayout blankLinearLayout;
    Alarm alarmToEdit;

    final static int REQUEST_CODE_1 = 1;
    final static int REQUEST_RINGTONE = 2;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        if (funModePreviewFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(funModePreviewFragment).commit();
        }
        if (quizModePreviewFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(quizModePreviewFragment).commit();
        }
        previewLinearLayout.setVisibility(View.GONE);
        blankLinearLayout.setVisibility(View.VISIBLE);
    }

    public int getTIME_LIMIT() {
        return TIME_LIMIT;
    }

    public void setTIME_LIMIT(int TIME_LIMIT) {
        this.TIME_LIMIT = TIME_LIMIT;
    }

    public int getQUESTION_NO() {
        return QUESTION_NO;
    }

    public void setQUESTION_NO(int QUESTION_NO) {
        this.QUESTION_NO = QUESTION_NO;
    }

    public int getRIGHT_NO() {
        return RIGHT_NO;
    }

    public void setRIGHT_NO(int RIGHT_NO) {
        this.RIGHT_NO = RIGHT_NO;
    }

    public int getSCORE_LIMIT() {
        return SCORE_LIMIT;
    }

    public void setSCORE_LIMIT(int SCORE_LIMIT) {
        this.SCORE_LIMIT = SCORE_LIMIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addalarm);
        weekdays = new HashSet<>();
        //time picker
        timePicker = (TimePicker)findViewById(R.id.picker);
        previewLinearLayout = (LinearLayout) findViewById(R.id.PreviewContainer);
        blankLinearLayout = (LinearLayout) findViewById(R.id.BlankContainer);
        //textAlarmPrompt = (TextView)findViewById(R.id.alarmprompt);

        //set listener for the weekday buttons:
        addListenerOnToggleButton();

        if (MainActivity.bgPic != null) {
            ((RelativeLayout) findViewById(R.id.addAlarmView)).setBackground(MainActivity.bgPic);
        } else {
            ((RelativeLayout) findViewById(R.id.addAlarmView)).setBackgroundColor(MainActivity.bgColor);
        }

        //check whether there is any
        alarmToEdit = getIntent().getParcelableExtra("alarm");
        if(alarmToEdit != null){
            //display the alarm time on time picker
            int time = alarmToEdit.getTime();
            timePicker.setCurrentHour(time/100);
            timePicker.setCurrentMinute(time % 100);
            TIME_LIMIT = alarmToEdit.getTimeLimit();
            SCORE_LIMIT = alarmToEdit.getScoreLimit();
            QUESTION_NO = alarmToEdit.getQuesNum();
            RIGHT_NO = alarmToEdit.getRightQues();
            //display the weekdays on the toggle button.
            ArrayList<Integer> days = alarmToEdit.getWeekBitmap();
            for(int day : days){
                switch (day){
                    case 1: sun.setChecked(true);
                        break;
                    case 2: mon.setChecked(true);
                        break;
                    case 3: tues.setChecked(true);
                        break;
                    case 4: wed.setChecked(true);
                        break;
                    case 5: thur.setChecked(true);
                        break;
                    case 6: fri.setChecked(true);
                        break;
                    case 7: sat.setChecked(true);
                        break;
                }
            }
            mode = alarmToEdit.getMode();
            ringtone = alarmToEdit.getRingtone();
        }

        //SetAlarm button
        setButton = (Button)findViewById(R.id.setalarmbutton);
        setButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Alarm alarm = createAlarm();
                AlarmDatabaseHelper db = new AlarmDatabaseHelper(AddAlarmActivity.this);
                int alarmID;
                if(alarmToEdit!=null){
                    //cancel the original alarm;
                    deleteAlarmIntent(alarmToEdit);
                    db.updateAlarm(alarm);
                    alarmID = alarm.getAlarmId();
                }
                else{
                    alarmID = (int) db.addAlarm(alarm);
                }
                db.close();
                mode = alarm.getMode();
                ArrayList<Integer> days = alarm.getWeekBitmap();
                if(days.size() ==0 ){
                    setAlarm(alarmID*10, 0);
                }
                for(int day: days){
                    Log.i(AddAlarmActivity.TAG, String.valueOf(day));
                    setAlarm(alarmID * 10 + day, day);
                }
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
                //mode = 10;
                normalMode.setBackgroundResource(0);
                trickMode.setBackgroundResource(0);
                funMode.setBackgroundResource(R.color.button_color);
                blankLinearLayout.setVisibility(View.GONE);
                if (quizModePreviewFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(quizModePreviewFragment).commit();
                }
                if (funModePreviewFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(funModePreviewFragment).commit();
                }
                funModePreviewFragment = new FunModePreviewFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.PreviewContainer, funModePreviewFragment).commit();
                previewLinearLayout.setVisibility(View.VISIBLE);
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
                funMode.setBackgroundResource(0);
                trickMode.setBackgroundResource(0);
                normalMode.setBackgroundResource(R.color.button_color);
                blankLinearLayout.setVisibility(View.VISIBLE);
                if (funModePreviewFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(funModePreviewFragment).commit();
                }
                if (quizModePreviewFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(quizModePreviewFragment).commit();
                }
            }
        });

        trickMode = (Button) findViewById(R.id.trickmode);
        trickMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                normalMode.setBackgroundResource(0);
                funMode.setBackgroundResource(0);
                trickMode.setBackgroundResource(R.color.button_color);
                blankLinearLayout.setVisibility(View.GONE);
                if (funModePreviewFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(funModePreviewFragment).commit();
                }
                if (quizModePreviewFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(quizModePreviewFragment).commit();
                }
                quizModePreviewFragment = new QuizModePreviewFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.PreviewContainer, quizModePreviewFragment).commit();
                previewLinearLayout.setVisibility(View.VISIBLE);
                //Intent intent = new Intent(AddAlarmActivity.this, QuizActivity.class);
                //startActivity(intent);
            }
        });

        ringtoneBtn = (Button) findViewById(R.id.ringtone);
        ringtoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);//pick one from the list
                startActivityForResult(intent, REQUEST_RINGTONE);
            }
        });

    }

    private Alarm createAlarm(){

        int time = timePicker.getCurrentHour()*100 + timePicker.getCurrentMinute();
        //mode =
        isRepeat = weekdays.isEmpty()? false:true;
        Alarm alarm;
        if(alarmToEdit!= null)
            alarm = new Alarm(alarmToEdit.getAlarmId(), time, isRepeat, weekdays, mode, ringtone, true, TIME_LIMIT, SCORE_LIMIT, QUESTION_NO, RIGHT_NO);
        else
            alarm = new Alarm(time, isRepeat, weekdays, mode, ringtone, true, TIME_LIMIT, SCORE_LIMIT, QUESTION_NO, RIGHT_NO);
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
        long alarmTime;
        if(dayOfWeek == 0)
            alarmTime = alarmCal.getTimeInMillis()>=System.currentTimeMillis()? alarmCal.getTimeInMillis(): alarmCal.getTimeInMillis()+24*3600*1000;
        else
            alarmTime = alarmCal.getTimeInMillis()>=System.currentTimeMillis()? alarmCal.getTimeInMillis(): alarmCal.getTimeInMillis()+24*3600*1000*7;
        Intent intent = new Intent(this, AlarmReceiver.class);

        //add the action:
        intent.setAction("nyu.tandon.cs9033.gameralarm.Alarm");

        intent.putExtra("Mode", mode); //should pass the alarm to the receiver
        intent.putExtra("ringtone", ringtone);
        intent.putExtra("TimeLimit", TIME_LIMIT);
        intent.putExtra("ScoreLimit", SCORE_LIMIT);
        intent.putExtra("TotalNumber", QUESTION_NO);
        intent.putExtra("RightNumber", RIGHT_NO);
        intent.putExtra("alarmId", (int)(id/10));
        intent.putExtra("isRepeat", isRepeat);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i("alarm time debug", "the alarmtime is " +String.valueOf(alarmTime));
        Log.i("alarm time debug", "current time is " + String.valueOf(System.currentTimeMillis()));
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        if(isRepeat)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 24*60*60*1000*7, pendingIntent);
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
    public void deleteAlarmIntent(Alarm alarm) {
        ArrayList<Integer> days = alarm.getWeekBitmap();
        if (days.size() == 0) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm.getAlarmId() * 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        } else {
            for (int day : days) {
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm.getAlarmId() * 10 + day, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RINGTONE) {
            if (resultCode == RESULT_OK) {
                String[] proj = {MediaStore.Audio.Media.DATA};
                Cursor c = managedQuery(data.getData(), proj, null, null, null);
                int musiccolumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                c.moveToFirst();
                ringtone = c.getString(musiccolumn);
                Log.i(this.getClass().toString(), "ringtone " + ringtone);
            } else {
                ringtone = "1";
            }
        }
    }
}
