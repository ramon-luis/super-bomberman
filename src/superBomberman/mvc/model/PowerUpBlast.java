package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * PowerUpBlast object
 * PowerUpBlast increases the power of Bomberman blast -> extends blast by an additional square in each direction
 */

public class PowerUpBlast extends PowerUp {

    // private instance members
    private Color mSquareColor;  // used to flash color of square that surrounds power up

    // constructor
    public PowerUpBlast() {
        // call super constructor
        super();

        // set team, type, shape, and size
        setTeam(Team.POWERUP);
        setPowerUpType(PowerUpType.BLAST);
        setShape(getShapeAsCartesianPoints());
    }

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

        // draw the polygon over the square
        setColor(Color.RED);
        super.draw(g);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

    // get the shape of the object
    private ArrayList<Point> getShapeAsCartesianPoints() {

        // define list to store points
        ArrayList<Point> pntCs = new ArrayList<>();

        // add each point to outline shape
        pntCs.add(new Point(-5, -5));
        pntCs.add(new Point(-4, -3));
        pntCs.add(new Point(-3, -4));
        pntCs.add(new Point(-2, -3));
        pntCs.add(new Point(-1, -5));
        pntCs.add(new Point(0, -4));
        pntCs.add(new Point(1, -5));
        pntCs.add(new Point(2, -3));
        pntCs.add(new Point(3, -4));
        pntCs.add(new Point(4, -3));
        pntCs.add(new Point(5, -5));
        pntCs.add(new Point(5, -5));
        pntCs.add(new Point(5, -5));
        pntCs.add(new Point(3, -4));
        pntCs.add(new Point(4, -3));
        pntCs.add(new Point(3, -2));
        pntCs.add(new Point(5, -1));
        pntCs.add(new Point(4, 0));
        pntCs.add(new Point(5, 1));
        pntCs.add(new Point(3, 2));
        pntCs.add(new Point(4, 3));
        pntCs.add(new Point(3, 4));
        pntCs.add(new Point(5, 5));
        pntCs.add(new Point(5, 5));
        pntCs.add(new Point(5, 5));
        pntCs.add(new Point(4, 3));
        pntCs.add(new Point(3, 4));
        pntCs.add(new Point(2, 3));
        pntCs.add(new Point(1, 5));
        pntCs.add(new Point(0, 4));
        pntCs.add(new Point(-1, 5));
        pntCs.add(new Point(-2, 3));
        pntCs.add(new Point(-3, 4));
        pntCs.add(new Point(-4, 3));
        pntCs.add(new Point(-5, 5));
        pntCs.add(new Point(-5, 5));
        pntCs.add(new Point(-5, 5));
        pntCs.add(new Point(-3, 4));
        pntCs.add(new Point(-4, 3));
        pntCs.add(new Point(-3, 2));
        pntCs.add(new Point(-5, 1));
        pntCs.add(new Point(-4, 0));
        pntCs.add(new Point(-5, -1));
        pntCs.add(new Point(-3, -2));
        pntCs.add(new Point(-4, -3));
        pntCs.add(new Point(-3, -4));
        pntCs.add(new Point(-5, -5));
        pntCs.add(new Point(-5, -5));

        // return the list
        return pntCs;
    }

}
