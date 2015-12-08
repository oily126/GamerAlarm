package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import nyu.tandon.cs9033.gameralarm.R;

/**
 * Created by oily on 12/7/2015.
 */
public class SetRingtoneActivity extends Activity {
    private String selectedRingtone = null;
    private final static int customId = 2;
    private final static int RINGTONE_SELECTION = 1;
    private MediaPlayer player = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ringtone);
        ((Button) findViewById(R.id.cancelRingtoneBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        ((Button) findViewById(R.id.setRingtoneBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup g = (RadioGroup) findViewById(R.id.ringtoneGroup);
                Intent intent = new Intent(SetRingtoneActivity.this, AddAlarmActivity.class);
                RadioButton radioBtn = (RadioButton) findViewById(g.getCheckedRadioButtonId());
                if (!radioBtn.getText().toString().startsWith("Ringtone")) {
                    if (selectedRingtone != null) {
                        intent.putExtra("ringtone", selectedRingtone);
                    } else {
                        intent.putExtra("ringtone", "1");
                    }
                } else {
                    intent.putExtra("ringtone", radioBtn.getText().toString().substring("Ringtone".length()));
                }
                Log.i(this.getClass().toString(), "submit ringtone " + intent.getStringExtra("ringtone"));
                setResult(RESULT_OK, intent);
                if (player != null && player.isPlaying()) player.stop();
                finish();
            }
        });

        ((RadioButton) findViewById(R.id.searchMusic)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null && player.isPlaying()) player.stop();
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);//pick one from the list
                startActivityForResult(intent, RINGTONE_SELECTION);
            }
        });

        ((RadioButton) findViewById(R.id.ringtone1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    if (player.isPlaying()) player.stop();
                    player.release();
                }
                player = MediaPlayer.create(SetRingtoneActivity.this, R.raw.ringtone1);
                player.start();
            }
        });

        ((RadioButton) findViewById(R.id.ringtone2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    if (player.isPlaying()) player.stop();
                    player.release();
                }
                player = MediaPlayer.create(SetRingtoneActivity.this, R.raw.ringtone2);
                player.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RINGTONE_SELECTION) {
            if (resultCode == RESULT_OK) {
                String[] proj = {MediaStore.Audio.Media.DATA};
                Cursor c = managedQuery(data.getData(), proj, null, null, null);
                int musiccolumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                c.moveToFirst();
                String path = c.getString(musiccolumn);
                selectedRingtone = path;
                Log.i(this.getClass().toString(), "ringtone " + path);
            } else {
                ((RadioButton) findViewById(R.id.ringtone1)).setChecked(true);
            }
        }
    }
}
