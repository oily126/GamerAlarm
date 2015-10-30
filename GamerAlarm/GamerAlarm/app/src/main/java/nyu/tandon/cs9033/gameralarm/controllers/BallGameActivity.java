package nyu.tandon.cs9033.gameralarm.controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
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
    }




}
