package superBomberman.mvc.model;

import superBomberman.sounds.Sound;

import java.awt.*;
import java.util.ArrayList;

/**
 * Shock is created from a Drone
 * Shock can collide with Bomberman and cannot pass walls of any type
 * Shock is fixed to a specific square
 */

public class Shock extends Enemy {

    // constants for default radius and expiration
    private static final int EXPIRE = 8;
    private static final int SIZE = (int) Math.sqrt(2 * (Square.SQUARE_LENGTH * Square.SQUARE_LENGTH)) / 2 - EXPIRE / 2;


    // private members
    private Direction mDirection;

    // constructor
    public Shock(Direction direction) {
        // call super constructor
        super();

        // set instance members
        mDirection = direction;  // assign direction -> used to determine shape of shock

        // set team, shape, size, radius, and expiry
        setShape(getShapeAsCartesianPoints());
        setSize(SIZE);
        setExpire(EXPIRE);
    }

    @Override
    public void move() {
        // call super method move
        super.move();

        // remove if expired
        if (getExpire() == 0) {
            CommandCenter.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
        } else {
            updateSize();  // update radius so that it grows & shrinks
            setExpire(getExpire() - 1);  // move towards expiry
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);

        // set color
        Color cFill = getFillColor();

        g.setColor(cFill);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }


    // ****************
    //  HELPER METHODS
    // ****************

    // grow size for 1st half of life, then shrink
    private void updateSize() {
        if (getExpire() > EXPIRE / 2) {
            setSize(getSize() + 1);
        } else {
            setSize(getSize() - 1);
        }
    }

    // returns color based on time remaining -> used to fill shock
    private Color getFillColor() {
        Color cFill;

        // assign color based on time remaining
        if (getExpire() > EXPIRE / 4 * 3) {
            cFill = new Color(255, 255, 255);
        } else if (getExpire() > EXPIRE / 4 * 2) {
            cFill = new Color(0, 255, 0);
        } else if (getExpire() > EXPIRE / 4) {
            cFill = new Color(255, 255, 0);
        } else {
            cFill = new Color(255, 255, 255);
        }

        return cFill;
    }

    // returns array of points used to draw the shock shape -> dependent on direction of shock
    private ArrayList<Point> getShapeAsCartesianPoints() {
        //define the points on cartesian grid
        ArrayList<Point> pntCs = new ArrayList<>();

        if (mDirection == Direction.DOWN || mDirection == Direction.UP) {
            // points for a vertical shock
            pntCs.add(new Point(-15,2));
            pntCs.add(new Point(-11,5));
            pntCs.add(new Point(-9,3));
            pntCs.add(new Point(-7,5));
            pntCs.add(new Point(-5,3));
            pntCs.add(new Point(-3,5));
            pntCs.add(new Point(0,4));
            pntCs.add(new Point(3,5));
            pntCs.add(new Point(5,3));
            pntCs.add(new Point(7,5));
            pntCs.add(new Point(9,3));
            pntCs.add(new Point(11,5));
            pntCs.add(new Point(15,2));
            pntCs.add(new Point(15,-2));
            pntCs.add(new Point(11,-5));
            pntCs.add(new Point(9,-3));
            pntCs.add(new Point(7,-5));
            pntCs.add(new Point(5,-3));
            pntCs.add(new Point(3,-5));
            pntCs.add(new Point(0,-4));
            pntCs.add(new Point(-3,-5));
            pntCs.add(new Point(-5,-3));
            pntCs.add(new Point(-7,-5));
            pntCs.add(new Point(-9,-3));
            pntCs.add(new Point(-11,-5));
            pntCs.add(new Point(-15,-2));
            pntCs.add(new Point(-15,2));
            pntCs.add(new Point(-11,5));
        } else if (mDirection == Direction.LEFT || mDirection == Direction.RIGHT) {
            // points for a horizontal shock
            pntCs.add(new Point(2,15));
            pntCs.add(new Point(5,11));
            pntCs.add(new Point(3,9));
            pntCs.add(new Point(5,7));
            pntCs.add(new Point(3,5));
            pntCs.add(new Point(5,3));
            pntCs.add(new Point(4,0));
            pntCs.add(new Point(5,-3));
            pntCs.add(new Point(3,-5));
            pntCs.add(new Point(5,-7));
            pntCs.add(new Point(3,-9));
            pntCs.add(new Point(5,-11));
            pntCs.add(new Point(2,-15));
            pntCs.add(new Point(-2,-15));
            pntCs.add(new Point(-5,-11));
            pntCs.add(new Point(-3,-9));
            pntCs.add(new Point(-5,-7));
            pntCs.add(new Point(-3,-5));
            pntCs.add(new Point(-5,-3));
            pntCs.add(new Point(-4,0));
            pntCs.add(new Point(-5,3));
            pntCs.add(new Point(-3,5));
            pntCs.add(new Point(-5,7));
            pntCs.add(new Point(-3,9));
            pntCs.add(new Point(-5,11));
            pntCs.add(new Point(-2,15));
            pntCs.add(new Point(2,15));
            pntCs.add(new Point(5,11));
        }

        return pntCs;
    }



}