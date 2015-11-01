package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.AlertDialog;
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
import java.util.Objects;

import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.view.AlarmListAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<Map<String, Object>> alarmListItems;
    private AlarmListAdapter alarmListAdapter;
    private ListView alarmList;
    private ImageView noAlarmImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readAlarmList();
        alarmList = (ListView) findViewById(R.id.alarmList);
        alarmListAdapter = new AlarmListAdapter(this, alarmListItems);
        alarmList.setAdapter(alarmListAdapter);
        alarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int alarmToDelete = position;
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
                                alarmListItems.remove(alarmToDelete);
                                alarmListAdapter.notifyDataSetChanged();
                                setAlarmVisible();
                            }
                        })
                        .show();
                return false;
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
    }

    private void readAlarmList() {
        Map<String, Object> tmp = new HashMap<String, Object>();
        tmp.put("alarmTime", "10:30");
        tmp.put("alarmWeek", "Mon Sun");
        tmp.put("alarmMode", "Normal");
        tmp.put("enableAlarm", true);

        alarmListItems = new ArrayList<Map<String, Object>>();
        alarmListItems.add(tmp);

        tmp = new HashMap<String, Object>();
        tmp.put("alarmTime", "18:50");
        tmp.put("alarmWeek", "Mon to Sun");
        tmp.put("alarmMode", "BallGame");
        tmp.put("enableAlarm", false);
        alarmListItems.add(tmp);
    }

    private void addNewAlarm() {
        //TODO go to the addAlarmActivity
        Intent intent = new Intent(this, AddAlarmActivity.class);
        startActivity(intent);
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
}
