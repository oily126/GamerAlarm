package nyu.tandon.cs9033.gameralarm.views;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.preview_fragment, container, false);
        viewGroup = (ViewGroup) v.findViewById(R.id.previewViewGroup);
        viewPager = (CustomizedViewPager) v.findViewById(R.id.viewPager);
        setModeButton = (Button) v.findViewById(R.id.SetModeButton);


        //Load image ID into imgIdArray
        imgIdArray.add(R.drawable.ball_game_preview);
        imgIdArray.add(R.drawable.jewel_game_preview);

        //Load ImageView into mImageViews
        mImageViews = new ImageView[imgIdArray.size()];
        for(int i=0; i< imgIdArray.size(); i++){
            ImageView imageView = new ImageView(addAlarmActivity);
            imageView.setBackgroundResource(imgIdArray.get(i));
            imageView.setAdjustViewBounds(true);
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
                int mode = 10;
                addAlarmActivity.setMode(mode);
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

            //If user click "Set Mode" button, send the Mode Value to parent activity
            setModeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mode = 10;
                    addAlarmActivity.setMode(mode + arg0);
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