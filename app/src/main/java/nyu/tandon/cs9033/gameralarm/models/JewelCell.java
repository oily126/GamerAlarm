package nyu.tandon.cs9033.gameralarm.models;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

/**
 * Created by oily on 11/23/2015.
 */
public class JewelCell extends Sprite implements IConstants {
    public JewelCell(final int pCellX, final int pCellY, final TextureRegion pTextureRegion) {
        super(pCellX * CELL_WIDTH, pCellY * CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT, pTextureRegion);
    }
}