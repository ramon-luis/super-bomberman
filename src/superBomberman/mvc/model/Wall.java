package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Wall object
 * Wall covers single square on gameboard
 * Wall can be solid or breakable -> blasts can destroy breakable walls
 * Wall blocks movement of Bomberman, Monster, and blast
 * Wall can contain a PowerUp if breakable -> spawns PowerUp when destroyed
 */

public class Wall extends Sprite {

    // enum for type of wall
    public enum WallType {SOLID, BREAKABLE}

    // constant for size
    public static final int SIZE = Square.SQUARE_LENGTH / 2 + 5;

    // private instance members
    private WallType mWallType;
    private PowerUp mPowerUpInside;

    // constructor
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

        // set color, center, and size
        setColor((mWallType == WallType.BREAKABLE) ? new Color(0, 125, 255) : new Color(55, 55, 55));
        setCenter(square.getCenter());
        setSize(SIZE);
    }

    @Override
    public void draw(Graphics g) {
        // draw Wall shape
        super.draw(g);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);

        // set color and draw inner square
        g.setColor((mWallType == WallType.BREAKABLE) ? Color.CYAN : new Color(105, 105, 105));
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

}
