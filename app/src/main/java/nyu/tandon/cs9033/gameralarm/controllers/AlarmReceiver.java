package nyu.tandon.cs9033.gameralarm.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import nyu.tandon.cs9033.gameralarm.R;

/**
 * Created by Zhe Wang on 10/31/2015.
 * */

//This class is used to receive the broadcast and play the ringtone.
public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        int mode = intent.getIntExtra("Mode", -1);
        String ringtone = intent.getStringExtra("ringtone");
        //normal mode

        if(mode/10 == 0){
            Log.d("mode = 0", "received");
            //Toast.makeText(context, "This a test alarm!", Toast.LENGTH_SHORT).show();
            //MediaPlayer player =  MediaPlayer.create(context, R.raw.ringtone1);
            //player.setLooping(true);
            //player.start();
            Intent nomalIntent = new Intent(context, NormalAlarmActivity.class);
            nomalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            nomalIntent.putExtra("ringtone", ringtone);
            context.startActivity(nomalIntent);
        }
        else if(mode/10 ==1 && mode % 10 == 0){
            Log.i(AddAlarmActivity.class.toString(), "received");
            Intent gameIntent = new Intent(context, BallGameActivity.class);
            gameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            gameIntent.putExtra("ringtone", ringtone);
            context.startActivity(gameIntent);
        } else if(mode/10 ==1 && mode % 10 == 1) {
            Intent gameIntent = new Intent(context, JewelsActivity.class);
            gameIntent.putExtra("score", 120);
            gameIntent.putExtra("time", 60);
            gameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            gameIntent.putExtra("ringtone", ringtone);
            context.startActivity(gameIntent);
        }

    }
}
