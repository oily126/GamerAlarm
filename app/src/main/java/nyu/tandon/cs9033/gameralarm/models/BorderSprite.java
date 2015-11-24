package nyu.tandon.cs9033.gameralarm.models;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

/**
 * Created by oily on 11/23/2015.
 */
public class BorderSprite implements IConstants {
    final Sprite mSprite;

    public BorderSprite(int row, int col, TextureRegion mTextureRegion ){
        this.mSprite = new JewelCell(row, col, mTextureRegion);
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

    public Sprite getSprite(){
        return this.mSprite;
    }
}
