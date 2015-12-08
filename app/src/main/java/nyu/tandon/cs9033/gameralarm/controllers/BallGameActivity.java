package nyu.tandon.cs9033.gameralarm.controllers;
/**
 * Created by Byron on 10/30/15.
 */

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.views.BallGameView;


public class BallGameActivity extends AppCompatActivity {

    private BallGameView BGV_instance = null;
    private MediaPlayer player = null;
    private int TIME_LIMIT = 90000;
    private static Timer timeLimit;
    private static TimerTask timeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        TIME_LIMIT = intent.getIntExtra("TimeLimit", TIME_LIMIT);

        Log.i("BallGame", "Create");
        //Play game in full size of screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        createMediaPlayer(getIntent().getStringExtra("ringtone"));
        player.start();

        //Play game in horizontal screen
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        BGV_instance = new BallGameView(this);
        setContentView(BGV_instance);

        // Set a timer to countdown of the time limit
        timeLimit = new Timer();
        timeTask = new TimerTask() {
            @Override
            public void run() {
                timeLimit.cancel();
                player.stop();
                player.release();
                player = null;
                Intent startNormal = new Intent(BallGameActivity.this, NormalAlarmActivity.class);
                //startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startNormal);
                finish();
            }
        };
        timeLimit.schedule(timeTask, TIME_LIMIT);

        // Check game status every 10 milliseconds
        /*final Timer checkTime = new Timer();
        TimerTask checkTask = new TimerTask() {
            @Override
            public void run() {
                if (!BGV_instance.mIsRunning) {
                    checkTime.cancel();
                    timeLimit.cancel();
                    player.stop();
                    player.release();
                    player = null;
                    finish();
                }
            }
        };
        checkTime.schedule(checkTask, 0, 10);*/
    }

    public void gameFinish() {
        timeTask.cancel();
        timeLimit.cancel();
        player.stop();
        player.release();
        player = null;
        finish();
    }

    // Disable Back, Menu and "Recent apps" buttons
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return false;
            case KeyEvent.KEYCODE_MENU:
                return false;
            case KeyEvent.KEYCODE_APP_SWITCH:
                return false;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onPause() {
        Log.i("BallGame", "Pause");
        if (player != null) player.pause();
        BGV_instance.mIsRunning = false;
        super.onPause();
        //finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("BallGame", "Resume");
        if (player == null) {
            createMediaPlayer(getIntent().getStringExtra("ringtone"));
            player.setLooping(true);
            player.start();
        }
        if (BGV_instance == null) {
            BGV_instance = new BallGameView(this);
            setContentView(BGV_instance);
            BGV_instance.mIsRunning = true;
        }
    }

    private void createMediaPlayer(String path) {
        try {
            int id = Integer.parseInt(path);
            if (id == 1) {
                player = MediaPlayer.create(this, R.raw.ringtone1);
            } else {
                if (id == 2) {
                    player = MediaPlayer.create(this, R.raw.ringtone1);
                }
            }
        } catch (NumberFormatException e) {
            try {
                player = new MediaPlayer();
                player.setDataSource(path);
                player.prepare();
            } catch (IOException e1) {
                player = MediaPlayer.create(this, R.raw.ringtone1);
            }
        }
    }

}
