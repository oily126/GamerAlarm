package nyu.tandon.cs9033.gameralarm.views;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.controllers.AddAlarmActivity;

/**
 * Created by Byron on 11/25/15.
 */
public class FunModePreviewFragment extends Fragment {
    private CustomizedViewPager viewPager;
    private ViewGroup viewGroup;
    private AddAlarmActivity addAlarmActivity;
    private ImageView[] tips;
    private PageChangeListener pageChangeListener;
    private ImageView[] mImageViews;
    private List<Integer> imgIdArray = new ArrayList<Integer>();
    private Button setModeButton;
    private SeekBar timeLimit;
    private SeekBar scoreLimit;
    private TextView timeLimitText;
    private TextView scoreLimitText;
    private int timeLimitProgress = 90;
    private int scoreLimitProgress = 140;
    private int TIME_LIMIT_STEP = 5;
    private int TIME_LIMIT_MAX = 180;
    private int TIME_LIMIT_MIN = 60;
    private int SCORE_LIMIT_STEP = 20;
    private int SCORE_LIMIT_MAX = 500;
    private int SCORE_LIMIT_MIN = 120;
    private int mode = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.game_preview_fragment, container, false);
        viewGroup = (ViewGroup) v.findViewById(R.id.previewViewGroup);
        viewPager = (CustomizedViewPager) v.findViewById(R.id.viewPager);
        setModeButton = (Button) v.findViewById(R.id.SetModeButton);
        timeLimit = (SeekBar) v.findViewById(R.id.TimeLimit);
        timeLimitText = (TextView) v.findViewById(R.id.TimeLimitText);
        scoreLimit = (SeekBar) v.findViewById(R.id.ScoreLimit);
        scoreLimitText = (TextView) v.findViewById(R.id.ScoreLimitText);


        //Load image ID into imgIdArray
        imgIdArray.add(R.drawable.ball_game_preview);
        imgIdArray.add(R.drawable.jewel_game_preview);

        //Load ImageView into mImageViews
        mImageViews = new ImageView[imgIdArray.size()];
        for(int i=0; i< imgIdArray.size(); i++){
            ImageView imageView = new ImageView(addAlarmActivity);
            imageView.setImageResource(imgIdArray.get(i));
            //imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mImageViews[i] = imageView;
        }

        //Load page indicator into tips
        tips = new ImageView[imgIdArray.size()];
        for(int i=0; i< imgIdArray.size(); i++){
            ImageView imageView = new ImageView(addAlarmActivity);
            imageView.setLayoutParams(new LayoutParams(10,10));
            tips[i] = imageView;
            if(i == 0){
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new
                    ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.setMargins(5,0,5,0);
            viewGroup.addView(imageView, layoutParams);
        }


        viewPager.setAdapter(mPagerAdapter);
        pageChangeListener = new PageChangeListener();
        viewPager.addOnPageChangeListener(pageChangeListener);

        setModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarmActivity.setMode(mode);
                addAlarmActivity.setTIME_LIMIT(timeLimitProgress * 1000);
            }
        });

        // the Seek Bar of setting time threshold of all games
        timeLimitText.setText("Alarm Time Threshold:");
        timeLimit.setMax((TIME_LIMIT_MAX - TIME_LIMIT_MIN) / TIME_LIMIT_STEP);
        timeLimit.setEnabled(true);
        timeLimit.setProgress(0);
        timeLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeLimitProgress = TIME_LIMIT_MIN + (progress * TIME_LIMIT_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                timeLimitText.setText("Alarm Time Threshold(" +
                        String.valueOf(timeLimitProgress) + " Seconds):");
            }
        });

        return v;
    }

    //Get context from parent activity
    public void onAttach(Context context) {
        super.onAttach(context);
        addAlarmActivity = (AddAlarmActivity) context;
    }

    //Remove PageChangeListener when fragment being destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewPager.removeOnPageChangeListener(pageChangeListener);
    }

    //Define customized ViewPager.OnPageChangeListener
    class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }


        @Override
        public void onPageSelected(final int arg0) {
            //Indicate page changed
            for(int i=0;i<tips.length;i++){
                tips[arg0].setBackgroundResource(R.drawable.page_indicator_focused);
                if (arg0 != i) {
                    tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                }
            }

            if (arg0 == 1) {
                // the Seek Bar of setting score threshold of jewel game
                scoreLimitText.setVisibility(View.VISIBLE);
                scoreLimit.setVisibility(View.VISIBLE);
                scoreLimitText.setText("Jewel Game Score Threshold:");
                scoreLimit.setMax((SCORE_LIMIT_MAX - SCORE_LIMIT_MIN) / SCORE_LIMIT_STEP);
                scoreLimit.setEnabled(true);
                scoreLimit.setProgress(0);
                scoreLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        scoreLimitProgress = SCORE_LIMIT_MIN + (progress * SCORE_LIMIT_STEP);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        scoreLimitText.setText("Jewel Game Score Threshold(" +
                                String.valueOf(scoreLimitProgress) + " Points):");
                    }
                });
            } else {
                scoreLimitText.setVisibility(View.GONE);
                scoreLimit.setVisibility(View.GONE);
                scoreLimit.setProgress(0);
            }

            //If user click "Set Mode" button, send the Mode Value to parent activity
            setModeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAlarmActivity.setMode(mode + arg0);
                    addAlarmActivity.setTIME_LIMIT(timeLimitProgress * 1000);
                    if (arg0 == 1) {
                        addAlarmActivity.setSCORE_LIMIT(scoreLimitProgress);
                    }
                }
            });

        }

    }

    PagerAdapter mPagerAdapter = new PagerAdapter(){

        @Override
        //Get current pages amount
        public int getCount() {
            return imgIdArray.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }

        //Destroy the page when user sliding out
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(mImageViews[position]);
        }

        //Initialize the page when user sliding in
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(mImageViews[position]);
            return mImageViews[position];
        }

    };
}