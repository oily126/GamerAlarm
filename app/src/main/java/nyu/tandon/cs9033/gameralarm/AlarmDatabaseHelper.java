package nyu.tandon.cs9033.gameralarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import nyu.tandon.cs9033.gameralarm.models.Alarm;

/**
 * Created by oily on 11/17/2015.
 */
public class AlarmDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "gameralarm";
    private static final String TABLE_ALARM = "alarm";
    private static final String COLUMN_ALARM_ID = "id";
    private static final String COLUMN_ALARM_TIME = "time";
    private static final String COLUMN_ALARM_REPEAT = "repeat";
    private static final String COLUMN_ALARM_WEEK = "week";
    private static final String COLUMN_ALARM_MODE = "mode";
    private static final String COLUMN_ALARM_RINGTONE = "ringtone";
    private static final String COLUMN_ALARM_ENABLE = "enable";

    public AlarmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create alarm table
        db.execSQL("create table " + TABLE_ALARM + "("
                + COLUMN_ALARM_ID + " long primary key, "
                + COLUMN_ALARM_TIME + " integer, "
                + COLUMN_ALARM_REPEAT + " integer, "
                + COLUMN_ALARM_WEEK + " integer, "
                + COLUMN_ALARM_MODE + " integer, "
                + COLUMN_ALARM_RINGTONE + " varchar(100), "
                + COLUMN_ALARM_ENABLE + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);

        // create tables again
        onCreate(db);
    }

    public ArrayList<Alarm> getAllAlarms() {
        ArrayList<Alarm> list = new ArrayList<Alarm>();
        String name[] = new String[]{
                COLUMN_ALARM_ID,
                COLUMN_ALARM_TIME,
                COLUMN_ALARM_REPEAT,
                COLUMN_ALARM_WEEK,
                COLUMN_ALARM_MODE,
                COLUMN_ALARM_RINGTONE,
                COLUMN_ALARM_ENABLE
        };
        Cursor c = getReadableDatabase().
                query(TABLE_ALARM, name, null, null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isLast()) {
                list.add(new Alarm(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), c.getString(5), c.getInt(6)));
                c.moveToNext();
            }
            list.add(new Alarm(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), c.getString(5), c.getInt(6)));
            return list;
        } else return list;
    }

    public long addAlarm(Alarm a) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALARM_TIME, a.getTime());
        if (a.isRepeat()) cv.put(COLUMN_ALARM_REPEAT, 1); else cv.put(COLUMN_ALARM_REPEAT, 0);
        cv.put(COLUMN_ALARM_WEEK, a.getWeek());
        cv.put(COLUMN_ALARM_MODE, a.getMode());
        cv.put(COLUMN_ALARM_RINGTONE, a.getRingtone());
        if (a.isEnable()) cv.put(COLUMN_ALARM_ENABLE, 1); else cv.put(COLUMN_ALARM_ENABLE, 0);
        return getWritableDatabase().insert(TABLE_ALARM, null, cv);
    }

    public int deleteAlarmById(int id) {
        String where = COLUMN_ALARM_ID + "=?";
        String[] arg = new String[] {
            String.valueOf(id)
        };
        return getWritableDatabase().delete(TABLE_ALARM, where, arg);
    }

    public int updateAlarm(Alarm a) {
        ContentValues cv = new ContentValues();
        String where = COLUMN_ALARM_ID + "=?";
        String[] arg = new String[] {
                String.valueOf(a.getAlarmId())
        };
        cv.put(COLUMN_ALARM_TIME, a.getTime());
        if (a.isRepeat()) cv.put(COLUMN_ALARM_REPEAT, 1); else cv.put(COLUMN_ALARM_REPEAT, 0);
        cv.put(COLUMN_ALARM_WEEK, a.getWeek());
        cv.put(COLUMN_ALARM_MODE, a.getMode());
        cv.put(COLUMN_ALARM_RINGTONE, a.getRingtone());
        if (a.isEnable()) cv.put(COLUMN_ALARM_ENABLE, 1); else cv.put(COLUMN_ALARM_ENABLE, 0);
        return getWritableDatabase().update(TABLE_ALARM, cv, where, arg);
    }
}
