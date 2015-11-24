package nyu.tandon.cs9033.gameralarm.models;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by oily on 11/23/2015.
 */
public class JewelSprite implements IConstants {

	int mStyle;
	int mState;
	final Sprite mSprite;

	public JewelSprite(int row, int col, TextureRegion mJewelTextureRegion){
		this.mSprite = new JewelCell(row, col, mJewelTextureRegion);
		this.mSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mState = STATE_NORMAL;
	}


	public int getRow() {
		return (int)this.mSprite.getX()/CELL_WIDTH;
	}

	public int getCol() {
		return (int)this.mSprite.getY()/CELL_HEIGHT;
	}	

	public void setMapPosition(int row, int col){
		this.mSprite.setPosition(row * CELL_WIDTH, col * CELL_HEIGHT);
	}


	public void setStyle(final int style){
		this.mStyle = style;
	}
	
	public int getStyle(){
		return this.mStyle;
	}
	
	public Sprite getJewel(){
		return this.mSprite;
	}	
	
	public void setState(int state){
		this.mState = state;
	}
	
	public int getState(){
		return this.mState;
	}

	int step = 0;
	public void doScale(){
		if(this.mState == STATE_SCALEINT)
		{
			if(step < 5)
			{
				step++;
				this.mSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				this.mSprite.setColor(1, 1, 1);
				switch (step) {
				case 0:
					this.mSprite.setScale(0.7f);
					this.mSprite.setAlpha(0.5f);
					break;
				case 1:
					this.mSprite.setScale(0.7f);
					this.mSprite.setAlpha(0.4f);
					break;
				case 2:
					this.mSprite.setScale(0.7f);
					this.mSprite.setAlpha(0.3f);
					break;
				case 3:
					this.mSprite.setScale(0.7f);
					this.mSprite.setAlpha(0.2f);
					break;
				case 4:
					this.mSprite.setScale(0.7f);
					this.mSprite.setAlpha(0);
					break;
				default:
					break;
				}
			}
			else
			{
				step = 0;
				this.mState= STATE_DEAD;
			}
		}
	}
}
