package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nyu.tandon.cs9033.gameralarm.AlarmDatabaseHelper;
import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.models.Alarm;
import nyu.tandon.cs9033.gameralarm.views.AlarmListAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ArrayList<Alarm> alarmListArray;
    private List<Map<String, Object>> alarmListItems;
    private AlarmListAdapter alarmListAdapter;
    private ListView alarmList;
    private ImageView noAlarmImage;
    private final static int ADD__ALARM = 1;
    private final static int EDIT__ALARM = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //addAlarmForTest();
        readAlarmList();
        alarmList = (ListView) findViewById(R.id.alarmList);
        alarmListAdapter = new AlarmListAdapter(this, alarmListItems);
        alarmList.setAdapter(alarmListAdapter);
        alarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int positionToDelete = position;
                final int alarmIdToDelete = alarmListArray.get(position).getAlarmId();
                final Alarm alarmToDelete = alarmListArray.get(position);
                AlertDialog tmp = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete alarm confirm")
                        .setMessage("Are you sure to delete this alarm?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alarmListItems.remove(positionToDelete);
                                alarmListAdapter.notifyDataSetChanged();
                                alarmListArray.remove(positionToDelete);
                                AlarmDatabaseHelper helper = new AlarmDatabaseHelper(MainActivity.this);
                                helper.deleteAlarmById(alarmIdToDelete);
                                MainActivity.this.deleteAlarmIntent(alarmToDelete);
                                setAlarmVisible();
                                helper.close();
                            }
                        })
                        .show();
                return false;
            }
        });

        alarmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.this.editAlarm(position);
            }
        });

        noAlarmImage = (ImageView) findViewById(R.id.noAlarm);
        noAlarmImage.setImageDrawable(getResources().getDrawable(R.drawable.alarm));

        setAlarmVisible();

        Button btn_add = (Button) findViewById(R.id.addAlarm);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewAlarm();
            }
        });

        Button btn_setting = (Button) findViewById(R.id.Setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JewelsActivity.class);
                intent.putExtra("score", 100000);
                intent.putExtra("time", 6000);
                startActivity(intent);
            }
        });
    }

    private void readAlarmList() {
        AlarmDatabaseHelper helper = new AlarmDatabaseHelper(this);
        alarmListArray = helper.getAllAlarms();
        Map<String, Object> tmp;
        alarmListItems = new ArrayList<Map<String, Object>>();
        for (Alarm a: alarmListArray) {
            tmp = new HashMap<String, Object>();
            tmp.put("alarmTime", a.getTimeStr());
            tmp.put("alarmWeek", a.getWeekStr());
            tmp.put("alarmMode", a.getModeStr());
            tmp.put("enableAlarm", a.isEnable());

            alarmListItems.add(tmp);
        }
        helper.close();
    }

    private void addNewAlarm() {
        //TODO go to the addAlarmActivity

        Intent intent = new Intent(this, AddAlarmActivity.class);
        startActivityForResult(intent, ADD__ALARM);
        /*
        Intent intent = new Intent(this, JewelsActivity.class);
        intent.putExtra("score", 100);
        intent.putExtra("time", 30);
        startActivity(intent);*/
    }

    private void editAlarm(int postion) {
        Intent intent = new Intent(this, AddAlarmActivity.class);
        intent.putExtra("alarm", alarmListArray.get(postion));
        startActivityForResult(intent, EDIT__ALARM);
    }

    public void setAlarmVisible() {
        if (alarmListItems.size() == 0) {
            noAlarmImage.setVisibility(View.VISIBLE);
            alarmList.setVisibility(View.INVISIBLE);
        } else {
            noAlarmImage.setVisibility(View.INVISIBLE);
            alarmList.setVisibility(View.VISIBLE);
        }
    }
    public void deleteAlarmIntent(Alarm alarm){
        ArrayList<Integer> days = alarm.getWeekBitmap();
        if(days.size() == 0){
            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm.getAlarmId()*10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
        else{
            for(int day: days){
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,alarm.getAlarmId()*10+day, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);}
            }

    }
    private void addAlarmForTest() {
        AlarmDatabaseHelper helper = new AlarmDatabaseHelper(this);
        helper.addAlarm(new Alarm(600, false, 0, 0, "", false));
        helper.addAlarm(new Alarm(60 * 8 + 50, true, 3, 0, "", true));
        helper.addAlarm(new Alarm(600, true, 5, 2, "", true));
        helper.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD__ALARM) {
                readAlarmList();
                alarmListAdapter.notifyDataSetChanged();
            } else if (requestCode == EDIT__ALARM) {
                readAlarmList();
                alarmListAdapter.notifyDataSetChanged();
            }
        }
    }
}
