package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * PowerUpBlast object
 * PowerUpBlast increases the power of Bomberman blast -> extends blast by an additional square in each direction
 */


public class PowerUpBlast extends PowerUp {

    // constant for radius
    private static final int RADIUS = Square.SQUARE_LENGTH / 2 - 5;

    // private instance members
    private Color mSquareColor;  // used to flash color of square that surrounds power up

    // constructor
    public PowerUpBlast() {
        // call super constructor, team, and type of power up
        super();
        setTeam(Team.POWERUP);
        setPowerUpType(PowerUpType.BLAST);

        //define the points on a cartesian grid
        ArrayList<Point> pntCs = new ArrayList<>();

        // points to draw a center blast object
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

        // assign polar points from cartesian points
        assignPolarPoints(pntCs);

        // set radius, center
        setSize(RADIUS);
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
        int iRecX = (int) getCenter().getX() - RADIUS;
        int iRecY = (int) getCenter().getY() - RADIUS;
        g.fillRect(iRecX, iRecY, RADIUS * 2, RADIUS * 2);

        // draw the polygon over the square
        setColor(Color.RED);
        super.draw(g);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

}
