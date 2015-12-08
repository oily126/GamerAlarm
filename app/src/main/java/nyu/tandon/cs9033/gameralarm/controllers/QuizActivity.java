package nyu.tandon.cs9033.gameralarm.controllers;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import android.os.Handler;

import nyu.tandon.cs9033.gameralarm.AlarmDatabaseHelper;
import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.models.QuizQuestions;

/**
 * Created by Byron on 12/4/15.
 */
public class QuizActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView question;
    private TextView option1;
    private TextView option2;
    private TextView option3;
    private TextView option4;
    private static TextView statistics;
    private TextView answerExplanation;
    private TextView questionResult;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private RelativeLayout R1;
    private RelativeLayout R2;
    private RelativeLayout R3;
    private RelativeLayout R4;
    private Button submit;
    private static int TOTAL_NUMBER = 8;
    private static int RIGHT_NUMBER = 4;
    private static int questionNo = 0;
    private static int rightNo = 0;
    private static int answerIndex;
    private static int selectedIndex;
    private MediaPlayer player;
    //private ViewPager viewPager;
    private String querySql;
    private String[] queryArgs;
    private List<QuizQuestions> qList = new ArrayList<QuizQuestions>();
    private TextView[] optionName;
    private ImageView[] imageViews;
    private RelativeLayout[] relativeLayouts;
    private Set<Integer> set = new HashSet<Integer>();
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        TOTAL_NUMBER = intent.getParcelableExtra("TotalNumber");
        RIGHT_NUMBER = intent.getParcelableExtra("RightNumber");

        question = (TextView) findViewById(R.id.question);
        option1 = (TextView) findViewById(R.id.selectOne);
        option2 = (TextView) findViewById(R.id.selectTwo);
        option3 = (TextView) findViewById(R.id.selectThree);
        option4 = (TextView) findViewById(R.id.selectFour);
        statistics = (TextView) findViewById(R.id.statistics);
        answerExplanation = (TextView) findViewById(R.id.answerExplanation);
        submit = (Button) findViewById(R.id.question_submit);
        questionResult = (TextView) findViewById(R.id.question_result);
        R1 = (RelativeLayout) findViewById(R.id.OptionOne);
        R2 = (RelativeLayout) findViewById(R.id.OptionTwo);
        R3 = (RelativeLayout) findViewById(R.id.OptionThree);
        R4 = (RelativeLayout) findViewById(R.id.OptionFour);
        image1 = (ImageView) findViewById(R.id.imageOne);
        image2 = (ImageView) findViewById(R.id.imageTwo);
        image3 = (ImageView) findViewById(R.id.imageThree);
        image4= (ImageView) findViewById(R.id.imageFour);

        optionName = new TextView[]{option1, option2, option3, option4};
        imageViews = new ImageView[]{image1, image2, image3, image4};
        relativeLayouts = new RelativeLayout[]{R1, R2, R3, R4};

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);
        R1.setOnClickListener(this);
        R2.setOnClickListener(this);
        R3.setOnClickListener(this);
        R4.setOnClickListener(this);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
        submit.setOnClickListener(this);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //player =  MediaPlayer.create(this, R.raw.ringtone1);
        //player.start();

        //Get all questions from database
        Cursor cursor = null;
        AlarmDatabaseHelper adh = new AlarmDatabaseHelper(this);
        SQLiteDatabase db = adh.getReadableDatabase();
        querySql = "select * from quiz order by RANDOM() LIMIT ?";
        queryArgs = new String[]{String.valueOf(TOTAL_NUMBER)};
        try {
            cursor = db.rawQuery(querySql,
                    queryArgs);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                QuizQuestions q = new QuizQuestions(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7));
                q.setQid(cursor.getInt(0));
                qList.add(q);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        questionNo = 0;
        setQuestionView(qList.get(questionNo));

    }

    /*Listen all click events in view*/
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.OptionOne || v.getId() == R.id.imageOne || v.getId() == R.id.selectOne){
            selectedIndex = 0;
            optionSetDefault(0);
            image1.setImageResource(R.drawable.question_selected);
            R1.setBackgroundResource(R.color.question_selected);
            option1.setBackgroundResource(R.color.question_selected);
            submit.setClickable(true);
        }
        if (v.getId() == R.id.OptionTwo || v.getId() == R.id.imageTwo || v.getId() == R.id.selectTwo) {
            selectedIndex = 1;
            optionSetDefault(1);
            image2.setImageResource(R.drawable.question_selected);
            R2.setBackgroundResource(R.color.question_selected);
            option2.setBackgroundResource(R.color.question_selected);
            submit.setClickable(true);
        }
        if (v.getId() == R.id.OptionThree || v.getId() == R.id.imageThree || v.getId() == R.id.selectThree){
            selectedIndex = 2;
            optionSetDefault(2);
            image3.setImageResource(R.drawable.question_selected);
            R3.setBackgroundResource(R.color.question_selected);
            option3.setBackgroundResource(R.color.question_selected);
            submit.setClickable(true);
        }
        if (v.getId() == R.id.OptionFour || v.getId() == R.id.imageFour || v.getId() == R.id.selectFour) {
            selectedIndex = 3;
            optionSetDefault(3);
            image4.setImageResource(R.drawable.question_selected);
            R4.setBackgroundResource(R.color.question_selected);
            option4.setBackgroundResource(R.color.question_selected);
            submit.setClickable(true);
        }
        if  (v.getId() == R.id.question_submit) {
            showAnswer();
            questionNo++;
            if (questionNo < TOTAL_NUMBER && rightNo < RIGHT_NUMBER) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        setQuestionView(qList.get(questionNo));
                    }
                }, 1500);


            } else {
                //player.stop();
                Intent startNormal = new Intent(QuizActivity.this, NormalAlarmActivity.class);
                startActivity(startNormal);
                finish();
            }
        }
    }

    /*Set new question*/
    private void setQuestionView(QuizQuestions q){
        int qIndex;
        question.setText(q.getQuestion());
        submit.setClickable(false);
        for (int i=0; i < 4; i++) {
            optionName[i].setClickable(true);
            relativeLayouts[i].setClickable(true);
            imageViews[i].setClickable(true);
            optionName[i].setBackgroundResource(R.color.question_default);
            imageViews[i].setImageResource(R.drawable.question_defaults);
            relativeLayouts[i].setBackgroundResource(R.color.question_default);
        }
        answerExplanation.setText("");
        statistics.setText("");
        questionResult.setText("");
        set.clear();
        // Show the question options randomly
        while (set.size() < 4) {
            qIndex = random.nextInt(4);
            if (set.add(qIndex) == true) {
                switch (set.size()) {
                    case 1:
                        optionName[qIndex].setText(q.getOption1());
                        answerIndex = qIndex;
                        break;
                    case 2:
                        optionName[qIndex].setText(q.getOption2());
                        break;
                    case 3:
                        optionName[qIndex].setText(q.getOption3());
                        break;
                    case 4:
                        optionName[qIndex].setText(q.getOption4());
                        break;
                    default:
                        break;
                }

            }
        }
    }

    /*Set attributes of all options except the selected one to default*/
    public void optionSetDefault(int index) {
        for (int i=0; i < 4; i++) {
            if (i != index) {
                optionName[i].setBackgroundResource(R.color.question_default);
                imageViews[i].setImageResource(R.drawable.question_defaults);
                relativeLayouts[i].setBackgroundResource(R.color.question_default);
            }
        }
    }

    /*Change attributes of all options after clicking the submit button*/
    public void showAnswer() {
        String str;
        for (int i=0; i < 4; i++) {
            optionName[i].setClickable(false);
            relativeLayouts[i].setClickable(false);
            imageViews[i].setClickable(false);
        }
        optionName[answerIndex].setBackgroundResource(R.color.question_right);
        imageViews[answerIndex].setImageResource(R.drawable.question_right);
        relativeLayouts[answerIndex].setBackgroundResource(R.color.question_right);
        if (answerIndex == selectedIndex) {
            rightNo++;
            questionResult.setText("Right Answer!");
        } else {
            optionName[selectedIndex].setBackgroundResource(R.color.question_error);
            imageViews[selectedIndex].setImageResource(R.drawable.question_wrong);
            relativeLayouts[selectedIndex].setBackgroundResource(R.color.question_error);
            questionResult.setText("Oops! Wrong Answer!");
        }
        str = "Total: " + String.valueOf(questionNo + 1) + " questions.\nRight: " + String.valueOf(rightNo)
                + " questions.";
        statistics.setText(str);
    }

}
