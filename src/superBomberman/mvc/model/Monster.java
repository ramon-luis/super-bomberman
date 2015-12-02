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

    public enum MonsterType {SOLDIER, ALIEN}


    // constants for speed & size
    private final int SIZE = (int) Math.sqrt(2 * (Square.SQUARE_LENGTH * Square.SQUARE_LENGTH)) / 2;

    // private instance members
    private MonsterType mMonsterType;
    private ArrayList<Point> mShape;
    private int mHitsToDestroy;
    private int mSpeed;
    private Direction mDirectionToMove;
    private PowerUp mPowerUpInside;

    // constructor
    public Monster(Square startingSquare, MonsterType monsterType) {

        // call super constructor & set team
        super();
        setTeam(Team.FOE);
        mMonsterType = monsterType;

        // set center, size, color, and random direction to move
        setShape();
        setCenter(startingSquare.getCenter());
        setSize(SIZE);
        setMonsterColor();
        setHitsToDestroy();
        setSpeed();
        setRandomDirection();

    }

    @Override
    public void move() {

        // define variables for where to move (square rol/col and sprite x/y)
        int iAdjustRow = 0;
        int iAdjustColumn = 0;
        double dAdjustX = 0;
        double dAdjustY = 0;


        // set the target location (square and spite)
        if (mDirectionToMove == Direction.DOWN) {
            iAdjustRow = 1;
            dAdjustY = mSpeed;
        } else if (mDirectionToMove == Direction.UP) {
            iAdjustRow = -1;
            dAdjustY = -mSpeed;
        } else if (mDirectionToMove == Direction.LEFT) {
            iAdjustColumn = -1;
            dAdjustX = -mSpeed;
        } else if (mDirectionToMove == Direction.RIGHT) {
            iAdjustColumn = 1;
            dAdjustX = mSpeed;
        }

        // find the square that object is targeting to move into
        int iNextRow = getCurrentSquare().getRow() + iAdjustRow;
        int iNextCol = getCurrentSquare().getColumn() + iAdjustColumn;
        Square targetSquare = CommandCenter.getInstance().getGameBoard().getSquare(iNextRow, iNextCol);

        // if the square is not blocked and does not have a foe, then move
        // otherwise set a new random direction
        // checking for past halfway allows object to move closer to progress in current square until it is in the middle
        if ((!targetSquare.isBlocked() && !targetSquare.hasFoe()) || !isPastSquareMidPoint() ) {
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

    private void setSpeed() {
        if (mMonsterType == MonsterType.SOLDIER) {
            mSpeed = 3;
        } else if (mMonsterType == MonsterType.ALIEN) {
            mSpeed = 6;
        }
    }

    private void setShape() {
        if (mMonsterType == MonsterType.SOLDIER) {
            mShape = getSoldierShape();
        } else if (mMonsterType == MonsterType.ALIEN) {
            mShape = getAlienShape();
        }

        assignPolarPoints(mShape);
    }

    private void setMonsterColor() {
        if (mMonsterType == MonsterType.SOLDIER) {
            setColor(Color.RED);
        } else if (mMonsterType == MonsterType.ALIEN) {
            setColor(Color.GREEN);
        }
    }


    private void setHitsToDestroy() {
        if (mMonsterType == MonsterType.SOLDIER) {
            mHitsToDestroy = 1;
        } else if (mMonsterType == MonsterType.ALIEN) {
            mHitsToDestroy = 3;
        }
    }

    public int getHitsToDestroy() {
        return mHitsToDestroy;
    }
    public boolean isDead() {
        return mHitsToDestroy == 0;
    }

    public void hitByBlast() {
        mHitsToDestroy--;
    }

    private ArrayList<Point> getSoldierShape() {

        // define list to store points
        ArrayList<Point> pntCs = new ArrayList<>();

        // add each point to outline shape
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

        // return the list
        return pntCs;
    }

    private ArrayList<Point> getAlienShape() {

        // define list to store points
        ArrayList<Point> pntCs = new ArrayList<>();

        // add each point to outline shape
        pntCs.add(new Point(5,15));
        pntCs.add(new Point(6,14));
        pntCs.add(new Point(7,13));
        pntCs.add(new Point(7,9));
        pntCs.add(new Point(8,8));
        pntCs.add(new Point(10,8));
        pntCs.add(new Point(11,15));
        pntCs.add(new Point(13,15));
        pntCs.add(new Point(14,13));
        pntCs.add(new Point(15,8));
        pntCs.add(new Point(15,-8));
        pntCs.add(new Point(14,-13));
        pntCs.add(new Point(13,-15));
        pntCs.add(new Point(11,-15));
        pntCs.add(new Point(10,-8));
        pntCs.add(new Point(8,-8));
        pntCs.add(new Point(7,-9));
        pntCs.add(new Point(7,-13));
        pntCs.add(new Point(6,-14));
        pntCs.add(new Point(5,-15));
        pntCs.add(new Point(-15,-15));
        pntCs.add(new Point(1,-14));
        pntCs.add(new Point(-6,-13));
        pntCs.add(new Point(1,-12));
        pntCs.add(new Point(-15,-11));
        pntCs.add(new Point(1,-10));
        pntCs.add(new Point(-6,-9));
        pntCs.add(new Point(1,-8));
        pntCs.add(new Point(-15,-7));
        pntCs.add(new Point(1,-6));
        pntCs.add(new Point(-6,-5));
        pntCs.add(new Point(1,-4));
        pntCs.add(new Point(-15,-3));
        pntCs.add(new Point(1,-2));
        pntCs.add(new Point(-6,-1));
        pntCs.add(new Point(-2,0));
        pntCs.add(new Point(-6,1));
        pntCs.add(new Point(1,2));
        pntCs.add(new Point(-15,3));
        pntCs.add(new Point(1,4));
        pntCs.add(new Point(-6,5));
        pntCs.add(new Point(1,6));
        pntCs.add(new Point(-15,7));
        pntCs.add(new Point(1,8));
        pntCs.add(new Point(-6,9));
        pntCs.add(new Point(1,10));
        pntCs.add(new Point(-15,11));
        pntCs.add(new Point(1,12));
        pntCs.add(new Point(-6,13));
        pntCs.add(new Point(1,14));
        pntCs.add(new Point(-15,15));
        pntCs.add(new Point(5,15));

        // return the list
        return pntCs;
    }

}