package nyu.tandon.cs9033.gameralarm.views;
/**
 * Created by Byron on 10/30/15.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Random;

import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.controllers.BallGameActivity;

public class BallGameView extends SurfaceView implements Callback, Runnable, SensorEventListener {
    private BallGameActivity ballGameActivity = (BallGameActivity) getContext();
    /** Refresh screen every 50 frames **/
    public static final int TIME_IN_FRAME = 50;

    /**Game Painter **/
    Paint mPaint = null;
    Paint mTextPaint = null;
    SurfaceHolder mSurfaceHolder = null;

    /** Game looping flag **/
    public boolean mIsRunning = false;

    //Thread BGVThread = null;

    /** Game Canvas **/
    Canvas mCanvas = null;

    /** Define SensorManager **/
    private SensorManager mSensorMgr = null;
    Sensor mSensor = null;

    /** Screen Width and Height **/
    int mScreenWidth = 0;
    int mScreenHeight = 0;

    /** Ball moving area width and height **/
    private int mBallMovingAreaWidth = 0;
    private int mBallMovingAreaHeight = 0;

    /** Game Background Resource **/
    private Bitmap mBitmapBg;

    /** Ball Resource **/
    private Bitmap mBitmapBall;

    private Bitmap mBitmapHole;

    /** Ball moving coordinate **/
    private float mBallPosX = 10;
    private float mBallPosY = 200;
    private float mBallUpperPosX = 0;
    private float mBallUpperPosY = 0;

    /** Hole Position **/
    private float mHolePosX = 0;
    private float mHolePosY = 0;
    private float mHoleUpperPosX = 0;
    private float mHoleUpperPosY = 0;

    /** The values of X axis, Y axis and Z axis of accelerometer **/
    private float mGX = 0;
    private float mGY = 0;
    private float mGZ = 0;

    public BallGameView(Context paramContext) {
        super(paramContext);
        /** Set whether current view can receive the focus. **/
        this.setFocusable(true);
        /** Set whether current view can receive focus while in touch mode. **/
        //this.setFocusableInTouchMode(true);

        /** Access to the underlying surface **/
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        /** Init a new canvas **/
        mCanvas = new Canvas();
        /** Init a new painter **/
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        /** Load the ball resource **/
        mBitmapBall = BitmapFactory.decodeResource(this.getResources(), R.drawable.ballgame_ball);
        /** Load the game background **/
        mBitmapBg = BitmapFactory.decodeResource(this.getResources(), R.drawable.ballgame_background);
        mBitmapHole = BitmapFactory.decodeResource(this.getResources(), R.drawable.ballgame_hole);

        /** Get a SensorManager object and **/
        mSensorMgr = (SensorManager)paramContext.getSystemService(paramContext.SENSOR_SERVICE);
        mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        /** Accelerometer accuracy attributes
         SENSOR_DELAY_FASTEST Fastest speed mode
         SENSOR_DELAY_GAME    Developing mode
         SENSOR_DELAY_NORMAL  Normal speed mode
         SENSOR_DELAY_UI 	   Lowest speed mode **/
        mSensorMgr.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        mIsRunning = true;
    }


    private void Draw() {

        /** Draw the game background **/
        mCanvas.drawBitmap(mBitmapBg, 0, 0, mPaint);
        /** Draw the game ball **/
        mCanvas.drawBitmap(mBitmapHole, mHolePosX, mHolePosY, mPaint);
        mCanvas.drawBitmap(mBitmapBall, mBallPosX, mBallPosY, mPaint);
        /*mCanvas.drawText("X axis：" + mGX, 10, 20, mPaint);
        mCanvas.drawText("Y axis：" + mGY, 10, 40, mPaint);
        mCanvas.drawText("Z axis：" + mGZ, 10, 60, mPaint);*/
        
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(this).start();
        /** Get the current screen width and height **/
        mScreenWidth = this.getWidth();
        mScreenHeight = this.getHeight();

        mHolePosX = getRandom(Float.valueOf(mScreenWidth));
        mHolePosY = getRandom(Float.valueOf(mScreenHeight));

        mHoleUpperPosX = mHolePosX + Float.valueOf(mBitmapHole.getWidth());
        mHoleUpperPosY = mHolePosY + Float.valueOf(mBitmapHole.getHeight());

        mBallMovingAreaWidth = mScreenWidth - mBitmapBall.getWidth();
        mBallMovingAreaHeight = mScreenHeight - mBitmapBall.getHeight();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        mIsRunning = false;
    }

    @Override
    public void run() {
        while (mIsRunning) {

            long startTime = System.currentTimeMillis();

            synchronized (mSurfaceHolder) {
                mCanvas = mSurfaceHolder.lockCanvas();
                Draw();
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }

            long endTime = System.currentTimeMillis();

            int diffTime = (int) (endTime - startTime);

            while (diffTime <= TIME_IN_FRAME) {
                diffTime = (int) (System.currentTimeMillis() - startTime);
                Thread.yield();
            }

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mGX = event.values[0];
        mGY= event.values[1];
        mGZ = event.values[2];

        mBallPosX -= mGX * 2;
        mBallPosY += mGY * 2;

        mBallUpperPosX = mBallPosX + Float.valueOf(mBitmapBall.getWidth());
        mBallUpperPosY = mBallPosY + Float.valueOf(mBitmapBall.getHeight());

        if (mBallPosX < 0) {
            mBallPosX = 0;
        } else if (mBallPosX > mBallMovingAreaWidth) {
            mBallPosX = mBallMovingAreaWidth;
        }
        if (mBallPosY < 0) {
            mBallPosY = 0;
        } else if (mBallPosY > mBallMovingAreaHeight) {
            mBallPosY = mBallMovingAreaHeight;
        }

        if (mBallPosX >= mHolePosX && mBallPosY >= mHolePosY && mBallUpperPosX <= mHoleUpperPosX && mBallUpperPosY <= mHoleUpperPosY ) {
            Toast.makeText(BallGameView.this.getContext(), "You Win!", Toast.LENGTH_LONG).show();
            mSensorMgr.unregisterListener(BallGameView.this);
            surfaceDestroyed(mSurfaceHolder);
            ballGameActivity.gameFinish();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float getRandom(float val){
        Random r = new Random();
        return val/3 + r.nextFloat()*(val/3);
    }
}
