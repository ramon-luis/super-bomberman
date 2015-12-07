package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Wall object covers single square on gameboard
 * Wall can be solid or breakable -> blasts can destroy breakable walls
 * Wall blocks movement of Bomberman, Monster, and blast
 * Wall can contain a PowerUp if breakable -> spawns PowerUp when destroyed
 */

public class Wall extends Sprite {

    // ===============================================
    // FIELDS
    // ===============================================
    // enum for type of wall
    public enum WallType {SOLID, BREAKABLE}

    // constant for size
    public static final int SIZE = Square.SQUARE_LENGTH / 2 + 5;

    // private instance members
    private WallType mWallType;
    private PowerUp mPowerUpInside;
    private boolean mColorDown;  // used for inside of breakable walls
    private int mRed;  // used for inside of breakable walls
    private int mGreen;  // used for inside of breakable walls
    private int mBlue;  // used for inside of breakable walls

    // ===============================================
    // CONSTRUCTOR
    // ===============================================
    public Wall (Square square, WallType wallType) {

        // call super constructor & set team
        super();
        setTeam(Team.WALL);

        // assign wall type
        mWallType = wallType;

        // create list points for shape
        ArrayList<Point> pntCs = new ArrayList<>();
        pntCs.add(new Point(1,1));
        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(-1,-1));
        pntCs.add(new Point(-1,1));

        // assign Polar Points
        assignPolarPoints(pntCs);

        // assign initial inner color RGB -> overridden later
        mRed = 0;
        mGreen = getRandomColorIndex(0,255);
        mBlue = mGreen;

        setColor((mWallType == WallType.BREAKABLE) ? new Color(0, 125, 255) : new Color(55, 55, 55));
        setCenter(square.getCenter());
        setSize(SIZE);
    }

    // ===============================================
    // METHODS
    // ===============================================
    @Override
    public void draw(Graphics g) {
        // draw Wall shape
        super.draw(g);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);

        // set color and draw inner square
        g.setColor((mWallType == WallType.BREAKABLE) ? getShiftedColor() : new Color(105, 105, 105));
        int iDrawX = (int) getCenter().getX() - SIZE / 2;
        int iDrawY = (int) getCenter().getY() - SIZE / 2;
        g.fillRect(iDrawX, iDrawY, SIZE, SIZE);
    }

    // get power up inside the object
    public PowerUp getPowerUpInside() {
        return mPowerUpInside;
    }

    // add a power up to the object
    public void setPowerUpInside(PowerUp powerUpInside) {
        mPowerUpInside = powerUpInside;
    }

    // check if object contains a power up
    public boolean containsPowerUp() {
        return mPowerUpInside != null;
    }

    // check if wall is breakable
    public boolean isBreakable() {
        return mWallType == WallType.BREAKABLE;
    }


    // ===============================================
    // HELPER METHODS
    // ===============================================

    // get a random color index between two numbers
    private int getRandomColorIndex(int iColorMin, int iColorMax) {
        int iMin = iColorMin;
        int iMax = iColorMax;
        return ThreadLocalRandom.current().nextInt(iMin, iMax);
    }

    // return a shifted color
    private Color getShiftedColor() {
        int iColorShift = 5;
        int iBlueMin = 0 + iColorShift;
        int iBlueMax = 255 - iColorShift;
        int iColorAdjustment = colorAdjustment(getBlue(), iBlueMin, iBlueMax, iColorShift);
        setBlue(getBlue() + iColorAdjustment);
        setGreen(getGreen() + iColorAdjustment);
        return(new Color(getRed(), getGreen(), getBlue()));

    }

    public int getRed() {
        return mRed;
    }

    public void setRed(int red) {
        mRed = red;
    }

    public int getGreen() {
        return mGreen;
    }

    public void setGreen(int green) {
        mGreen = green;
    }

    public int getBlue() {
        return mBlue;
    }

    public void setBlue(int blue) {
        mBlue = blue;
    }

    // adjust a color given a color index value, min, max, and incremental change value
    private int colorAdjustment(int nCol, int iMinCol, int iMaxCol, int nAdj) {
        if (nCol <= iMinCol && mColorDown) {
            mColorDown = false;
            return nAdj;
        } else if (nCol >= iMaxCol && !mColorDown) {
            mColorDown = true;
            return -nAdj;
        } else if (mColorDown) {
            return - nAdj;
        } else {
            return nAdj;
        }
    }

}
