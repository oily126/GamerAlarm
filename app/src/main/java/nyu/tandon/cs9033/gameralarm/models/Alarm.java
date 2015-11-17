package nyu.tandon.cs9033.gameralarm.models;

/**
 * Created by oily on 11/17/2015.
 */
public class Alarm {
    private static String[] weekday = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static String[] modeStr = {"Normal", "BallGame", "Tetris", "Math"};
    private int alarmId;
    private long time;
    private boolean repeat;
    private int week;
    private int mode;
    private String ringtone;
    private boolean enable;

    public Alarm(long time, boolean repeat, int week, int mode, String ringtone, boolean enable) {
        this.alarmId = 0;
        this.time = time;
        this.repeat = repeat;
        this.week = week;
        this.mode = mode;
        this.ringtone = ringtone;
        this.enable = enable;
    }

    public Alarm(int alarmId, long time, int repeat, int week, int mode, String ringtone, int enable) {
        this.alarmId = alarmId;
        this.time = time;
        if (repeat == 0) this.repeat = false; else this.repeat = true;
        this.week = week;
        this.mode = mode;
        this.ringtone = ringtone;
        if (enable == 0) this.enable = false; else this.enable = true;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public long getTime() {
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

    public String getTimeStr() {
        if (time % 60 < 10) {
            return  String.valueOf(time / 60) + ":0" + String.valueOf(time % 60);
        } else {
            return String.valueOf(time / 60) + ":" +  String.valueOf(time % 60);
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
        return modeStr[mode];
    }
}
