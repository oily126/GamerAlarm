package nyu.tandon.cs9033.gameralarm.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.modifier.AlphaModifier;
import org.anddev.andengine.entity.shape.modifier.LoopShapeModifier;
import org.anddev.andengine.entity.shape.modifier.RotationModifier;
import org.anddev.andengine.entity.shape.modifier.ScaleModifier;
import org.anddev.andengine.entity.shape.modifier.SequenceShapeModifier;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.MathUtils;
import org.anddev.andengine.engine.options.WakeLockOptions;

import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import nyu.tandon.cs9033.gameralarm.R;
import nyu.tandon.cs9033.gameralarm.models.BackgroundCell;
import nyu.tandon.cs9033.gameralarm.models.BorderSprite;
import nyu.tandon.cs9033.gameralarm.models.IConstants;
import nyu.tandon.cs9033.gameralarm.models.JewelSprite;

/**
 * Created by oily on 11/23/2015.
 */
public class JewelsActivity extends BaseGameActivity implements Scene.IOnSceneTouchListener, IConstants {
    /** size of the screen **/
    private static final int CAMERA_WIDTH = 320;
    private static final int CAMERA_HEIGHT = 526;

    private static final int LAYER_BACKGROUND = 0;
    private static final int LAYER_BG_CELL = LAYER_BACKGROUND + 1;
    private static final int LAYER_JEWELS = LAYER_BG_CELL + 1;
    private static final int LAYER_SCORE = LAYER_JEWELS + 1;
    private static int mv[][] = {{-1, 0},{0, -1}, {1, 0}, {0, 1}};

    private int mScoreLimit;
    private int mTimeLimit;

    private boolean mGameRunning;
    private boolean mIsSwaping;
    private final int MOVE_UP = 1;
    private final int MOVE_DOWN = 2;
    private final int MOVE_LEFT = 3;
    private final int MOVE_RIGHT = 4;
    private final int FALL = 5;
    private final int DEAD = 6;
    private final int CHECK = 0;
    private int STATE = CHECK;

    private final int SPEED = 4;
    private int moveValue = 0;

    private Camera mCamera;
    protected Scene mMainScene;

    /** background **/
    protected TextureRegion mBackgroundTextureRegion;
    private Texture mBackgroundTexture;

    /** jewel **/
    private HashMap<String, JewelSprite> mHashMap;
    private Texture[] mJewelTexture;
    protected TextureRegion[] mJewelTextureRegion;

    private BorderSprite mBorder;
    private Texture mBorderTexture;
    private TextureRegion mBorderTextureRegion;

    private Texture mBoardTexture;
    private TextureRegion mBoardTextureRegion;

    private Texture mBGCellTexture;
    private TextureRegion mBGCellTextureRegion;

    private int mScore = 0;
    private Texture mScoreFontTexture;
    private Font mScoreFont;
    private ChangeableText mScoreBGText,mScoreText;

    private int mCurRow,mCurCol;
    private int mLastRow,mLastCol;
    private ArrayList<String> mDeadArrList;
    private int mTime = 0;

    private Sprite mSpark,mSpark2;
    private Texture mSparkTexture,mSpark2Texture;
    private TextureRegion mSparkTextureRegion,mSpark2TextureRegion;

