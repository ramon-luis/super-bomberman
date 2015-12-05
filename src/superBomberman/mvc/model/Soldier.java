package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Soldier object is a type of enemy
 * Soldier moves at a slow speed and requires 1 hit to be killed
 */

public class Soldier extends Enemy {

    public static final int SPEED = 3;
    public static final int INITIAL_HITS_TO_DESTROY = 1;

    // constructor
    public Soldier() {

        // call super constructor
        super();

        // set shape, color, speed, and hits to destroy
        setSize(getSize() + 5);
        setShape(getShapeAsCartesianPoints());
        setColor(Color.RED);
        setSpeed(SPEED);
        setHitsToDestroy(INITIAL_HITS_TO_DESTROY);

    }

    // get the shape of the object
    private ArrayList<Point> getShapeAsCartesianPoints() {

        // define list to store points
        ArrayList<Point> pntCs = new ArrayList<>();

        // add each point to outline shape
        pntCs.add(new Point(1,6));
        pntCs.add(new Point(2,6));
        pntCs.add(new Point(2,5));
        pntCs.add(new Point(4,5));
        pntCs.add(new Point(4,6));
        pntCs.add(new Point(5,5));
        pntCs.add(new Point(6,2));
        pntCs.add(new Point(6,-2));
        pntCs.add(new Point(5,-5));
        pntCs.add(new Point(4,-6));
        pntCs.add(new Point(4,-5));
        pntCs.add(new Point(2,-5));
        pntCs.add(new Point(2,-6));
        pntCs.add(new Point(1,-6));
        pntCs.add(new Point(-1,-5));
        pntCs.add(new Point(-2,-6));
        pntCs.add(new Point(-6,-6));
        pntCs.add(new Point(-4,-5));
        pntCs.add(new Point(-3,-5));
        pntCs.add(new Point(-3,-3));
        pntCs.add(new Point(-5,-4));
        pntCs.add(new Point(-5,-5));
        pntCs.add(new Point(-6,-5));
        pntCs.add(new Point(-5,-2));
        pntCs.add(new Point(-4,-3));
        pntCs.add(new Point(-4,3));
        pntCs.add(new Point(-5,2));
        pntCs.add(new Point(-6,5));
        pntCs.add(new Point(-5,5));
        pntCs.add(new Point(-5,4));
        pntCs.add(new Point(-3,3));
        pntCs.add(new Point(-3,5));
        pntCs.add(new Point(-4,5));
        pntCs.add(new Point(-6,6));
        pntCs.add(new Point(-2,6));
        pntCs.add(new Point(-1,5));
        pntCs.add(new Point(1,6));
        pntCs.add(new Point(2,6));
        pntCs.add(new Point(2,5));
        pntCs.add(new Point(4,5));
        pntCs.add(new Point(4,3));
        pntCs.add(new Point(3,2));
        pntCs.add(new Point(3,-2));
        pntCs.add(new Point(4,-3));
        pntCs.add(new Point(4,-5));
        pntCs.add(new Point(2,-5));
        pntCs.add(new Point(2,-3));
        pntCs.add(new Point(2,3));
        pntCs.add(new Point(2,5));
        pntCs.add(new Point(2,6));
        pntCs.add(new Point(1,6));

        // return the list
        return pntCs;
    }

}