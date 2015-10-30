package nyu.tandon.cs9033.gameralarm.controllers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import nyu.tandon.cs9033.gameralarm.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, BallGameActivity.class);
        startActivity(intent);
    }
}
