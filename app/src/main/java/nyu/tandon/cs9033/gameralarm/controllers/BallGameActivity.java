package nyu.tandon.cs9033.gameralarm.controllers;
/**
 * Created by Byron on 10/30/15.
 */

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.views.BallGameView;


public class BallGameActivity extends AppCompatActivity {

    private BallGameView BGV_instance = null;
    private MediaPlayer player = null;
    private int TIME_LIMIT = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        TIME_LIMIT = intent.getParcelableExtra("TimeLimit");

        Log.i("BallGame", "Create");
        //Play game in full size of screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        player =  MediaPlayer.create(this, R.raw.ringtone1);
        player.start();

        //Play game in horizontal screen
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        BGV_instance = new BallGameView(this);
        setContentView(BGV_instance);

        // Set a timer to countdown of the time limit
        final Timer timeLimit = new Timer();
        TimerTask timeTask = new TimerTask() {
            @Override
            public void run() {
                timeLimit.cancel();
                player.stop();
                Intent startNormal = new Intent(BallGameActivity.this, NormalAlarmActivity.class);
                //startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startNormal);
                finish();
            }
        };
        timeLimit.schedule(timeTask, TIME_LIMIT);

        // Check game status every 10 milliseconds
        final Timer checkTime = new Timer();
        TimerTask checkTask = new TimerTask() {
            @Override
            public void run() {
                if (!BGV_instance.mIsRunning) {
                    checkTime.cancel();
                    timeLimit.cancel();
                    player.stop();
                    finish();
                }
            }
        };
        checkTime.schedule(checkTask, 0, 10);
    }


    // Disable Back and Menu buttons
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return false;
            case KeyEvent.KEYCODE_MENU:
                return false;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    // Disable "Recent apps button", only support API>11
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("BallGame", "Pause");
        if (player != null) player.pause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activityManager.moveTaskToFront(getTaskId(), 0);
        }

        BGV_instance.mIsRunning = false;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("BallGame", "Resume");
        if (player == null) {
            player =  MediaPlayer.create(this, R.raw.ringtone1);
            player.setLooping(true);
        }
        player.start();
        if (BGV_instance == null) {
            BGV_instance = new BallGameView(this);
            setContentView(BGV_instance);
            BGV_instance.mIsRunning = true;
        }
    }
}
