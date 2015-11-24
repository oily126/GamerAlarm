package nyu.tandon.cs9033.gameralarm.models;

/**
 * Created by oily on 11/23/2015.
 */
public interface IConstants {
    /** constants **/
    /** number of cells **/
    public static final int CELLS_HORIZONTAL = 8;
    public static final int CELLS_VERTICAL = CELLS_HORIZONTAL;

    /** size of cells **/
    public static final int CELL_WIDTH = 40;
    public static final int CELL_HEIGHT = 40;

    /** cell number in bg **/
    public static final int CELLBG_HORIZONTAL = 4;
    public static final int CELLBG_VERTICAL = CELLBG_HORIZONTAL;

    /** cell size in bg **/
    public static final int CELLBG_WIDTH = 80;
    public static final int CELLBG_HEIGHT = CELLBG_WIDTH;

    /** status of jewel **/
    final int STATE_NORMAL = 0;  //normal
    final int STATE_SCALEINT = STATE_NORMAL + 1; //zoning
    final int STATE_FALL = STATE_SCALEINT + 1;   //falling
    final int STATE_DEAD = STATE_FALL + 1;//dead
}
