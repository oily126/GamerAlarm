package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nyu.tandon.cs9033.gameralarm.AlarmDatabaseHelper;
import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.models.Alarm;
import nyu.tandon.cs9033.gameralarm.views.AlarmListAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static ArrayList<Alarm> alarmListArray;
    private List<Map<String, Object>> alarmListItems = new ArrayList<Map<String, Object>>();;
    private AlarmListAdapter alarmListAdapter;
    private ListView alarmList;
    private ImageView noAlarmImage;
    private final static int ADD__ALARM = 1;
    private final static int EDIT__ALARM = 2;
    private final static int BACKGOUND_SELECTION = 3;
    private final static int SET_FONT_COLOR = 4;
    public static Drawable bgPic = null;
    public static String bgPicLocation = null;
    public static int bgColor = Color.WHITE;
    public static int fontColor = Color.GRAY;
    public static final String PREF_BGPIC = "bgpic";
    public static final String PREF_FONT_COLOR = "fontcolor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //addAlarmForTest();
        readAlarmList();
        if (bgPicLocation == null) {
            bgPicLocation = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(PREF_BGPIC, bgPicLocation);
            if (bgPicLocation != null) {
                setPicAsBackground(bgPicLocation);
            }
        }
        fontColor = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(PREF_FONT_COLOR, fontColor);
        Log.i(TAG, String.valueOf(PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(PREF_FONT_COLOR, 0)));
        if (MainActivity.bgPic != null) {
            ((RelativeLayout) findViewById(R.id.welcomeScreen)).setBackground(MainActivity.bgPic);
            setFontColor();
        } else {
            ((RelativeLayout) findViewById(R.id.welcomeScreen)).setBackgroundColor(MainActivity.bgColor);
            setFontColor();
        }
        alarmList = (ListView) findViewById(R.id.alarmList);
        alarmListAdapter = new AlarmListAdapter(this, alarmListItems);
        alarmList.setAdapter(alarmListAdapter);
        alarmListAdapter.bindListView(alarmList);
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
                return true;
            }
        });

        alarmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.this.editAlarm(position);
            }
        });


        noAlarmImage = (ImageView) findViewById(R.id.noAlarm);
        noAlarmImage.setImageDrawable(getResources().getDrawable(R.drawable.mainpage_alarm));

        setAlarmVisible();

        final Button btn_add = (Button) findViewById(R.id.addAlarm);
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
                AlertDialog diag = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Setting")
                        .setItems(R.array.setting_items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: //background
                                        MainActivity.this.setUserBackground();
                                        break;
                                    case 1: //background restore default
                                        ((RelativeLayout) findViewById(R.id.welcomeScreen)).setBackgroundColor(MainActivity.bgColor);
                                        MainActivity.bgPic = null;
                                        MainActivity.bgPicLocation = null;
                                        break;
                                    case 2: //set font color
                                        Intent intent = new Intent(MainActivity.this, SetFontActivity.class);
                                        startActivityForResult(intent, SET_FONT_COLOR);
                                        break;
                                    case 3: //about
                                        MainActivity.this.getAbout();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
                WindowManager.LayoutParams layoutParams = diag.getWindow().getAttributes();
                layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                diag.getWindow().setAttributes(layoutParams);
            }
        });
    }

    private void readAlarmList() {
        AlarmDatabaseHelper helper = new AlarmDatabaseHelper(this);
        alarmListArray = helper.getAllAlarms();
        Map<String, Object> tmp;
        alarmListItems.clear();
        for (Alarm a : alarmListArray) {
            tmp = new HashMap<String, Object>();
            tmp.put("alarmId", a.getAlarmId());
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

    public void deleteAlarmIntent(Alarm alarm) {
        ArrayList<Integer> days = alarm.getWeekBitmap();
        if (days.size() == 0) {
            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm.getAlarmId() * 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        } else {
            for (int day : days) {
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm.getAlarmId() * 10 + day, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }
        }

    }

    private void addAlarmForTest() {
        AlarmDatabaseHelper helper = new AlarmDatabaseHelper(this);
        helper.addAlarm(new Alarm(600, false, 0, 0, "", false));
        helper.addAlarm(new Alarm(60 * 8 + 50, true, 3, 0, "", true));
        helper.addAlarm(new Alarm(600, true, 5, 2, "", true));
        helper.close();
    }

    private void setUserBackground() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//pick one from the list
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");//get all the image in the file system
        startActivityForResult(intent, BACKGOUND_SELECTION);
    }

    private void getAbout() {
        new AlertDialog.Builder(this)
                .setTitle("About the GamerAlarm")
                .setMessage("This app is developed by Zhiyuan Hu, Baicheng Zhang, Zhe Wang.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void setPicAsBackground(String path) {
        Bitmap bm = BitmapFactory.decodeFile(path);
        bgPicLocation = path;
        bgPic = new BitmapDrawable(this.getResources(), zoomImg(bm, 800, 450));
        ((RelativeLayout) findViewById(R.id.welcomeScreen)).setBackground(bgPic);
    }

    public  Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD__ALARM) {
                readAlarmList();
                setAlarmVisible();
                alarmListAdapter.notifyDataSetChanged();
            } else if (requestCode == EDIT__ALARM) {
                readAlarmList();
                setAlarmVisible();
                alarmListAdapter.notifyDataSetChanged();
            } else if (requestCode == BACKGOUND_SELECTION) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor c = managedQuery(data.getData(), proj, null, null, null);
                int photocolumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                c.moveToFirst();
                String path = c.getString(photocolumn);
                setPicAsBackground(path);
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putString(PREF_BGPIC, path)
                        .commit();
            } else if (requestCode == SET_FONT_COLOR) {
                setFontColor();
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putInt(PREF_FONT_COLOR, fontColor)
                        .commit();
            }
        }
    }

    public static void disableAlarm(Context context, int id) {
        if (alarmListArray.size() <= id) return;
        Alarm alarm = alarmListArray.get(id);
        ArrayList<Integer> days = alarm.getWeekBitmap();
        if (days.size() == 0) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getAlarmId() * 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        } else {
            for (int day : days) {
                Intent intent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getAlarmId() * 10 + day, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }
        }
        AlarmDatabaseHelper helper = new AlarmDatabaseHelper(context);
        alarm.setEnable(false);
        helper.updateAlarm(alarm);
        helper.close();
    }

    public static void enableAlarm(Context context, int id) {
        if (alarmListArray.size() <= id) return;
        Alarm alarm = alarmListArray.get(id);
        ArrayList<Integer> days = alarm.getWeekBitmap();
        if(days.size() ==0 ){
            setAlarm(context, alarm.getAlarmId() * 10, 0, alarm);
        } else {
            for (int day : days) {
                Log.i(AddAlarmActivity.TAG, String.valueOf(day));
                setAlarm(context, alarm.getAlarmId() * 10 + day, day, alarm);
            }

        }
        AlarmDatabaseHelper helper = new AlarmDatabaseHelper(context);
        alarm.setEnable(true);
        helper.updateAlarm(alarm);
        helper.close();
    }

    private static void setAlarm(Context context, int id, int dayOfWeek, Alarm alarm) {
        Calendar calNow = Calendar.getInstance();
        Calendar alarmCal = (Calendar) calNow.clone();

        if(dayOfWeek !=0)
            alarmCal.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        alarmCal.set(Calendar.HOUR_OF_DAY, alarm.getTime() / 100);
        alarmCal.set(Calendar.MINUTE, alarm.getTime() % 100);
        alarmCal.set(Calendar.SECOND, 0);
        alarmCal.set(Calendar.MILLISECOND, 0);

        long alarmTime;
        if(dayOfWeek == 0)
            alarmTime = alarmCal.getTimeInMillis()>=System.currentTimeMillis()? alarmCal.getTimeInMillis(): alarmCal.getTimeInMillis()+24*3600*1000;
        else
            alarmTime = alarmCal.getTimeInMillis()>=System.currentTimeMillis()? alarmCal.getTimeInMillis(): alarmCal.getTimeInMillis()+24*3600*1000*7;
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("Mode", alarm.getMode()); //should pass the alarm to the receiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i(AddAlarmActivity.TAG, "the alarmtime is " +String.valueOf(alarmTime));
        Log.i(AddAlarmActivity.TAG, "current time is " + String.valueOf(System.currentTimeMillis()));
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(alarm.isRepeat())
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 24*60*60*1000*7, pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    private void setFontColor() {
        ((TextView) findViewById(R.id.textView)).setTextColor(MainActivity.fontColor);
        if (alarmListAdapter != null) {
            alarmListAdapter = new AlarmListAdapter(this, alarmListItems);
            alarmList.setAdapter(alarmListAdapter);
            alarmListAdapter.bindListView(alarmList);
        }
    }
}
