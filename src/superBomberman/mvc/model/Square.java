package superBomberman.mvc.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Square object is used to create a grid of playable "spaces" on a GameBoard
 * each square object is created as a single "point" on an XY place,
 * but is drawn by the program as a larger object with height, width, center
 */
public class Square extends Sprite {

    // ===============================================
    // FIELDS
    // ===============================================

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

    private Bomb mBombInside;
    private List<Enemy> mExploredByEnemies;

    // ===============================================
    // CONSTRUCTOR
    // ===============================================

    public Square(int row, int column) {

        // assign row and column
        mRow = row;
        mColumn = column;

        mExploredByEnemies = new ArrayList<>();

        // create & assign center point for display
        // changed equation from TargetSpace
        mCenter = new Point(SQUARE_LENGTH * mColumn + SQUARE_LENGTH/2, SQUARE_LENGTH * mRow + SQUARE_LENGTH/2 );
        setCenter(mCenter);

    }

    // ===============================================
    // METHODS
    // ===============================================

    public Square getNextSquareUp() {
        return getOffsetSquare(-1, 0);
    }

    public Square getNextSquareDown() {
        return getOffsetSquare(1, 0);
    }

    public Square getNextSquareRight() {
        return getOffsetSquare(0, 1);
    }

    public Square getNextSquareLeft() {
        return getOffsetSquare(0, 1);
    }

    public Square getOffsetSquare(int rowOffset, int colOffset) {
        return CommandCenter.getInstance().getGameBoard().getSquare(mRow + rowOffset, mColumn + colOffset);
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

    public boolean isSolidWall() {
        if (isWall()) {
            Wall wall = (Wall) getInside();
            return !wall.isBreakable();
        }
        return false;
    }
    public void removeWall() {
        mIsWall = false;
    }

    public void addWall() {
        mIsWall = true;
    }

    public boolean containsBomb() {
        return mBombInside != null;
    }

    public Bomb getBombInside() {
        return mBombInside;
    }

    public void removeBomb() {
        mBombInside = null;
    }

    public void addBomb(Bomb bomb) {
        mBombInside = bomb;
    }

    // valid move if square is not a wall, block, or breakable
    public boolean isBlocked() {
        return isWall()|| containsBomb();
    }

    public boolean hasEnemy() {
        for (Movable movEnemy : CommandCenter.getInstance().getMovEnemies()) {
            if (movEnemy.getCurrentSquare().equals(this))
                return true;
        }
        return false;
    }

    public boolean hasBomberman() {
        for (Movable movFriend : CommandCenter.getInstance().getMovFriends()) {
            if (movFriend.getCurrentSquare().equals(this))
                return true;
        }
        return false;
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
