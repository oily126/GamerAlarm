package nyu.tandon.cs9033.gameralarm.models;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

/**
 * Created by oily on 11/24/2015.
 */
public class BackgroundCell extends Sprite implements IConstants {

    // ===========================================================
    // Constructors
    // ===========================================================

    public BackgroundCell(final int pCellX, final int pCellY, final TextureRegion pTextureRegion) {
        super(pCellX * CELLBG_WIDTH, pCellY * CELLBG_HEIGHT, CELLBG_WIDTH, CELLBG_HEIGHT, pTextureRegion);
    }
}
