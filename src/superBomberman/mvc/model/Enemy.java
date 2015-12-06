package superBomberman.mvc.model;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;

/**
 * Enemy class used to define common traits of enemies
 * Allows game to efficiently process different types of enemy objects
 */

public abstract class Enemy extends Sprite {

    // enum for types of power ups
    public enum EnemyType {
        ALIEN, MONSTER, SOLDIER
    }

    ;

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


    // constructor
    public Enemy() {
        super();
        setTeam(Team.ENEMY);
        setSize(SIZE);
        mSeekPath = new ArrayList<>();
    }




    public boolean inNewSquare() {
        return getCurrentSquare() != mPriorSquare;
    }


    public void setPriorSquare(Square square) {
        mPriorSquare = square;
    }

    public Square getPriorSquare() {
        return mPriorSquare;
    }

    public void updateDirection(Direction direction) {
        resetDirectionChangeCounter();
        mDirectionToMove = direction;
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

    private boolean newDirectionPossible() {
        boolean bIsNewDirection = false;

        if (mPriorSquare == null)
            return false;

        if (inNewSquare()) {
            if (mDirectionToMove == Direction.RIGHT || mDirectionToMove == Direction.LEFT) {
                boolean bDownSquareIsNowOpen = !getCurrentSquare().getNextSquareDown().isWall() && mPriorSquare.getNextSquareDown().isWall();
                boolean bUpSquareIsNowOpen = !getCurrentSquare().getNextSquareUp().isWall() && mPriorSquare.getNextSquareUp().isWall();
                bIsNewDirection = bDownSquareIsNowOpen || bUpSquareIsNowOpen;
            } else if (mDirectionToMove == Direction.UP || mDirectionToMove == Direction.DOWN) {
                boolean bRightSquareIsNowOpen = !getCurrentSquare().getNextSquareRight().isWall() && mPriorSquare.getNextSquareRight().isWall();
                boolean bLeftSquareIsNowOpen = !getCurrentSquare().getNextSquareLeft().isWall() && mPriorSquare.getNextSquareLeft().isWall();
                bIsNewDirection = bRightSquareIsNowOpen || bLeftSquareIsNowOpen;
            }
        }
        System.out.println("new direction possible: " + bIsNewDirection);
        return bIsNewDirection;
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


    // refresh the gameBoard used for seek
    private void updateGoalSquare() {
        // get bomberman location -> needs to be updated whenever gameBoard is updated
        mGoalSquare = CommandCenter.getInstance().getBomberman().getCurrentSquare();
    }

    public void removeCurrentSquareFromSeekPath() {
        int iLastSquareIndex = mSeekPath.size() - 1;
        if (mSeekPath.get(iLastSquareIndex).equals(getCurrentSquare())) {
            mSeekPath.remove(iLastSquareIndex);
        }
    }

    public void setSeekDirection() {
        if (seek(getCurrentSquare(), mGoalSquare)) {

            System.out.println("seek path:");
            for (Square square : mSeekPath)
                System.out.println("  " + square);
            setDirectionForTargetSquare(getNextSeekSquare());
            System.out.println("direction to move: " + mDirectionToMove);
            if (inNewSquare()) {
                removeNextSeekSquare();
            }
        } else {
            setRandomDirection();
        }
    }

    public void updateSeekPath() {
        mSeekPath.clear();  // clear current seek path
        updateGoalSquare();
    }

    // get the direction between current square and an adjacent square
    private void setDirectionForTargetSquare(Square targetSquare) {
        int iTargetSquareRow = targetSquare.getRow();
        int iTargetSquareCol = targetSquare.getColumn();
        int iCurrentSquareRow = getCurrentSquare().getRow();
        int iCurrentSquareCol = getCurrentSquare().getColumn();

        if (iTargetSquareRow > iCurrentSquareRow && iTargetSquareCol == iCurrentSquareCol)
            mDirectionToMove = Direction.RIGHT;
        else if (iTargetSquareRow < iCurrentSquareRow && iTargetSquareCol == iCurrentSquareCol)
            mDirectionToMove = Direction.LEFT;
        else if (iTargetSquareRow == iCurrentSquareRow && iTargetSquareCol > iCurrentSquareCol)
            mDirectionToMove = Direction.DOWN;
        else if (iTargetSquareRow == iCurrentSquareRow && iTargetSquareCol < iCurrentSquareCol)
            mDirectionToMove = Direction.UP;
        else
            setRandomDirection();

    }


    private Square getNextSeekSquare() {
        removeCurrentSquareFromSeekPath();
        int iNextSeekSquareIndex = mSeekPath.size() - 1;  // list is stored in reverse order
        return mSeekPath.get(iNextSeekSquareIndex);
    }

    private void removeNextSeekSquare() {
        int iNextSeekSquareIndex = mSeekPath.size() - 1;  // list is stored in reverse order
        mSeekPath.remove(iNextSeekSquareIndex);
    }

    private boolean seek(Square currentSquare, Square goalSquare) {
        // unproductive path: wall or previously explored by this enemy
        if (currentSquare.isSolidWall() || currentSquare.isExplored(this)) {
            return false;

            // base case: goal found
        } else if (currentSquare.equals(goalSquare)) {

            mSeekPath.add(currentSquare);  // add square to seek path
            return true;

            // new square (not a wall or goal): explore square
        } else {

            currentSquare.setExplored(this);
            if (seek(currentSquare.getNextSquareDown(), goalSquare) || // left
                    seek(currentSquare.getNextSquareUp(), goalSquare) || // up
                    seek(currentSquare.getNextSquareLeft(), goalSquare) || // down
                    seek(currentSquare.getNextSquareRight(), goalSquare)) { // right
                mSeekPath.add(currentSquare);  // add square to seek path
                return true;    // location leads to goal square
            }
            // unchoose
        }
        return false;   // does not lead to goal square
    }

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

    @Override
    public void move() {

        // set random direction (if needed)
        if (mDirectionToMove == null)
            setRandomDirection();

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


    // ****************
    //  HELPER METHODS
    // ****************

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
