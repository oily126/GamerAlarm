package nyu.tandon.cs9033.gameralarm.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by oily on 11/17/2015.
 */
public class Alarm implements Parcelable{
    private static String[] weekday = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private static String[] modeStr = {"Normal", "GameMode", "QuizMode"};
    private static String[] gameStr = {"BallGame", "JewelGame"};
    private int alarmId;
    private int time;
    private boolean repeat;
    private int week;
    private int mode;
    private String ringtone;
    private boolean enable;
    private int timeLimit;
    private int scoreLimit;
    private int quesNum;
    private int rightQues;

    public Alarm(int time, boolean repeat, int week, int mode, String ringtone, boolean enable) {
        this.alarmId = 0;
        this.time = time;
        this.repeat = repeat;
        this.week = week;
        this.mode = mode;
        this.ringtone = ringtone;
        this.enable = enable;
    }

    public Alarm(int alarmId, int time, int repeat, int week, int mode, String ringtone, int enable ,
                 int timeLimit, int scoreLimit, int quesNum, int rightQues) {
        this.alarmId = alarmId;
        this.time = time;
        this.repeat = repeat != 0;
        this.week = week;
        this.mode = mode;
        this.ringtone = ringtone;
        this.enable = enable != 0;
        this.timeLimit = timeLimit;
        this.scoreLimit = scoreLimit;
        this.quesNum = quesNum;
        this.rightQues = rightQues;
    }

    public Alarm(int alarmId, int time, boolean repeat, Set<Integer> week, int mode, String ringtone, boolean enable,
                 int timeLimit, int scoreLimit, int quesNum, int rightQues) {
        this.alarmId = alarmId;
        this.time = time;
        this.repeat = repeat;
        this.week = 0;
        for (int a: week) {
            this.week += (int) Math.pow(2, a - 1);
        }
        this.mode = mode;
        this.ringtone = ringtone;
        this.enable = enable;
        this.timeLimit = timeLimit;
        this.scoreLimit = scoreLimit;
        this.quesNum = quesNum;
        this.rightQues = rightQues;
    }
    
    public Alarm(int time, boolean repeat, Set<Integer> week, int mode, String ringtone, boolean enable
                 ,int timeLimit, int scoreLimit, int quesNum, int rightQues) {
        this.alarmId = 0;
        this.time = time;
        this.repeat = repeat;
        this.week = 0;
        for (int a: week) {
            this.week += (int) Math.pow(2, a - 1);
        }
        this.mode = mode;
        this.ringtone = ringtone;
        this.enable = enable;
        this.timeLimit = timeLimit;
        this.scoreLimit = scoreLimit;
        this.quesNum = quesNum;
        this.rightQues = rightQues;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public int getTime() {
        return time;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public int getWeek() {
        return week;
    }

    public int getMode() {
        return mode;
    }

    public String getRingtone() {
        return ringtone;
    }

    public boolean isEnable() {
        return enable;
    }

    public int getTimeLimit(){
        return  timeLimit;
    }

    public int getScoreLimit(){
        return  scoreLimit;
    }

    public int getQuesNum(){
        return quesNum;
    }

    public int getRightQues(){
        return rightQues;
    }

    public String getTimeStr() {
        if (time % 100 < 10) {
            if (time / 100 < 10) return "0" + String.valueOf(time / 100) + ":0" +  String.valueOf(time % 100);
            else return String.valueOf(time / 100) + ":0" +  String.valueOf(time % 100);
        } else {
            if (time / 100 < 10) return "0" + String.valueOf(time / 100) + ":" +  String.valueOf(time % 100);
            else return String.valueOf(time / 100) + ":" +  String.valueOf(time % 100);
        }
    }

    public String getWeekStr() {
        if (!isRepeat()) return "Once";
        else {
            int cur = week, i = 0;
            String s = new String();
            while (cur > 0) {
                if (cur % 2 == 1) {
                    s += weekday[i] + " ";
                }
                i += 1;
                cur /= 2;
            }
            return s;
        }
    }

    public String getModeStr() {
        switch (mode / 10) {
            case 0: return modeStr[0];
            case 1: return gameStr[mode % 10];
            case 2: return modeStr[2];
            default: return "";
        }
    }

    public ArrayList<Integer> getWeekBitmap() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int cur = week, i = 1;
        while (cur > 0) {
            if (cur % 2 != 0) list.add(i);
            cur /= 2;
            i++;
        }
        return list;
    }

    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
        public Alarm createFromParcel(Parcel p) {
            return new Alarm(p);
        }

        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    public Alarm(Parcel p) {
        this.alarmId = p.readInt();
        this.time = p.readInt();
        this.repeat = p.readInt() == 1;
        this.week = p.readInt();
        this.mode = p.readInt();
        this.ringtone = p.readString();
        this.enable = p.readInt() == 1;
        this.timeLimit = p.readInt();
        this.scoreLimit = p.readInt();
        this.quesNum = p.readInt();
        this.rightQues = p.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(alarmId);
        dest.writeInt(time);
        if (repeat) dest.writeInt(1); else dest.writeInt(0);
        dest.writeInt(week);
        dest.writeInt(mode);
        dest.writeString(ringtone);
        if (enable) dest.writeInt(1); else dest.writeInt(0);
        dest.writeInt(timeLimit);
        dest.writeInt(scoreLimit);
        dest.writeInt(quesNum);
        dest.writeInt(rightQues);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
