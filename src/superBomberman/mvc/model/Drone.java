package superBomberman.mvc.model;

import superBomberman.sounds.Sound;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Drone object is a type of enemy
 * Drone randomly creates "shock" that can kill
 * Drone moves at a slow speed and requires 2 hits to be killed
 */


public class Drone extends Enemy {

    public static final int SPEED = 3;
    public static final int INITIAL_HITS_TO_DESTROY = 2;
    public static final int SHOCK_POWER = 1;

    private int mTick;
    private int mActiveShockExpiry;

    // constructor
    public Drone() {

        // call super constructor
        super();

        // set shape, color, speed, and hits to destroy
        setShape(getShapeAsCartesianPoints());
        setColor(Color.lightGray);
        setSpeed(SPEED);
        setHitsToDestroy(INITIAL_HITS_TO_DESTROY);
    }

    @Override
    public void move() {
        // tick
        tick();
        updateActiveShockExpiry();

        // rotate
        if (!hasActiveShocks() && isRandomTick(getRandomTick(1,3))) {
            setOrientation(getRandomOrientation());
            setOrientation(getOrientation() + 1);
            super.move();
        }

        // create shock
        if (!hasActiveShocks() && isRandomTick(getRandomTick(35, 75))) {
            setOrientation(0);
            shock();

        }
    }

    private int getRandomOrientation() {
        int iMin = 0;
        int iMax = 360;
        return ThreadLocalRandom.current().nextInt(iMin, iMax + 1);
    }

    private boolean isRandomTick(int randomTick) {
        return mTick %  randomTick == 0;
    }

    private int getRandomTick(int tickMin, int tickMax) {
        int iMin = tickMin;
        int iMax = tickMax;
        return ThreadLocalRandom.current().nextInt(iMin, iMax + 1);
    }

    private boolean hasActiveShocks() {
        return mActiveShockExpiry > 0;
    }

    private void updateActiveShockExpiry() {
        if (mActiveShockExpiry > 0)
            mActiveShockExpiry--;
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (getHitsToDestroy() == 1) {
            setColor(Color.yellow);  // add fast flash
        }
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

    private void tick() {
        if (mTick == Integer.MAX_VALUE)
            mTick = 0;
        else
            mTick++;
    }

    // create shock
    private void shock() {
            // get map of squares that should contain shocks and their direction from current square
            Map<Square, Direction> shockSquares = getShockSquares();

            // add a shock (with direction) for each square in map to the OpsList
            for (Square shockSquare : shockSquares.keySet()) {
                Shock shock = new Shock(shockSquares.get(shockSquare));
                mActiveShockExpiry = shock.getExpire();
                shock.setCenter(shockSquare.getCenter());
                CommandCenter.getInstance().getOpsList().enqueue(shock, CollisionOp.Operation.ADD);
            }

            // update shock sound
            Sound.playSound("blast.wav");
    }


    // returns a map of squares and directions used to create new shocks from the drone
    private Map<Square, Direction> getShockSquares() {
        // create map to return & add current square
        Map shockSquares = new HashMap<>();

        // loop through each direction
        for (Sprite.Direction direction : Sprite.Direction.values()) {
            // within each direction: loop until shock size matches shock power
            for (int iSquareOffset = 1; iSquareOffset <= SHOCK_POWER; iSquareOffset++) {
                // get a shock square
                Square shockSquare = getShockSquare(iSquareOffset, direction);
                // if shock square is not a solid wall, then add to map & continue
                if (!shockSquare.isWall()) {
                    shockSquares.put(shockSquare, direction);
                } else {
                    break;  // else move onto different direction (shock stopped by wall)
                }
            }
        }

        // return the map of squares and directions
        return shockSquares;
    }

    // returns a single square -> used to place a Shock
    private Square getShockSquare(int offSet, Sprite.Direction direction) {
        // get the row and col for current square (i.e. bomb location)
        int iRow = getCurrentSquare().getRow();
        int iCol = getCurrentSquare().getColumn();

        // variables to define what square will have the shock
        int iColAdjust = 0;
        int iRowAdjust = 0;

        // adjust the row & col based on the direction
        if (direction == Sprite.Direction.LEFT) {
            iColAdjust = -offSet;
        } else if (direction == Sprite.Direction.RIGHT) {
            iColAdjust = offSet;
        } else if (direction == Sprite.Direction.DOWN) {
            iRowAdjust = -offSet;
        } else if (direction == Sprite.Direction.UP) {
            iRowAdjust = offSet;
        }

        // return the square
        return CommandCenter.getInstance().getGameBoard().getSquare(iRow + iRowAdjust, iCol + iColAdjust);
    }

    // get the shape of the object
    private ArrayList<Point> getShapeAsCartesianPoints() {

        // define list to store points
        ArrayList<Point> pntCs = new ArrayList<>();

        // add each point to outline shape
        pntCs.add(new Point(-2,2));
        pntCs.add(new Point(-3,7));
        pntCs.add(new Point(-6,10));
        pntCs.add(new Point(-3,12));
        pntCs.add(new Point(-6,14));
        pntCs.add(new Point(-5,15));
        pntCs.add(new Point(5,15));
        pntCs.add(new Point(6,14));
        pntCs.add(new Point(3,12));
        pntCs.add(new Point(6,10));
        pntCs.add(new Point(3,7));
        pntCs.add(new Point(2,2));
        pntCs.add(new Point(7,3));
        pntCs.add(new Point(10,6));
        pntCs.add(new Point(12,3));
        pntCs.add(new Point(14,6));
        pntCs.add(new Point(15,5));
        pntCs.add(new Point(15,-5));
        pntCs.add(new Point(14,-6));
        pntCs.add(new Point(12,-3));
        pntCs.add(new Point(10,-6));
        pntCs.add(new Point(7,-3));
        pntCs.add(new Point(2,-2));
        pntCs.add(new Point(3,-7));
        pntCs.add(new Point(6,-10));
        pntCs.add(new Point(3,-12));
        pntCs.add(new Point(6,-14));
        pntCs.add(new Point(5,-15));
        pntCs.add(new Point(-5,-15));
        pntCs.add(new Point(-6,-14));
        pntCs.add(new Point(-3,-12));
        pntCs.add(new Point(-6,-10));
        pntCs.add(new Point(-3,-7));
        pntCs.add(new Point(-2,-2));
        pntCs.add(new Point(-7,-3));
        pntCs.add(new Point(-10,-6));
        pntCs.add(new Point(-12,-3));
        pntCs.add(new Point(-14,-6));
        pntCs.add(new Point(-15,-5));
        pntCs.add(new Point(-15,5));
        pntCs.add(new Point(-14,6));
        pntCs.add(new Point(-12,3));
        pntCs.add(new Point(-10,6));
        pntCs.add(new Point(-7,3));
        pntCs.add(new Point(-2,2));

        // return the list
        return pntCs;
    }
}
