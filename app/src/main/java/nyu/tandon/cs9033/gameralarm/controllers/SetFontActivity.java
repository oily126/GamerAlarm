package nyu.tandon.cs9033.gameralarm.controllers;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import nyu.tandon.cs9033.gameralarm.R;

/**
 * Created by oily on 12/3/2015.
 */
public class SetFontActivity extends Activity {
    private static int color[] = {Color.BLACK, Color.WHITE, Color.BLUE, Color.YELLOW, Color.GRAY, Color.GREEN};
    private static String colorName[] = {"BLACK", "WHITE", "BLUE", "YELLOW", "GRAY","GREEN"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_font);

        ((TextView) findViewById(R.id.color0)).setTextColor(color[0]);
        ((TextView) findViewById(R.id.color1)).setTextColor(color[1]);
        ((TextView) findViewById(R.id.color1)).setBackgroundColor(color[4]);
        ((TextView) findViewById(R.id.color2)).setTextColor(color[2]);
        ((TextView) findViewById(R.id.color3)).setTextColor(color[3]);
        ((TextView) findViewById(R.id.color4)).setTextColor(color[4]);
        ((TextView) findViewById(R.id.color5)).setTextColor(color[5]);

        ((TextView) findViewById(R.id.color0)).setText(colorName[0]);
        ((TextView) findViewById(R.id.color1)).setText(colorName[1]);
        ((TextView) findViewById(R.id.color2)).setText(colorName[2]);
        ((TextView) findViewById(R.id.color3)).setText(colorName[3]);
        ((TextView) findViewById(R.id.color4)).setText(colorName[4]);
        ((TextView) findViewById(R.id.color5)).setText(colorName[5]);

        ((TextView) findViewById(R.id.color0)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fontColor = SetFontActivity.this.getColor()[0];
                setResult(RESULT_OK);
                finish();
            }
        });

        ((TextView) findViewById(R.id.color1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fontColor = SetFontActivity.this.getColor()[1];
                setResult(RESULT_OK);
                finish();
            }
        });

        ((TextView) findViewById(R.id.color2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fontColor = SetFontActivity.this.getColor()[2];
                setResult(RESULT_OK);
                finish();
            }
        });

        ((TextView) findViewById(R.id.color3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fontColor = SetFontActivity.this.getColor()[3];
                setResult(RESULT_OK);
                finish();
            }
        });

        ((TextView) findViewById(R.id.color4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fontColor = SetFontActivity.this.getColor()[4];
                setResult(RESULT_OK);
                finish();
            }
        });

        ((TextView) findViewById(R.id.color5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fontColor = SetFontActivity.this.getColor()[5];
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    public static int[] getColor() {
        return color;
    }
}
