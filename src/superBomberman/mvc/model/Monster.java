package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Monster object
 * Monster moves randomly throughout gameboard -> kills Bomberman if they collide
 * Monster can be killed by Blast (from Bomb)
 * Wall can contain a PowerUp -> spawns PowerUp when destroyed
 */

public class Monster extends Sprite {

    // constants for speed & size
    private final int SPEED = 4;
    private final int SIZE = (int) Math.sqrt(2 * (Square.SQUARE_LENGTH * Square.SQUARE_LENGTH)) / 2;

    // private instance members
    private Direction mDirectionToMove;
    private PowerUp mPowerUpInside;

    // constructor
    public Monster(Square startingSquare) {

        // call super constructor & set team
        super();
        setTeam(Team.FOE);

        //define the points on a cartesian grid
        ArrayList<Point> pntCs = new ArrayList<>();
        pntCs.add(new Point(0, 5));
        pntCs.add(new Point(1, 6));
        pntCs.add(new Point(2, 6));
        pntCs.add(new Point(2, 5));
        pntCs.add(new Point(4, 5));
        pntCs.add(new Point(4, 6));
        pntCs.add(new Point(5, 5));
        pntCs.add(new Point(6, 2));
        pntCs.add(new Point(6, -2));
        pntCs.add(new Point(5, -5));
        pntCs.add(new Point(4, -6));
        pntCs.add(new Point(4, -5));
        pntCs.add(new Point(2, -5));
        pntCs.add(new Point(2, -6));
        pntCs.add(new Point(1, -6));
        pntCs.add(new Point(-1, -5));
        pntCs.add(new Point(-2, -6));
        pntCs.add(new Point(-6, -6));
        pntCs.add(new Point(-4, -5));
        pntCs.add(new Point(-3, -5));
        pntCs.add(new Point(-1, 0));
        pntCs.add(new Point(-3, -3));
        pntCs.add(new Point(-5, -4));
        pntCs.add(new Point(-5, -5));
        pntCs.add(new Point(-6, -5));
        pntCs.add(new Point(-5, -2));
        pntCs.add(new Point(-4, -3));
        pntCs.add(new Point(-4, 3));
        pntCs.add(new Point(-5, 2));
        pntCs.add(new Point(-6, 5));
        pntCs.add(new Point(-5, 5));
        pntCs.add(new Point(-5, 4));
        pntCs.add(new Point(-3, 3));
        pntCs.add(new Point(-1, 0));
        pntCs.add(new Point(-3, 5));
        pntCs.add(new Point(-4, 5));
        pntCs.add(new Point(-6, 6));
        pntCs.add(new Point(-2, 6));
        pntCs.add(new Point(-1, 5));
        pntCs.add(new Point(1, 6));
        pntCs.add(new Point(2, 6));
        pntCs.add(new Point(2, 5));
        pntCs.add(new Point(4, 5));
        pntCs.add(new Point(4, 3));
        pntCs.add(new Point(3, 2));
        pntCs.add(new Point(3, -2));
        pntCs.add(new Point(4, -3));
        pntCs.add(new Point(4, -5));
        pntCs.add(new Point(2, -5));
        pntCs.add(new Point(2, -3));
        pntCs.add(new Point(2, 3));
        pntCs.add(new Point(2, 5));

        // assign polar points from cartesian points
        assignPolarPoints(pntCs);

        // set center, size, color, and random direction to move
        setCenter(startingSquare.getCenter());
        setSize(SIZE);
        setColor(Color.RED);
        setRandomDirection();

    }

    @Override
    public void move() {

        int iAdjustRow = 0;
        int iAdjustColumn = 0;
        double dAdjustX = 0;
        double dAdjustY = 0;


        if (mDirectionToMove == Direction.DOWN) {
            iAdjustRow = 1;
            dAdjustY = SPEED;
        } else if (mDirectionToMove == Direction.UP) {
            iAdjustRow = -1;
            dAdjustY = -SPEED;
        } else if (mDirectionToMove == Direction.LEFT) {
            iAdjustColumn = -1;
            dAdjustX = -SPEED;
        } else if (mDirectionToMove == Direction.RIGHT) {
            iAdjustColumn = 1;
            dAdjustX = SPEED;
        }

        int iNextRow = getCurrentSquare().getRow() + iAdjustRow;
        int iNextCol = getCurrentSquare().getColumn() + iAdjustColumn;

        Square targetSquare = CommandCenter.getInstance().getGameBoard().getSquare(iNextRow, iNextCol);

        if (!targetSquare.isBlocked() || !isPastSquareMidPoint()) {
            setDeltaX(dAdjustX);
            setDeltaY(dAdjustY);
            super.move();
        } else {
            setCenter(getCurrentSquare().getCenter());
            setRandomDirection();
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

    // get power up inside the object
    public PowerUp getPowerUpInside() {
        return mPowerUpInside;
    }

    // add a power up to the object
    public void setPowerUpInside(PowerUp powerUpInside) {
        mPowerUpInside = powerUpInside;
    }

    // check if object contains a power up
    public boolean containsPowerUp() {
        return mPowerUpInside != null;
    }

    // ****************
    //  HELPER METHODS
    // ****************

    // set random direction to move
    private void setRandomDirection() {
        // define random choice as int 1-4, representing each possible direction
        int iMin = 1;
        int iMax = 4;
        int iRandomChoice = ThreadLocalRandom.current().nextInt(iMin, iMax + 1);

        // assign direction based on random integer from above
        Direction randomDirection = Direction.UP;
        if (iRandomChoice == 1)
            randomDirection = Direction.RIGHT;
        else if (iRandomChoice == 2)
            randomDirection = Direction.DOWN;
        else if (iRandomChoice == 3)
            randomDirection = Direction.LEFT;

        // set the direction to move
        setDirectionToMove(randomDirection);
    }

    // set the direction to move
    private void setDirectionToMove(Direction direction) {
        mDirectionToMove = direction;
    }

    // check if object is past the mid-point of a square based on active direction moving
    private boolean isPastSquareMidPoint() {
        if (mDirectionToMove == Direction.DOWN) {
            return getCenter().getY() >= getCurrentSquare().getCenter().getY();
        } else if (mDirectionToMove == Direction.UP) {
            return getCenter().getY() <= getCurrentSquare().getCenter().getY();
        } else if (mDirectionToMove == Direction.LEFT) {
            return getCenter().getX() <= getCurrentSquare().getCenter().getX();
        } else if (mDirectionToMove == Direction.RIGHT) {
            return getCenter().getX() >= getCurrentSquare().getCenter().getX();
        }
        throw new IllegalArgumentException("could not determine direction");
    }

}