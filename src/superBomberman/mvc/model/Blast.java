package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Blast is created when a Bomb expires -> expire creates multiple Blast objects
 * Blast can collide with Bomberman, Monsters, and BreakableWalls -> cannot pass solid wall
 * Blast is fixed to a specific square
 */

public class Blast extends Sprite {

    // constants for default radius and expiration
    private static final int EXPIRE = 20;
    private static final int RADIUS = (int) Math.sqrt(2 * (Square.SQUARE_LENGTH * Square.SQUARE_LENGTH)) / 2 - EXPIRE / 2;


    // private members
    private Direction mDirection;
    private boolean bFlexSmaller;
    private ArrayList<Monster> mBlastedMonsters;

    // constructor
    public Blast(Square square, Direction direction) {
        // call super constructor
        super();

        // assign direction -> used to determine shape of blast
        mDirection = direction;
        bFlexSmaller = true;

        // set team expiration, radius, and center
        setTeam(Team.BLAST);
        setExpire(EXPIRE);
        setSize(RADIUS);
        setCenter(square.getCenter());

        // assign polar points from cartesian points
        assignPolarPoints(getPoints());

        // create empty list for blasted monsters
        mBlastedMonsters = new ArrayList<>();

        // add to opsList
        CommandCenter.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.ADD);
    }

    public void addToBlastedMonsters(Monster blastedMonster) {
        mBlastedMonsters.add(blastedMonster);
    }

    public boolean alreadyBlastedThisMonster(Monster monster) {
        return mBlastedMonsters.contains(monster);
    }

    @Override
    public void move() {
        // call super method move
        super.move();

        // remove if expired
        if (getExpire() == 0)
            CommandCenter.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
        else {
            updateRadius();  // update radius so that it grows & shrinks
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
    private void updateRadius() {
        if (getExpire() > EXPIRE / 2) {
            setSize(getRadius() + 1);
        } else  {
            setSize(getRadius() - 1);
        }
    }

    // returns color based on time remaining -> used to fill blast
    private Color getFillColor() {
        Color cFill;

        // assign color based on time remaining
        if (getExpire() > EXPIRE / 4 * 3) {
            cFill = new Color(255, 50, 0);
        } else if (getExpire() > EXPIRE / 4 * 2) {
            cFill = new Color(255, 255, 0);
        } else if (getExpire() > EXPIRE / 4) {
            cFill = new Color(255, 255, 255);
        } else {
            cFill = new Color(255,0,0);
        }

        return cFill;
    }

    // returns array of points used to draw the blast shape -> dependent on direction of blast
    private ArrayList<Point> getPoints() {
        //define the points on cartesian grid
        ArrayList<Point> pntCs = new ArrayList<>();

        if (mDirection == Direction.DOWN || mDirection == Direction.UP) {
            // points for a vertical blast
            pntCs.add(new Point(-10, -5));
            pntCs.add(new Point(-9, -4));
            pntCs.add(new Point(-8, -5));
            pntCs.add(new Point(-7, -3));
            pntCs.add(new Point(-6, -4));
            pntCs.add(new Point(-5, -3));
            pntCs.add(new Point(-4, -5));
            pntCs.add(new Point(-3, -4));
            pntCs.add(new Point(-2, -5));
            pntCs.add(new Point(-1, -3));
            pntCs.add(new Point(0, -4));
            pntCs.add(new Point(1, -3));
            pntCs.add(new Point(2, -5));
            pntCs.add(new Point(3, -4));
            pntCs.add(new Point(4, -5));
            pntCs.add(new Point(5, -3));
            pntCs.add(new Point(6, -4));
            pntCs.add(new Point(7, -3));
            pntCs.add(new Point(8, -5));
            pntCs.add(new Point(9, -4));
            pntCs.add(new Point(10, -5));
            pntCs.add(new Point(10, 5));
            pntCs.add(new Point(9, 4));
            pntCs.add(new Point(8, 5));
            pntCs.add(new Point(7, 3));
            pntCs.add(new Point(6, 4));
            pntCs.add(new Point(5, 3));
            pntCs.add(new Point(4, 5));
            pntCs.add(new Point(3, 4));
            pntCs.add(new Point(2, 5));
            pntCs.add(new Point(1, 3));
            pntCs.add(new Point(0, 4));
            pntCs.add(new Point(-1, 3));
            pntCs.add(new Point(-2, 5));
            pntCs.add(new Point(-3, 4));
            pntCs.add(new Point(-4, 5));
            pntCs.add(new Point(-5, 3));
            pntCs.add(new Point(-6, 4));
            pntCs.add(new Point(-7, 3));
            pntCs.add(new Point(-8, 5));
            pntCs.add(new Point(-9, 4));
            pntCs.add(new Point(-10, 5));
        } else if (mDirection == Direction.LEFT || mDirection == Direction.RIGHT) {
            // points for a horizontal blast
            pntCs.add(new Point(-5, 10));
            pntCs.add(new Point(-4, 9));
            pntCs.add(new Point(-5, 8));
            pntCs.add(new Point(-3, 7));
            pntCs.add(new Point(-4, 6));
            pntCs.add(new Point(-3, 5));
            pntCs.add(new Point(-5, 4));
            pntCs.add(new Point(-4, 3));
            pntCs.add(new Point(-5, 2));
            pntCs.add(new Point(-3, 1));
            pntCs.add(new Point(-4, 0));
            pntCs.add(new Point(-3, -1));
            pntCs.add(new Point(-5, -2));
            pntCs.add(new Point(-4, -3));
            pntCs.add(new Point(-5, -4));
            pntCs.add(new Point(-3, -5));
            pntCs.add(new Point(-4, -6));
            pntCs.add(new Point(-3, -7));
            pntCs.add(new Point(-5, -8));
            pntCs.add(new Point(-4, -9));
            pntCs.add(new Point(-5, -10));
            pntCs.add(new Point(5, -10));
            pntCs.add(new Point(4, -9));
            pntCs.add(new Point(5, -8));
            pntCs.add(new Point(3, -7));
            pntCs.add(new Point(4, -6));
            pntCs.add(new Point(3, -5));
            pntCs.add(new Point(5, -4));
            pntCs.add(new Point(4, -3));
            pntCs.add(new Point(5, -2));
            pntCs.add(new Point(3, -1));
            pntCs.add(new Point(4, 0));
            pntCs.add(new Point(3, 1));
            pntCs.add(new Point(5, 2));
            pntCs.add(new Point(4, 3));
            pntCs.add(new Point(5, 4));
            pntCs.add(new Point(3, 5));
            pntCs.add(new Point(4, 6));
            pntCs.add(new Point(3, 7));
            pntCs.add(new Point(5, 8));
            pntCs.add(new Point(4, 9));
            pntCs.add(new Point(5, 10));
        } else {
            // points for a center blast (direction == null)
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
        }

        return pntCs;
    }

}
