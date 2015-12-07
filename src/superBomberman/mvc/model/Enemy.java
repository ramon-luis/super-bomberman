package superBomberman.mvc.model;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;

/**
 * Enemy class used to define common traits of enemies
 * Allows game to efficiently process different types of enemy objects
 */

public abstract class Enemy extends Sprite {

    // ===============================================
    // FIELDS
    // ===============================================
    public final int SIZE = Square.SQUARE_LENGTH / 2;

    // private instance members
    private int mHitsToDestroy;
    private int mSpeed;
    private Direction mDirectionToMove;
    private PowerUp mPowerUpInside;

    private int mDirectionChangeCounter;
    private Square mPriorSquare;
    private Square mGoalSquare;
    private ArrayList<Square> mSeekPath;


    // ===============================================
    // CONSTRUCTOR
    // ===============================================
    public Enemy() {
        super();
        setTeam(Team.ENEMY);
        setSize(SIZE);
        mSeekPath = new ArrayList<>();
    }

    // ===============================================
    // METHODS
    // ===============================================

    @Override
    public void move() {

        // set random direction (if needed)
        if (mDirectionToMove == null)
            setRandomDirection();

        // check if there is more than 2 direction choices
        updateDirectionForMultiplePaths();

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
        if ((!targetSquare.isBlocked() && !targetSquare.hasEnemy()) || !isPastSquareMidPoint()) {
            if (isCenteredForMove()) {
                setDeltaX(dAdjustX);
                setDeltaY(dAdjustY);
                super.move();
                tickDirectionChangeCounter();
            } else {
                setCenter(getCurrentSquare().getCenter());
            }
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

    public void resetDirectionChangeCounter() {
        mDirectionChangeCounter = 0;
    }

    public boolean recentlyChangedDirection() {
        return mDirectionChangeCounter < 20;
    }

    public void tickDirectionChangeCounter() {
        mDirectionChangeCounter++;
    }

    // set the center of the enemy
    public void setCenterFromSquare(Square square) {
        setCenter(square.getCenter());
    }

    // set the speed of the enemy
    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    // get the speed of the enemy
    public int getSpeed() {
        return mSpeed;
    }

    // set the hits to destroy
    public void setHitsToDestroy(int hitsToDestry) {
        mHitsToDestroy = hitsToDestry;
    }

    // get the hits needed to destroy
    public int getHitsToDestroy() {
        return mHitsToDestroy;
    }

    // enemy hit -> lower the hits needed to destroy
    public void hitByBlast() {
        mHitsToDestroy--;
    }

    // check if enemy is dead (hits to destroy == 0)
    public boolean isDead() {
        return mHitsToDestroy == 0;
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

    // get random boolean choice
    public boolean getRandomChoice() {
        int iMin = 1;
        int iMax = 2;
        int iRandomChoice = ThreadLocalRandom.current().nextInt(iMin, iMax + 1);

        return iRandomChoice == 1;
    }

    public void updateDirectionForMultiplePaths() {
        if (isMultipleOpenPaths() && getRandomChoice() && !recentlyChangedDirection() && !isPastSquareMidPoint()) {
            setRandomDirection();
        }
    }

    public int getScore() {
        int iScore = 0;
        if (this instanceof Soldier) {
            iScore = 100;
        } else if (this instanceof Alien) {
            iScore = 250;
        } else if (this instanceof Drone) {
            iScore = 300;
        }
        return iScore;
    }

    // ===============================================
    // HELPER METHODS
    // ===============================================

    private boolean isCenteredForMove() {
        boolean bCentered = false;
        if (mDirectionToMove == Direction.DOWN || mDirectionToMove == Direction.UP) {
            bCentered = isInHorizontalCenterOfSquare();
        } else if (mDirectionToMove == Direction.LEFT || mDirectionToMove == Direction.RIGHT) {
            bCentered = isInVerticalCenterOfSquare();
        }
        return bCentered;
    }

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
        resetDirectionChangeCounter();
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
