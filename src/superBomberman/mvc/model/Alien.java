package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Alien object is a type of enemy
 * Alien moves at a fast speed and requires 3 hits to be killed
 */


//TODO
    // add flashing or fade functionality based on hits left to destroy

public class Alien extends Enemy {

    // ===============================================
    // FIELDS
    // ===============================================

    public static final int SPEED = 6;
    public static final int INITIAL_HITS_TO_DESTROY = 3;


    // ===============================================
    // CONSTRUCTOR
    // ===============================================

    public Alien() {

        // call super constructor
        super();

        // set shape, color, speed, and hits to destroy
        setShape(getShapeAsCartesianPoints());
        setColor(Color.GREEN);
        setSize(getSize() + 10);
        setSpeed(SPEED);
        setHitsToDestroy(INITIAL_HITS_TO_DESTROY);
    }

    // ===============================================
    // METHODS
    // ===============================================

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (getHitsToDestroy() == INITIAL_HITS_TO_DESTROY - 1) {
            setColor(Color.ORANGE);  // add slow flash
        } else if (getHitsToDestroy() == 1) {
            setColor(Color.YELLOW);  // add fast flash
        }
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);

        // draw outer eye
        int iDrawX = (int) getCenter().getX() - getSize() / 4;
        int iDrawY = (int) getCenter().getY() - getSize() / 2;
        g.setColor(Color.WHITE);
        g.fillOval(iDrawX, iDrawY, getSize() / 2, getSize() / 2);

        // draw inner eye
        iDrawX= (int) getCenter().getX() - getSize() / 8;
        iDrawY = (int) getCenter().getY() - getSize() / 3;
        g.setColor(Color.BLACK);
        g.fillOval(iDrawX, iDrawY, getSize() / 4, getSize() / 4);
    }

    // ===============================================
    // HELPER METHODS
    // ===============================================

    // get the shape of the object
    private ArrayList<Point> getShapeAsCartesianPoints() {

        // define list to store points
        ArrayList<Point> pntCs = new ArrayList<>();

        // add each point to outline shape
        pntCs.add(new Point(5, 15));
        pntCs.add(new Point(6, 14));
        pntCs.add(new Point(7, 13));
        pntCs.add(new Point(7, 9));
        pntCs.add(new Point(8, 8));
        pntCs.add(new Point(10, 8));
        pntCs.add(new Point(11, 15));
        pntCs.add(new Point(13, 15));
        pntCs.add(new Point(14, 13));
        pntCs.add(new Point(15, 8));
        pntCs.add(new Point(15, -8));
        pntCs.add(new Point(14, -13));
        pntCs.add(new Point(13, -15));
        pntCs.add(new Point(11, -15));
        pntCs.add(new Point(10, -8));
        pntCs.add(new Point(8, -8));
        pntCs.add(new Point(7, -9));
        pntCs.add(new Point(7, -13));
        pntCs.add(new Point(6, -14));
        pntCs.add(new Point(5, -15));
        pntCs.add(new Point(-15, -15));
        pntCs.add(new Point(1, -14));
        pntCs.add(new Point(-6, -13));
        pntCs.add(new Point(1, -12));
        pntCs.add(new Point(-15, -11));
        pntCs.add(new Point(1, -10));
        pntCs.add(new Point(-6, -9));
        pntCs.add(new Point(1, -8));
        pntCs.add(new Point(-15, -7));
        pntCs.add(new Point(1, -6));
        pntCs.add(new Point(-6, -5));
        pntCs.add(new Point(1, -4));
        pntCs.add(new Point(-15, -3));
        pntCs.add(new Point(1, -2));
        pntCs.add(new Point(-6, -1));
        pntCs.add(new Point(-2, 0));
        pntCs.add(new Point(-6, 1));
        pntCs.add(new Point(1, 2));
        pntCs.add(new Point(-15, 3));
        pntCs.add(new Point(1, 4));
        pntCs.add(new Point(-6, 5));
        pntCs.add(new Point(1, 6));
        pntCs.add(new Point(-15, 7));
        pntCs.add(new Point(1, 8));
        pntCs.add(new Point(-6, 9));
        pntCs.add(new Point(1, 10));
        pntCs.add(new Point(-15, 11));
        pntCs.add(new Point(1, 12));
        pntCs.add(new Point(-6, 13));
        pntCs.add(new Point(1, 14));
        pntCs.add(new Point(-15, 15));
        pntCs.add(new Point(5, 15));

        // return the list
        return pntCs;
    }
}