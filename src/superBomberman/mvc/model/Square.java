package superBomberman.mvc.model;

import java.awt.Point;

/**
 * Created by RAM0N on 11/27/15.
 */
public class Square extends Sprite {

    // game board consists of 143 playable squares (13 wide x 11 tall) and an exterior wall (0 & 14 for x values, 0 & 12 for y values)
    // each square object is created as a single "point" on an XY place, but is drawn by the program as a larger object with height, width, center, etc.

    // constants for height & width of each grid square
    public static final int SQUARE_LENGTH = 50;

    // private boolean members used for movement & collision
    private boolean mIsWall;
    private boolean mIsExit;

    // x y coordinates of square
    private int mRow;
    private int mColumn;

    // private members for center of square and object inside square
    private Point mCenter;
    private Sprite mInside;

    private boolean mContainsBomb;

    public Square(int row, int column) {

        // assign row and column
        mRow = row;
        mColumn = column;

        // create & assign center point for display
        // changed equation from TargetSpace
        mCenter = new Point(SQUARE_LENGTH * mColumn + SQUARE_LENGTH/2, SQUARE_LENGTH * mRow + SQUARE_LENGTH/2 );
        setCenter(mCenter);

    }

    public boolean isExit() {
        return mIsExit;
    }

    public void addExit() {
        mIsExit = true;
    }

    public boolean isWall() {
        return mIsWall;
    }

    public void removeWall() {
        mIsWall = false;
    }

    public void addWall() {
        mIsWall = true;
    }
    public boolean containsBomb() {
        return mContainsBomb;
    }

    public void removeBomb() {
        mContainsBomb = false;
    }

    public void addBomb() {
        mContainsBomb = true;
    }

    // valid move if square is not a wall, block, or breakable
    public boolean isBlocked() {
        return isWall()|| containsBomb();
    }

    @Override
    public Point getCenter() {
        return mCenter;
    }

    @Override
    public void setCenter(Point center) {
        mCenter = center;
    }

    public Sprite getInside() {
        return mInside;
    }

    public void setInside(Sprite inside) {
        mInside = inside;
    }

    public int getRow() {
        return mRow;
    }

    public int getColumn() {
        return mColumn;
    }

    public String toString() {
        return "row: " + getRow() + ", col: " + getColumn();
    }

}
