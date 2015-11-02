package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import nyu.tandon.cs9033.gameralarm.R;

/**
 * Created by oily on 11/1/2015.
 */
public class AlarmPlayerService extends Service{
    private static final String TAG = "AlarmPlayerService";
    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flagId, int startId)
    {
        super.onStartCommand(intent, flagId, startId);
        player = MediaPlayer.create(this, R.raw.ringtone1);
        player.setLooping(true);
        player.start();
        return START_NOT_STICKY;
    }
    public void onDestroy()
    {
        super.onDestroy();
        if(player != null){
            player.stop();
        }
    }
}
