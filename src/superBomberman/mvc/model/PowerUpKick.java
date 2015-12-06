package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * PowerUpKick object
 * PowerUpKick gives Bomberman the ability to kick a Bomb
 */


public class PowerUpKick extends PowerUp {

    // private instance members
    private Color mSquareColor;  // used to flash color of square that surrounds power up

    // constructor
    public PowerUpKick() {
        // call super constructor, team, and type of power up
        super();

        // set team, type, shape, and size
        setTeam(Team.POWERUP);
        setPowerUpType(PowerUpType.KICK);
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
        setColor(Color.BLACK);
        super.draw(g);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);

    }

    // get the shape of the object
    private ArrayList<Point> getShapeAsCartesianPoints() {

        // define list to store points
        ArrayList<Point> pntCs = new ArrayList<>();

        // add each point to outline shape
        pntCs.add(new Point(-15,-15));
        pntCs.add(new Point(-10,-15));
        pntCs.add(new Point(-5,-14));
        pntCs.add(new Point(0,-14));
        pntCs.add(new Point(11,-15));
        pntCs.add(new Point(15,-15));
        pntCs.add(new Point(15,0));
        pntCs.add(new Point(8,0));
        pntCs.add(new Point(0,-1));
        pntCs.add(new Point(-3,8));
        pntCs.add(new Point(-6,14));
        pntCs.add(new Point(-9,15));
        pntCs.add(new Point(-15,15));
        pntCs.add(new Point(-15,-15));

        // return the list
        return pntCs;
    }


}