    private MediaPlayer player = null;
    private PowerManager.WakeLock mWakeLock = null;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    public Engine onLoadEngine() {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.ringtone1);
            player.setLooping(true);
        }
        player.start();

        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true, EngineOptions.ScreenOrientation.PORTRAIT,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
    }

    @Override
    public void onLoadResources() {
        TextureRegionFactory.setAssetBasePath("gfx/");

        String bgPath = "background.jpg";
        this.mBackgroundTexture = new Texture(512, 1024, TextureOptions.DEFAULT);
        this.mBackgroundTextureRegion = TextureRegionFactory.createFromAsset
                (this.mBackgroundTexture, this, bgPath, 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mBackgroundTexture);

        this.mJewelTexture = new Texture[7];
        this.mJewelTextureRegion = new TextureRegion[7];
        for(int i=0; i<this.mJewelTexture.length; i++){
            this.mJewelTexture[i] = new Texture(64, 64, TextureOptions.DEFAULT);
            this.mJewelTextureRegion[i] = TextureRegionFactory.createFromAsset
                    (this.mJewelTexture[i], this, "jewel"+ String.valueOf(i + 1) +".png", 0, 0);
            this.mEngine.getTextureManager().loadTexture(this.mJewelTexture[i]);
        }

        this.mBorderTexture = new Texture(64, 64, TextureOptions.DEFAULT);
        this.mBorderTextureRegion = TextureRegionFactory.createFromAsset
                (this.mBorderTexture, this, "selection.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mBorderTexture);

        this.mBoardTexture = new Texture(512, 512, TextureOptions.DEFAULT);
        this.mBoardTextureRegion = TextureRegionFactory.createFromAsset
                (this.mBoardTexture, this, "board.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mBoardTexture);

        this.mBGCellTexture = new Texture(128, 128, TextureOptions.DEFAULT);
        this.mBGCellTextureRegion = TextureRegionFactory.createFromAsset
                (this.mBGCellTexture, this, "bg_cell.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mBGCellTexture);

        this.mScoreFontTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mScoreFont = FontFactory.createFromAsset(this.mScoreFontTexture, this, "fonts/bluehigh.ttf", 44, true, Color.WHITE);
        this.mEngine.getTextureManager().loadTexture(this.mScoreFontTexture);
        this.mEngine.getFontManager().loadFont(this.mScoreFont);

        this.mSparkTexture = new Texture(64, 64, TextureOptions.DEFAULT);
        this.mSparkTextureRegion = TextureRegionFactory.createFromAsset
                (this.mSparkTexture, this, "spark1.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mSparkTexture);
        this.mSpark2Texture = new Texture(64, 64, TextureOptions.DEFAULT);
        this.mSpark2TextureRegion = TextureRegionFactory.createFromAsset
                (this.mSpark2Texture, this, "spark2.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mSpark2Texture);
    }

    @Override
    public Scene onLoadScene() {
        this.mMainScene = new Scene(4);
        this.mMainScene.setBackgroundEnabled(false);
        this.mMainScene.setOnSceneTouchListener(this);

        this.getLimitFromAlarm();
        this.init();
        this.prepareGame();
        this.prepareToBack();
        this.gameLoop();
        this.autoTips();
        return this.mMainScene;
    }

    @Override
    public void onLoadComplete() {}

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        if(STATE == MOVE_DOWN || STATE == MOVE_LEFT || STATE == MOVE_RIGHT || STATE == MOVE_UP || STATE == FALL){
            return false;
        }

        if(touchEvent.getX() > 0 && touchEvent.getX() < CAMERA_WIDTH
                && touchEvent.getY() > 0 && touchEvent.getY() < CAMERA_WIDTH){

            if(touchEvent.getAction() == MotionEvent.ACTION_DOWN){
                mDeadArrList.clear();
                this.mCurRow = (int)(touchEvent.getX()/CELL_WIDTH);
                this.mCurCol = (int)(touchEvent.getY()/CELL_HEIGHT);
                this.mBorder.setMapPosition(this.mCurRow, this.mCurCol);
                this.mBorder.getSprite().setVisible(true);
                if(this.isNext()){
                    this.mBorder.getSprite().setVisible(false);
                    this.setMoveDirection();
                }else if(this.mCurRow == this.mLastRow && this.mCurCol == this.mLastCol){
                    this.mLastRow = -2;
                    this.mLastCol = -2;
                    this.mBorder.getSprite().setVisible(false);
                }else {
                    this.mLastRow = this.mCurRow;
                    this.mLastCol = this.mCurCol;
                    this.mBorder.setMapPosition(this.mCurRow, this.mCurCol);
                    this.mBorder.getSprite().setVisible(true);
                }
            }
        }
        return false;
    }
    private void autoTips(){
        if (mSpark != null) {
            mSpark.setVisible(false);
            mSpark2.setVisible(false);
            this.mMainScene.getTopLayer().addEntity(mSpark);
            this.mMainScene.getTopLayer().addEntity(mSpark2);
        }
        this.mMainScene.registerUpdateHandler(new TimerHandler(0.5f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                 if (mGameRunning) {
                     if (STATE == CHECK) {
                         if (mTime == 0) {
                             checkMapDead();
                         }
                         mTime++;
                         if (mTime >= 10) {
                             doTips();
                             mTime = 0;
                         }
                     } else {
                         mTime = 0;
                     }
                }
            }
        }));
    }

    private void doTips(){
        if(mSpark == null){
            mSpark = new Sprite(0, 0, mSparkTextureRegion);
            mSpark2 = new Sprite(0, 0, mSpark2TextureRegion);
            mSpark.setVisible(false);
            mSpark2.setVisible(false);
            this.mMainScene.getTopLayer().addEntity(mSpark);
            this.mMainScene.getTopLayer().addEntity(mSpark2);
        }

        if(mDeadArrList.size() > 0) {
            String key = mDeadArrList.get(MathUtils.random(0, mDeadArrList.size() - 1));

            mSpark.setPosition(Integer.parseInt(key.substring(0, 1)) * CELL_WIDTH + 8,
                    Integer.parseInt(key.substring(1, 2)) * CELL_HEIGHT + 8);
            mSpark2.setPosition(Integer.parseInt(key.substring(0, 1)) * CELL_WIDTH + 4,
                    Integer.parseInt(key.substring(1, 2)) * CELL_HEIGHT + 4);
            mSpark.setVisible(true);
            mSpark2.setVisible(true);
            mSpark2.addShapeModifier(new RotationModifier(1.5f, 0, 90));
            mSpark.addShapeModifier(new SequenceShapeModifier(
                    new ScaleModifier(1.5f, 0.4f, 0.6f), new ScaleModifier(0.1f, 0.6f, 0f)));
            mSpark2.addShapeModifier(new SequenceShapeModifier(
                    new ScaleModifier(1.5f, 0.5f, 1.1f), new ScaleModifier(0.1f, 1.1f, 0f)));
        }

        if (mDeadArrList.isEmpty()) {
            Log.i(JewelsActivity.class.toString(), "DEAD for a time");
            int mScore = JewelsActivity.this.mScore;
            JewelsActivity.this.mEngine.setScene(JewelsActivity.this.onLoadScene());
            JewelsActivity.this.mScore = mScore;
            JewelsActivity.this.adjustScorePanel();
            JewelsActivity.this.mScoreText.setText(String.valueOf(mScore));
        }
    }

    private void setMoveDirection(){
        if(this.mLastRow == this.mCurRow && this.mLastCol > this.mCurCol){
            this.STATE = this.MOVE_UP;
        }
        if(this.mLastRow == this.mCurRow && this.mLastCol < this.mCurCol){
            this.STATE = this.MOVE_DOWN;
        }
        if(this.mLastRow > this.mCurRow && this.mLastCol == this.mCurCol){
            this.STATE = this.MOVE_LEFT;
        }
        if(this.mLastRow < this.mCurRow && this.mLastCol == this.mCurCol){
            this.STATE = this.MOVE_RIGHT;
        }
    }

    private boolean isNext(){
        if((Math.abs(this.mCurRow - this.mLastRow) == 1 && this.mCurCol == this.mLastCol)
                || (Math.abs(this.mCurCol - this.mLastCol) == 1 && this.mCurRow == this.mLastRow)){
            return true;
        }else {
            return false;
        }
    }

    private void getLimitFromAlarm() {
        mScoreLimit = getIntent().getIntExtra("score", 1000);
        mTimeLimit = getIntent().getIntExtra("time", 300);
    }

    private void prepareToBack(){
        this.mMainScene.registerUpdateHandler(new TimerHandler(mTimeLimit, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler bTimerHandler) {
                JewelsActivity.this.mGameRunning = false;
                player.stop();
                Intent intent = new Intent(JewelsActivity.this, NormalAlarmActivity.class);
                startActivity(intent);

                //JewelsActivity.this.mEngine.stop();
                //JewelsActivity.this.releaseWakeLock();
                JewelsActivity.this.finish();
            }
        }));
    }

    private void prepareGame(){
        this.mMainScene.registerUpdateHandler(new TimerHandler(1.00f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mMainScene.unregisterUpdateHandler(pTimerHandler);
                JewelsActivity.this.mGameRunning = true;
            }
        }));
    }

    private void init(){
        this.initFields();
        this.initBG();
        this.initCellBG();
        this.initJewels();
        this.initBorderSprite();
        this.initScore();
    }

    private boolean isSwapFall(){
        int count = 0;

        if(checkHorizontal(mHashMap.get(getKey(mCurRow, mCurCol))) >= 3){
            count += 1;
        }

        if(checkHorizontal(mHashMap.get(getKey(mLastRow, mLastCol))) >= 3){
            count += 1;
        }

        if(checkVertical(mHashMap.get(getKey(mCurRow, mCurCol))) >= 3){
            count += 1;
        }

        if(checkVertical(mHashMap.get(getKey(mLastRow, mLastCol))) >= 3){
            count += 1;
        }

        if(count == 0){
            return false;
        } else {
            return true;
        }
    }

    private void swapInHashMap(){
        JewelSprite temp = mHashMap.get(getKey(mLastRow, mLastCol));
        mHashMap.remove(getKey(mLastRow, mLastCol));
        mHashMap.put(getKey(mLastRow, mLastCol),
                mHashMap.get(getKey(mCurRow, mCurCol)));
        mHashMap.remove(getKey(mCurRow, mCurCol));
        mHashMap.put(getKey(mCurRow, mCurCol), temp);
    }

    private void moveUp(){
        if(mIsSwaping)
        {
            if(moveValue < CELL_HEIGHT)
            {
                moveValue += SPEED;
                final float x = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getX();
                final float curY = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getY();
                final float lastY = this.mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().getY();
                mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().setPosition(x, curY + SPEED);
                mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().setPosition(x, lastY - SPEED);
            }
            else
            {
                swapInHashMap();
                STATE = CHECK;
                moveValue = 0;
                mIsSwaping = false;
                this.mLastRow = -2;
                this.mLastCol = -2;
            }
        }
        else
        {
            if(moveValue < CELL_HEIGHT)
            {
                moveValue += SPEED;
                final float x = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getX();
                final float curY = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getY();
                final float lastY = this.mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().getY();
                mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().setPosition(x, curY + SPEED);
                mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().setPosition(x, lastY - SPEED);
            }
            else
            {
                swapInHashMap();
                if(isSwapFall())
                {
                    STATE = CHECK;
                    this.mLastRow = -2;
                    this.mLastCol = -2;
                }
                else
                {
                    mIsSwaping = true;
                }
                moveValue = 0;
            }
        }
    }

    private void moveDown(){
        if(mIsSwaping)
        {
            if(moveValue < CELL_HEIGHT)
            {
                moveValue += SPEED;
                final float x = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getX();
                final float curY = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getY();
                final float lastY = this.mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().getY();
                mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().setPosition(x, curY - SPEED);
                mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().setPosition(x, lastY + SPEED);
            }
            else
            {
                swapInHashMap();
                STATE = CHECK;
                moveValue = 0;
                mIsSwaping = false;
                this.mLastRow = -2;
                this.mLastCol = -2;
            }
        }
        else
        {
            if(moveValue < CELL_HEIGHT)
            {
                moveValue += SPEED;
                final float x = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getX();
                final float curY = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getY();
                final float lastY = this.mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().getY();
                mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().setPosition(x, curY - SPEED);
                mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().setPosition(x, lastY + SPEED);
            }
            else
            {
                swapInHashMap();
                if(isSwapFall())
                {
                    STATE = CHECK;
                    this.mLastRow = -2;
                    this.mLastCol = -2;
                }
                else
                {
                    mIsSwaping = true;
                }
                moveValue = 0;
            }
        }
    }

    private void moveLeft(){
        if(mIsSwaping)
        {
            if(moveValue < CELL_HEIGHT)
            {
                moveValue += SPEED;
                final float curX = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getX();
                final float lastX = this.mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().getX();
                final float y = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getY();
                mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().setPosition(curX + SPEED, y);
                mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().setPosition(lastX - SPEED, y);
            }
            else
            {
                swapInHashMap();
                STATE = CHECK;
                moveValue = 0;
                mIsSwaping = false;
                this.mLastRow = -2;
                this.mLastCol = -2;
            }
        }
        else
        {
            if(moveValue < CELL_HEIGHT)
            {
                moveValue += SPEED;
                final float curX = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getX();
                final float lastX = this.mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().getX();
                final float y = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getY();
                mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().setPosition(curX + SPEED, y);
                mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().setPosition(lastX - SPEED, y);
            }
            else
            {
                swapInHashMap();
                if(isSwapFall())
                {
                    STATE = CHECK;
                    this.mLastRow = -2;
                    this.mLastCol = -2;
                }
                else
                {
                    mIsSwaping = true;
                }
                moveValue = 0;
            }
        }
    }

    private void moveRight(){
        if(mIsSwaping)
        {
            if(moveValue < CELL_HEIGHT)
            {
                moveValue += SPEED;
                final float curX = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getX();
                final float lastX = this.mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().getX();
                final float y = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getY();
                mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().setPosition(curX - SPEED, y);
                mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().setPosition(lastX + SPEED, y);
            }
            else
            {
                swapInHashMap();
                STATE = CHECK;
                moveValue = 0;
                mIsSwaping = false;
                this.mLastRow = -2;
                this.mLastCol = -2;
            }
        }
        else
        {
            if(moveValue < CELL_HEIGHT)
            {
                moveValue += SPEED;
                final float curX = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getX();
                final float lastX = this.mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().getX();
                final float y = this.mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().getY();
                mHashMap.get(getKey(mCurRow, mCurCol)).getJewel().setPosition(curX - SPEED, y);
                mHashMap.get(getKey(mLastRow, mLastCol)).getJewel().setPosition(lastX + SPEED, y);
            }
            else
            {
                swapInHashMap();
                if(isSwapFall())
                {
                    STATE = CHECK;
                    this.mLastRow = -2;
                    this.mLastCol = -2;
                }
                else
                {
                    mIsSwaping = true;
                }
                moveValue = 0;
            }
        }
    }

    private void checkMapDead(){
        int count = 0;
        for(int i = 0; i < CELLS_HORIZONTAL; i++)
        {
            for(int j = 0; j < CELLS_VERTICAL; j++)
            {
                if(mHashMap.get(getKey(j, i)).getState() == STATE_NORMAL){
                    count ++;
                }
            }
        }
        if(count == 64){
            int i = 0;
            mDeadArrList.clear();
            while (i < CELLS_HORIZONTAL) {
                int j = 0;
                while (j < CELLS_HORIZONTAL) {
                    if (checkDead(mHashMap.get(getKey(j, i))) == true) {
                        this.mDeadArrList.add(getKey(j, i));
                    }
                    j += 1;
                }
                i += 1;
            }
        }
    }

    private boolean checkDead(final JewelSprite sprite){
        int row = sprite.getRow(), nrow, row_num = 1;
        int col = sprite.getCol(), ncol, col_num = 1;
        JewelSprite temp;
        int t;

        for (int i = 0; i < 4; i++) {
            nrow = mv[i][0] + row;
            ncol = mv[i][1] + col;
            if (nrow >= 0 && ncol >= 0 && nrow < CELLS_VERTICAL && ncol < CELLS_HORIZONTAL) {
                row_num = 1;
                col_num = 1;
                t = nrow - 1;
                while (t >= 0) {
                    if (t == row && ncol == col) {
                        if (mHashMap.get(getKey(nrow, ncol)).getStyle() == sprite.getStyle()) row_num++;
                        else break;
                    } else {
                        if (mHashMap.get(getKey(t, ncol)).getStyle() == sprite.getStyle()) row_num++;
                        else break;
                    }
                    t--;
                }

                t = nrow + 1;
                while (t < CELLS_VERTICAL) {
                    if (t == row && ncol == col) {
                        if (mHashMap.get(getKey(nrow, ncol)).getStyle() == sprite.getStyle()) row_num++;
                        else break;
                    } else {
                        if (mHashMap.get(getKey(t, ncol)).getStyle() == sprite.getStyle()) row_num++;
                        else break;
                    }
                    t++;
                }

                if (row_num >= 3) return true;

                t = ncol - 1;
                while (t >= 0) {
                    if (nrow == row && t == col) {
                        if (mHashMap.get(getKey(nrow, ncol)).getStyle() == sprite.getStyle()) col_num++;
                        else break;
                    } else {
                        if (mHashMap.get(getKey(nrow, t)).getStyle() == sprite.getStyle()) col_num++;
                        else break;
                    }
                    t--;
                }

                t = ncol + 1;
                while (t < CELLS_HORIZONTAL) {
                    if (nrow == row && t == col) {
                        if (mHashMap.get(getKey(nrow, ncol)).getStyle() == sprite.getStyle()) col_num++;
                        else break;
                    } else {
                        if (mHashMap.get(getKey(nrow, t)).getStyle() == sprite.getStyle()) col_num++;
                        else break;
                    }
                    t++;
                }

                if (col_num >= 3) return true;
            }
        }
        return false;
    }

    private void removeHorizontal(){
        int k = 0;
        for(int i = 0; i < CELLS_VERTICAL; i++)
        {
            for(int j = 0; j < CELLS_HORIZONTAL - 2; j++)
            {
                if(mHashMap.get(getKey(j, i)).getState() == STATE_NORMAL){
                    for(k = 1; j+k < CELLS_HORIZONTAL
                            &&  mHashMap.get(getKey(j, i)).getStyle() == mHashMap.get(getKey(j+k, i)).getStyle()
                            &&  mHashMap.get(getKey(j, i)).getState() == mHashMap.get(getKey(j+k, i)).getState();
                        k++);
                    if(k >= 3)
                    {
                        this.addScore(k);
                        removeVrtical();
                        for(int n = 0; n < k; n++)
                        {
                            mHashMap.get(getKey(j++, i)).setState(STATE_SCALEINT);
                        }
                    }
                }
            }
        }
    }

    private void removeVrtical(){
        int k = 0;
        for(int i = 0; i < CELLS_HORIZONTAL; i++)
        {
            for(int j = 0; j < CELLS_VERTICAL - 2; j++)
            {
                if(mHashMap.get(getKey(i, j)).getState() == STATE_NORMAL){
                    for(k = 1; j+k < CELLS_VERTICAL
                            &&  mHashMap.get(getKey(i, j)).getStyle() == mHashMap.get(getKey(i, j+k)).getStyle()
                            &&  mHashMap.get(getKey(i, j)).getState() == mHashMap.get(getKey(i, j+k)).getState();
                        k++);
                    if(k >= 3) {
                        this.addScore(k);
                        for(int n = 0; n < k; n++)
                        {
                            mHashMap.get(getKey(i, j++)).setState(STATE_SCALEINT);
                        }
                    }
                }
            }
        }
    }

    private void changeState(){
        int fallCount = 0;
        for(int i = 0; i < CELLS_HORIZONTAL; i++)
        {
            for(int j = 0; j < CELLS_VERTICAL; j++)
            {
                if(mHashMap.get(getKey(j, i)).getState() == STATE_SCALEINT){
                    fallCount ++;
                }
            }
        }
        if(fallCount > 0){
            STATE = FALL;
        }
    }

    private void refreshScale(){
        for(int i = 0; i < CELLS_HORIZONTAL; i++)
        {
            for(int j = 0; j < CELLS_VERTICAL; j++)
            {
                if(mHashMap.get(getKey(j, i)) != null && mHashMap.get(getKey(j, i)).getState() == STATE_SCALEINT)
                {
                    mHashMap.get(getKey(j, i)).doScale();
                }
            }
        }
    }

    private void fillEmpty(){
        for(int i = 0; i < CELLS_HORIZONTAL; i++)
        {
            for(int j = 0; j < CELLS_VERTICAL; j++)
            {
                if(mHashMap.get(getKey(j, i)).getState() == STATE_DEAD)
                {
                    int p = i;
                    while((p-1) >= 0  &&
                            mHashMap.get(getKey(j, p-1)).getState() != STATE_DEAD){
                        JewelSprite temp = mHashMap.get(getKey(j, p-1));
                        mHashMap.put(getKey(j, p-1),mHashMap.get(getKey(j, p)));
                        mHashMap.put(getKey(j, p),temp);
                        p--;
                    }
                }
            }
        }

        for(int i = 0; i < CELLS_HORIZONTAL; i++)
        {
            for(int j = 0; j < CELLS_VERTICAL; j++)
            {
                if(mHashMap.get(getKey(j, i)).getState() == STATE_DEAD)
                {
                    int v = 1;
                    for(v = 1; j+v < CELLS_VERTICAL
                            &&  mHashMap.get(getKey(j, i)).getStyle() == mHashMap.get(getKey(j, i+v)).getStyle()
                            &&  mHashMap.get(getKey(j, i)).getState() == mHashMap.get(getKey(j, i+v)).getState();
                        v++);
                    for(int z = v; z > 0; z--){
                        JewelSprite newJewel = getRandomJewel(j, -z);
                        mMainScene.getLayer(LAYER_JEWELS).addEntity(newJewel.getJewel());
                        mHashMap.put(getKey(j, v-z), newJewel);
                    }
                }
            }
        }

        int count = 0;
        for(int i = 0; i < CELLS_HORIZONTAL; i++)
        {
            for(int j = CELLS_VERTICAL - 1; j >= 0; j--)
            {
                if(mHashMap.get(getKey(i, j)).getJewel().getY() < j*CELL_HEIGHT)
                {
                    mHashMap.get(getKey(i, j)).getJewel()
                            .setPosition(i*CELL_WIDTH, mHashMap.get(getKey(i, j)).getJewel().getY()+ CELL_HEIGHT/2);
                    count++;
                }
            }
        }
        if(count == 0){
            STATE = CHECK;
        }
    }

    private void gameLoop(){
        this.mMainScene.registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void reset() {
            }

            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (JewelsActivity.this.mGameRunning) {
                    switch (STATE) {
                        case MOVE_UP:
                            moveUp();
                            break;
                        case MOVE_DOWN:
                            moveDown();
                            break;
                        case MOVE_LEFT:
                            moveLeft();
                            break;
                        case MOVE_RIGHT:
                            moveRight();
                            break;
                        case CHECK:
                            checkMapDead();
                            removeHorizontal();
                            removeVrtical();
                            changeState();
                            break;
                        case FALL:
                            refreshScale();
                            fillEmpty();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    private void addScore(int fallCount){
        switch (fallCount) {
            case 3:
                this.mScore += 20;
                break;
            case 4:
                this.mScore += 40;
                break;
            case 5:
                this.mScore += 60;
                break;
            case 6:
                this.mScore += 80;
                break;
            case 7:
                this.mScore += 100;
                break;
            case 8:
                this.mScore += 120;
                break;
            default:
                break;
        }
        this.adjustScorePanel();
        this.mScoreText.setText(String.valueOf(this.mScore));
        if (this.mScore >= this.mScoreLimit) {
            this.mGameRunning = false;
            player.stop();
            //this.mEngine.stop();
            //this.releaseWakeLock();
            finish();
        }
    }

    private void adjustScorePanel(){
        if(this.mScore > 9 && (int)this.mScoreText.getX() == 295){
            this.mScoreBGText.setText("00000");
            this.mScoreText.setPosition(274, 336);
        }else if(this.mScore > 99 && this.mScoreText.getX() == 274){
            this.mScoreBGText.setText("0000");
            this.mScoreText.setPosition(253, 336);
        }else if(this.mScore > 999 && this.mScoreText.getX() == 253){
            this.mScoreBGText.setText("000");
            this.mScoreText.setPosition(232, 336);
        }else if(this.mScore > 9999 && this.mScoreText.getX() == 232){
            this.mScoreBGText.setText("00");
            this.mScoreText.setPosition(211, 336);
        }else if(this.mScore > 99999 && this.mScoreText.getX() == 211){
            this.mScoreBGText.setText("0");
            this.mScoreText.setPosition(190, 336);
        }else if(this.mScore > 999999 && this.mScoreText.getX() == 190){
            this.mScoreBGText.setText("");
            this.mScoreText.setPosition(169, 336);
        }
    }

    private void initFields(){
        this.mLastRow = -2;
        this.mLastCol = -2;
        this.mIsSwaping = false;
        mDeadArrList = new ArrayList<String>();
    }

    private void initBG(){
        final Sprite background = new Sprite(0, 0, this.mBackgroundTextureRegion);
        this.mMainScene.getLayer(LAYER_BACKGROUND).addEntity(background);
    }

    private void initCellBG(){
        final Sprite board = new Sprite(0, 0, mBoardTextureRegion);
        board.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        board.setAlpha(0.2f);
        this.mMainScene.getLayer(LAYER_BG_CELL).addEntity(board);

        Sprite cellBG[][] = new Sprite[CELLBG_HORIZONTAL][CELLBG_VERTICAL];
        for(int i=0; i<CELLBG_HORIZONTAL; i++){
            for(int j=0; j<CELLBG_VERTICAL; j++){
                cellBG[i][j] = new BackgroundCell(i, j, this.mBGCellTextureRegion);
                cellBG[i][j].setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                cellBG[i][j].setAlpha(0.2f);
                this.mMainScene.getLayer(LAYER_BG_CELL).addEntity(cellBG[i][j]);
            }
        }
    }

    private String getKey(final int row, final int col){
        return String.valueOf(row) + String.valueOf(col);
    }

    public JewelSprite getRandomJewel(final int row,final int col){
        int style = MathUtils.random(0, 6);
        JewelSprite jewelSprite = new JewelSprite(row, col, mJewelTextureRegion[style]);
        jewelSprite.setStyle(style);
        return jewelSprite;
    }

    private int checkHorizontal(final JewelSprite jewel){
        int list_length = 0;
        if(jewel != null){
            int curRow = jewel.getRow();
            final int curCol =jewel.getCol();
            final int curStyle = jewel.getStyle();

            while((curRow-1) >= 0){
                if(mHashMap.get(getKey(curRow-1, curCol)) != null){
                    if(curStyle == mHashMap.get(getKey(curRow-1, curCol)).getStyle()){
                        list_length++;
                    }else {
                        curRow = 0;
                    }
                }
                curRow -= 1;
            }
            curRow = jewel.getRow();
            list_length++;

            while((curRow+1) < CELLS_VERTICAL){
                if(mHashMap.get(getKey(curRow+1, curCol)) != null){
                    if(curStyle == mHashMap.get(getKey(curRow+1, curCol)).getStyle()){
                        list_length++;
                    }else {
                        curRow = CELLS_VERTICAL;
                    }
                }
                curRow += 1;
            }
        }
        return list_length;
    }

    private int checkVertical(final JewelSprite jewel){
        int list_length = 0;
        if(jewel != null){
            final int curRow = jewel.getRow();
            int curCol =jewel.getCol();
            final int curStyle = jewel.getStyle();

            while((curCol-1) >= 0){
                if(mHashMap.get(getKey(curRow, curCol-1)) != null){
                    if(curStyle == mHashMap.get(getKey(curRow, curCol-1)).getStyle()){
                        list_length++;
                    }else {
                        curCol = 0;
                    }
                }
                curCol -= 1;
            }

            curCol =jewel.getCol();
            list_length++;
            while((curCol+1) < CELLS_HORIZONTAL){
                if(mHashMap.get(getKey(curRow, curCol+1)) != null){
                    if(curStyle == mHashMap.get(getKey(curRow, curCol+1)).getStyle()){
                        list_length++;
                    }else {
                        curCol = CELLS_HORIZONTAL;
                    }
                }
                curCol += 1;
            }
        }
        return list_length;
    }

    private void initJewels(){
        this.mHashMap = new HashMap<String, JewelSprite>();
        for(int i = 0; i < CELLS_HORIZONTAL; i++){
            for(int j = 0; j < CELLS_VERTICAL; j++){
                String key = getKey(i, j);
                JewelSprite value = getRandomJewel(i, j);
                while(checkHorizontal(value) >= 3 || checkVertical(value) >= 3){
                    value = getRandomJewel(i, j);
                }
                mHashMap.put(key, value);
                this.mMainScene.getLayer(LAYER_JEWELS).addEntity(
                        this.mHashMap.get(key).getJewel());
            }
        }
    }

    private void initBorderSprite(){
        this.mBorder = new BorderSprite(-2, -2, mBorderTextureRegion);
        this.mBorder.getSprite().setVisible(false);
        this.mBorder.getSprite().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mBorder.getSprite().addShapeModifier(new LoopShapeModifier(new SequenceShapeModifier
                (new AlphaModifier(0.4f, 1, 0), new AlphaModifier(0.2f, 0, 1))));
        this.mMainScene.getLayer(LAYER_JEWELS).addEntity(this.mBorder.getSprite());
    }

    private void initScore(){
        this.mScoreBGText = new ChangeableText(170, 336, this.mScoreFont, "000000");
        this.mScoreBGText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mScoreBGText.setAlpha(0.4f);
        this.mScoreBGText.setScaleY(1.5f);
        this.mMainScene.getLayer(LAYER_SCORE).addEntity(this.mScoreBGText);

        this.mScoreText = new ChangeableText(295, 336, this.mScoreFont, "0", 7);
        this.mScoreText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mScoreText.setAlpha(1);
        this.mScoreText.setScaleY(1.5f);
        this.mMainScene.getLayer(LAYER_SCORE).addEntity(this.mScoreText);
    }

    // Disable Back and Menu buttons
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return false;
            case KeyEvent.KEYCODE_MENU:
                return false;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onPause() {
        this.mGameRunning = false;
        if (player != null) player.pause();
        /*player.stop();
        this.mGameRunning = false;
        //this.mEngine.stop();
        //this.releaseWakeLock();
        finish();*/
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mGameRunning = true;
        if (player == null) {
            player =  MediaPlayer.create(this, R.raw.ringtone1);
            player.setLooping(true);
        }
        player.start();
        /*this.acquireWakeLock(this.mEngine.getEngineOptions().getWakeLockOptions());
        if(this.mHasWindowFocused) {
            this.mRenderSurfaceView.onResume();
            this.mEngine.start();
        }*/
    }

    private void acquireWakeLock(WakeLockOptions pWakeLockOptions) {
        final PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(pWakeLockOptions.getFlag() | PowerManager.ON_AFTER_RELEASE, "AndEngine");
        try {
            this.mWakeLock.acquire();
        } catch (final SecurityException e) {
        }
    }

    private void releaseWakeLock() {
        if(this.mWakeLock != null && this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
    }
}
