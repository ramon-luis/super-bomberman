package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * BodyBlast object occurs at key action events (Enemy hit, Bomberman dies, PowerUp processed)
 * BodyBlast does NOT collide with other objects
 */


public class BodyBlast extends Sprite {

    // ===============================================
    // FIELDS
    // ===============================================

    // constants for default radius and expiration
    public static final int EXPIRE = 16;
    public static final int SIZE = Square.SQUARE_LENGTH / 2;

    // ===============================================
    // CONSTRUCTOR
    // ===============================================

    public BodyBlast() {
        // call super constructor
        super();

        setTeam(Team.DISPLAY);

        // set shape, color, speed, and hits to destroy
        setShape(getShapeAsCartesianPoints());
        setColor(Color.WHITE);
        setSize(SIZE);
        setExpire(EXPIRE);
    }

    // ===============================================
    // METHODS
    // ===============================================

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
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

    // ===============================================
    // HELPER METHODS
    // ===============================================

    // grow size for 1st half of life, then shrink
    private void updateSize() {
        if (getExpire() > EXPIRE / 2) {
            setSize(getSize() + 1);
        } else {
            setSize(getSize() - 1);
        }
    }

    // returns array of points used to draw the blast shape -> dependent on direction of blast
    private ArrayList<Point> getShapeAsCartesianPoints() {

        //define the list
        ArrayList<Point> pntCs = new ArrayList<>();

        // add points for Cartesian shape
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

        // return the list of points
        return pntCs;
    }


}
