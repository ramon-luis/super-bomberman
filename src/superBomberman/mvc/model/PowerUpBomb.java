package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * PowerUpBomb object
 * PowerUpBomb increases the number of bombs that Bomberman can use
 */


public class PowerUpBomb extends PowerUp {

    // ===============================================
    // FIELDS
    // ===============================================

    // private instance members
    private Color mSquareColor;  // used to flash color of square that surrounds power up

    // ===============================================
    // CONSTRUCTOR
    // ===============================================

    public PowerUpBomb() {
        // call super constructor, team, and type of power up
        super();

        // set team, type, shape, and size
        setTeam(Team.POWERUP);
        setPowerUpType(PowerUpType.BOMB);
        setShape(getShapeAsCartesianPoints());
    }

    // ===============================================
    // METHODS
    // ===============================================

    @Override
    public void draw(Graphics g) {
        // alternate the color the square so that it flashes
        if (mSquareColor == Color.YELLOW) {
            mSquareColor = Color.WHITE;
        } else {
            mSquareColor = Color.YELLOW;
        }

        // draw a square
        g.setColor(mSquareColor);
        int iRecX = (int) getCenter().getX() - SIZE;
        int iRecY = (int) getCenter().getY() - SIZE;
        g.fillRect(iRecX, iRecY, SIZE * 2, SIZE * 2);

        // draw the polygon over the square - bomb wick
        setColor(Color.BLACK);
        super.draw(g);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);

        // draw a circle over the square -> main bomb shape
        int iOvalX = (int) getCenter().getX() - SIZE / 2;
        int iOvalY = (int) getCenter().getY() - SIZE / 2;
        g.setColor(Color.BLACK);
        g.fillOval(iOvalX, iOvalY, SIZE * 2 - 15, SIZE * 2 - 15);
    }

    // ===============================================
    // HELPER METHODS
    // ===============================================

    // get the shape of the object
    private ArrayList<Point> getShapeAsCartesianPoints() {

        // define list to store points
        ArrayList<Point> pntCs = new ArrayList<>();

        // add each point to outline shape
        pntCs.add(new Point(10, 0));
        pntCs.add(new Point(11, -1));
        pntCs.add(new Point(12, -2));
        pntCs.add(new Point(12, -3));
        pntCs.add(new Point(12, -4));
        pntCs.add(new Point(13, -5));
        pntCs.add(new Point(14, -6));
        pntCs.add(new Point(14, -7));
        pntCs.add(new Point(14, -8));
        pntCs.add(new Point(15, -9));
        pntCs.add(new Point(15, -10));
        pntCs.add(new Point(14, -10));
        pntCs.add(new Point(14, -9));
        pntCs.add(new Point(13, -8));
        pntCs.add(new Point(13, -7));
        pntCs.add(new Point(13, -6));
        pntCs.add(new Point(12, -5));
        pntCs.add(new Point(11, -4));
        pntCs.add(new Point(11, -3));
        pntCs.add(new Point(11, -2));
        pntCs.add(new Point(10, -1));
        pntCs.add(new Point(9, 0));

        // return the list
        return pntCs;
    }


}