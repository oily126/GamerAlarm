package nyu.tandon.cs9033.gameralarm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Byron on 12/4/15.
 */
public class QuizQuestions implements Parcelable {

    private int qid;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int answer;
    private int category;
    private String answerExplanation;


    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getAnswerExplanation() {
        return answerExplanation;
    }

    public void setAnswerExplanation(String answerExplanation) {
        this.answerExplanation = answerExplanation;
    }

    /**Do not modify this function.
     * Parcelable creator.
     */
    public static final Parcelable.Creator<QuizQuestions> CREATOR = new Parcelable.Creator<QuizQuestions>() {
        public QuizQuestions createFromParcel(Parcel p) {
            return new QuizQuestions(p);
        }

        public QuizQuestions[] newArray(int size) {
            return new QuizQuestions[size];
        }
    };

    /**
     * Create a QuizQuestions model object from a Parcel. This
     * function is called via the Parcelable creator.
     *
     * @param p The Parcel used to populate the
     * Model fields.
     */
    public QuizQuestions(Parcel p) {
        this.qid = p.readInt();
        this.question = p.readString();
        this.option1 = p.readString();
        this.option2 = p.readString();
        this.option3 = p.readString();
        this.option4 = p.readString();
        //this.answer = p.readInt();
        this.category = p.readInt();
        this.answerExplanation = p.readString();

    }

    /**
     * Create a QuizQuestions model object from arguments
     *
     */
    public QuizQuestions(String question, String option1, String option2, String option3,
                         String option4, Integer category, String answerExplanation) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        //this.answer = answer;
        this.category = category;
        this.answerExplanation = answerExplanation;
    }

    /**
     * Serialize QuizQuestions object by using writeToParcel.  
     * This function is automatically called by the
     * system when the object is serialized.
     *
     * @param dest Parcel object that gets written on 
     * serialization. Use functions to write out the
     * object stored via your member variables. 
     *
     * @param flags Additional flags about how the object 
     * should be written. May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
     * In our case, you should be just passing 0.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(qid);
        dest.writeString(question);
        dest.writeString(option1);
        dest.writeString(option2);
        dest.writeString(option3);
        dest.writeString(option4);
        //dest.writeInt(answer);
        dest.writeInt(category);
        dest.writeString(answerExplanation);
    }
    
    /**
     * Do not implement
     */
    @Override
    public int describeContents() {
        // Do not implement!
        return 0;
    }
}
