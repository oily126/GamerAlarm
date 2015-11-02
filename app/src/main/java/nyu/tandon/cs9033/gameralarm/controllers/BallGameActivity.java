package nyu.tandon.cs9033.gameralarm.controllers;
/**
 * Created by Byron on 10/30/15.
 */

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import nyu.tandon.cs9033.gameralarm.views.BallGameView;


public class BallGameActivity extends AppCompatActivity {

    private BallGameView BGV_instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //Play game in full size of screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                //Intent startMain = new Intent(BallGameActivity.this, MainActivity.class);
                //startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(startMain);
                finish();
            }
        };
        timeLimit.schedule(timeTask, 16000);

        // Check game status every 10 milliseconds
        final Timer checkTime = new Timer();
        TimerTask checkTask = new TimerTask() {
            @Override
            public void run() {
                if (!BGV_instance.mIsRunning) {
                    checkTime.cancel();
                    //Intent startMain = new Intent(BallGameActivity.this, MainActivity.class);
                    //startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(startMain);
                    finish();
                }
            }
        };
        timeLimit.schedule(checkTask, 0, 10);
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

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activityManager.moveTaskToFront(getTaskId(), 0);
        }

        BGV_instance.mIsRunning = false;

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        BGV_instance.mIsRunning = true;
    }

}
