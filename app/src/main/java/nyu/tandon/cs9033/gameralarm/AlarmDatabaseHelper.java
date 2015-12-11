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
    private static final String COLUMN_ALARM_TIMELIMIT = "timelimit";
    private static final String COLUMN_ALARM_SCORELIMIT = "scorelimit";
    private static final String COLUMN_ALARM_QUESNUM = "quesnum";
    private static final String COLUMN_ALARM_RIGHTQUES = "rightques";



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
                + COLUMN_ALARM_ENABLE + " integer, "
                + COLUMN_ALARM_TIMELIMIT+ " integer, "
                + COLUMN_ALARM_SCORELIMIT + " integer, "
                + COLUMN_ALARM_QUESNUM  + " integer, "
                + COLUMN_ALARM_RIGHTQUES + " integer)"
        );

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
		int i, j, k, l, op;
		int ans[] = new int[4];
		boolean accept = false;
		String problem = new String();
		for (i = 10; i <= 20; i++) {
			for (j = 11; j <= 20; j++) {
				for (op = 0; op < 4; op++) {
                    if (op == 3 && i % j != 0) break;
					switch(op) {
						case 0:
							ans[0] = i + j;
							problem = String.valueOf(i) + "+" + String.valueOf(j) + "=?";
							break;
						case 1:
							ans[0] = i - j;
							problem = String.valueOf(i) + "-" + String.valueOf(j) + "=?";
							break;
						case 2:
							ans[0] = i * j;
							problem = String.valueOf(i) + "*" + String.valueOf(j) + "=?";
							break;
						case 3:
							ans[0] = i / j;
							problem = String.valueOf(i) + "/" + String.valueOf(j) + "=?";
							break;
						default:
							break;							
					}
					for (k = 1; k < 4; k++) {
						do {
							accept = true;
							if (Math.random() > 0.5) ans[k] = ans[0] + (int)(Math.random() * 50 + 1);
							else ans[k] = ans[0] + (int)(Math.random() * 50 + 1);
							for (l = 0; l < k; l++) {
								if (ans[l] == ans[k]) accept = false;
								break;
							}
						} while (!accept);						
					}
					this.addQuestion(db, problem, String.valueOf(ans[0]), String.valueOf(ans[1]), String.valueOf(ans[2]), String.valueOf(ans[3]), 1, null);
				}
			}
		}

        this.addQuestion(db, "Who has the most championships in NBA players?",
                "Bill Russell", "Sam Jones", "Michael Jordan", "Tim Duncan", 2, null);
        this.addQuestion(db, "Name the only major team sport in the USA with no game clock? ",
                "Baseball", "Hockey", "Football", "Cricket", 2, null);
        this.addQuestion(db, "The American basketball team 'Hornets', represent which city?",
                "Charlotte", "New Orleans", "Phoenix", "Orlando", 2, null);
        this.addQuestion(db, "The Commissioner's Trophy is awarded to the winner of which league? ",
                "MLB", "NFL", "NBA", "NHL", 2, null);
        this.addQuestion(db, "James Naismith invented which sport? ",
                "Basketball", "Hockey", "Football", "Cricket", 2, null);
        this.addQuestion(db, "How many years must a player be retired to be eligible for the Pro Football Hall of Fame? ",
                "Five", "Four", "Six", "Ten", 2, null);
        this.addQuestion(db, "How many football teams play in the Big Ten Conference? ",
                "Eleven", "Ten", "Nine", "Eight", 2, null);
        this.addQuestion(db, "How many of the five Dallas teams Tom Landry took to Super Bowls were victorious? ",
                "Two", "Four", "Three", "One", 2, null);
        this.addQuestion(db, "Who is the only coach to win both a NCAA and NBA championship? ",
                "Larry Brown", "John Calipari", "Gregg Popovich", "Rick Pitino", 2, null);
        this.addQuestion(db, "What City Are the Pelican in?",
                "New Orleans", "Phoenix", "Orlando", "Miami", 2, null);
        this.addQuestion(db, "How Many championships have the Boston Celtics won? ",
                "21", "7", "18", "25", 2, null);
        this.addQuestion(db, "What team has won the most NBA titles? ",
                "The Boston Celtics.", "The Philadelphia 76ers.", "The Chicago Bulls.", "The Los Angeles Lakers.", 2, null);
        this.addQuestion(db, "Michael Jordan is famous for wearing number 23. What other number has MJ worn during his NBA career? ",
                "45", "32", "67", "99", 2, null);
        this.addQuestion(db, "Who won the NBA's 2004 Slam Dunk Contest? ",
                "Fred Jones.", "Lebron James.", "Vince Carter", "Jason Richardson.", 2, null);
        this.addQuestion(db, "Which of the following is NOT the nickname of an NBA player? ",
                "Dynamite.", "Big dog", "The answer", "The Truth", 2, null);
        this.addQuestion(db, "Who is Dwight Howard favorite superhero? ",
                "Superman", "Batman", "Ironman", "Spiderman", 2, null);
        this.addQuestion(db, "Who is the NBA's all-time leading scorer? ",
                "Kareem Abdul-Jabbar.", "Michael Jordan.", "Karl Malone.", "Wilt Chamberlain.", 2, null);
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
                COLUMN_ALARM_ENABLE,
                COLUMN_ALARM_TIMELIMIT,
                COLUMN_ALARM_SCORELIMIT,
                COLUMN_ALARM_QUESNUM,
                COLUMN_ALARM_RIGHTQUES
        };
        Cursor c = getReadableDatabase().
                query(TABLE_ALARM, name, null, null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isLast()) {
                list.add(new Alarm(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), c.getString(5), c.getInt(6),
                        c.getInt(7),c.getInt(8),c.getInt(9),c.getInt(10)));
                c.moveToNext();
            }
            list.add(new Alarm(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), c.getString(5), c.getInt(6),
                    c.getInt(7),c.getInt(8),c.getInt(9),c.getInt(10)));
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
        cv.put(COLUMN_ALARM_TIMELIMIT, a.getTimeLimit());
        cv.put(COLUMN_ALARM_SCORELIMIT, a.getScoreLimit());
        cv.put(COLUMN_ALARM_QUESNUM, a.getQuesNum());
        cv.put(COLUMN_ALARM_RIGHTQUES, a.getRightQues());
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
        cv.put(COLUMN_ALARM_TIMELIMIT, a.getTimeLimit());
        cv.put(COLUMN_ALARM_SCORELIMIT, a.getScoreLimit());
        cv.put(COLUMN_ALARM_QUESNUM, a.getQuesNum());
        cv.put(COLUMN_ALARM_RIGHTQUES, a.getRightQues());
        return getWritableDatabase().update(TABLE_ALARM, cv, where, arg);
    }
}
