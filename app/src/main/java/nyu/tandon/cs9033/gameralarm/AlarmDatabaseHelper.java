package nyu.tandon.cs9033.gameralarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import nyu.tandon.cs9033.gameralarm.models.Alarm;
import nyu.tandon.cs9033.gameralarm.models.QuizQuestions;

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

    public static final String TABLE_QUIZ = "quiz";
    private static final String COLUMN_QUESTION_ID = "qid";
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_OPTION1 = "option1";
    private static final String COLUMN_OPTION2 = "option2";
    private static final String COLUMN_OPTION3 = "option3";
    private static final String COLUMN_OPTION4 = "option4";
    //private static final String COLUMN_ANSWER = "answer";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_ANSWER_EXPLANATION = "answer_explanation";

    public AlarmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create alarm table
        db.execSQL("create table " + TABLE_ALARM + "("
                + COLUMN_ALARM_ID + " integer primary key autoincrement, "
                + COLUMN_ALARM_TIME + " integer, "
                + COLUMN_ALARM_REPEAT + " integer, "
                + COLUMN_ALARM_WEEK + " integer, "
                + COLUMN_ALARM_MODE + " integer, "
                + COLUMN_ALARM_RINGTONE + " varchar(100), "
                + COLUMN_ALARM_ENABLE + " integer)");

        db.execSQL("create table " + TABLE_QUIZ + "("
                + COLUMN_QUESTION_ID + " integer primary key autoincrement, "
                + COLUMN_QUESTION + " text, "
                + COLUMN_OPTION1 + " text, "
                + COLUMN_OPTION2 + " text, "
                + COLUMN_OPTION3 + " text, "
                + COLUMN_OPTION4 + " text, "
                + COLUMN_CATEGORY + " integer, "
                + COLUMN_ANSWER_EXPLANATION + " text)");

        populateQuizDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ);
        // create tables again
        onCreate(db);
    }

    public long addQuestion(QuizQuestions quizQuestions) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QUESTION, quizQuestions.getQuestion());
        cv.put(COLUMN_OPTION1, quizQuestions.getOption1());
        cv.put(COLUMN_OPTION2, quizQuestions.getOption2());
        cv.put(COLUMN_OPTION3, quizQuestions.getOption3());
        cv.put(COLUMN_OPTION4, quizQuestions.getOption4());
        //cv.put(COLUMN_ANSWER, quizQuestions.getAnswer());
        cv.put(COLUMN_CATEGORY, quizQuestions.getCategory());
        cv.put(COLUMN_ANSWER_EXPLANATION, quizQuestions.getAnswerExplanation());
        // return id of new quiz question
        return getWritableDatabase().insert(TABLE_QUIZ, null, cv);
    }

    public void addQuestion(SQLiteDatabase db, String question, String option1, String option2, String option3,
                            String option4, Integer category, String answerExplanation) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QUESTION, question);
        cv.put(COLUMN_OPTION1, option1);
        cv.put(COLUMN_OPTION2, option2);
        cv.put(COLUMN_OPTION3, option3);
        cv.put(COLUMN_OPTION4, option4);
        //cv.put(COLUMN_ANSWER, answer);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_ANSWER_EXPLANATION, answerExplanation);

        db.insert(TABLE_QUIZ, null, cv);
    }

    public void populateQuizDB(SQLiteDatabase db){
        this.addQuestion(db, "14*15=?", "210", "215", "225", "205", 1, null);
        this.addQuestion(db, "19*19=?", "361", "362", "365", "367", 1, null);
        this.addQuestion(db, "21*19=?", "399", "398", "391", "401", 1, null);
        this.addQuestion(db, "(54/3)*4=?", "72", "64", "80", "68", 1, null);
        this.addQuestion(db, "Who has the most championships in NBA players?",
                "Bill Russell", "Sam Jones", "Michael Jordan", "Tim Duncan", 2, null);
        this.addQuestion(db, "Name the only major team sport in the USA with no game clock? ",
                "Baseball", "Hockey", "Football", "Cricket", 2, null);
        this.addQuestion(db, "The American basketball team 'Hornets', represent which city?",
                "Charlotte", "New Orleans", "Phoenix", "Orlando", 2, null);
        this.addQuestion(db, "The Commissioner's Trophy is awarded to the winner of which league? ",
                "MLB", "NFL", "NBA", "NHL", 2, null);

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
        Log.i(AlarmDatabaseHelper.this.toString(), String.valueOf(id));
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
