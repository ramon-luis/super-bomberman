package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;


public class Bomberman extends Sprite {

    // ==============================================================
    // FIELDS
    // ==============================================================

    private final int SPEED = 10;
    private final int RADIUS = Square.SQUARE_LENGTH / 2 + 5;
    private Color mColor;
    private Direction mDirectionToMove;
    private int mBombCount;
    private int mBlastPower;
    private boolean mHasKickAbility;
    private int mProtectedCounter;
    private boolean bProtected;
    private boolean bMoving = false;
    private int nShield;

    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================

    public Bomberman() {
        super();
        setTeam(Team.FRIEND);
        mBombCount = 1;
        mBlastPower = 1;

        setShape(getShapeAsCartesianPoints());
        setColor(Color.white);
        setSize(RADIUS);
        setProtected(true);
        setFadeValue(0);
    }


    // ==============================================================
    // METHODS
    // ==============================================================

    public void move() {

        // flash if protected
        if (getProtectedCounter() == 0) {
            setProtected(false);
        }
        if (getProtected()) {
            if (mColor == Color.WHITE)
                mColor = Color.YELLOW;
            else
                mColor = Color.WHITE;
            setProtectedCounter(getProtectedCounter() - 1);
        } else
            mColor = Color.WHITE;

        // move if direction arrow being pressed
        if (bMoving) {

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
                if (isCenteredForMove()) {
                    setDeltaX(dAdjustX);
                    setDeltaY(dAdjustY);
                    super.move();
                } else {
                    setCenter(getCurrentSquare().getCenter());
                }
            }
        }
    }

    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(mColor);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

    private boolean isCenteredForMove() {
        boolean bCentered = false;
        if (mDirectionToMove == Direction.DOWN || mDirectionToMove == Direction.UP) {
            bCentered = isInHorizontalCenterOfSquare();
        } else if (mDirectionToMove == Direction.LEFT || mDirectionToMove == Direction.RIGHT) {
            bCentered = isInVerticalCenterOfSquare();
        }
        return bCentered;
    }

    public void setProtectedCounter(int number) {
        mProtectedCounter = number;
    }

    public int getProtectedCounter() {
        return mProtectedCounter;
    }

    public void kickBomb() {
        if (mHasKickAbility) {
            getBombToKick().isKicked(mDirectionToMove);
            BodyBlast bodyBlast = new BodyBlast();
            bodyBlast.setColor(Color.white);
            bodyBlast.setCenter(getCenter());
            bodyBlast.setExpire(6);
            CommandCenter.getInstance().getOpsList().enqueue(bodyBlast, CollisionOp.Operation.ADD);
        }
    }

    public boolean hasKickAbility() {
        return mHasKickAbility;
    }

    public void addKickAbility() {
        mHasKickAbility = true;
    }

    public boolean isFacingBomb() {
        return inSameSquareAsBomb() || nextToBomb();
    }

    public boolean inSameSquareAsBomb() {
        return getCurrentSquare().containsBomb();
    }

    public boolean nextToBomb() {

        boolean bNearCenterOfSquare = false;
        boolean bNextSquareHasBomb = false;

        if (mDirectionToMove == Direction.DOWN) {
            bNearCenterOfSquare = isInVerticalCenterOfSquare();
            bNextSquareHasBomb = getCurrentSquare().getNextSquareDown().containsBomb();
        } else if (mDirectionToMove == Direction.UP) {
            bNearCenterOfSquare = isInVerticalCenterOfSquare();
            bNextSquareHasBomb = getCurrentSquare().getNextSquareUp().containsBomb();
        } else if (mDirectionToMove == Direction.LEFT) {
            bNearCenterOfSquare = isInHorizontalCenterOfSquare();
            bNextSquareHasBomb = getCurrentSquare().getNextSquareLeft().containsBomb();
        } else if (mDirectionToMove == Direction.RIGHT) {
            bNearCenterOfSquare = isInHorizontalCenterOfSquare();
            bNextSquareHasBomb = getCurrentSquare().getNextSquareRight().containsBomb();
        }

        return bNearCenterOfSquare && bNextSquareHasBomb;
    }

    public Bomb getBombToKick() {
        Bomb bombToKick = null;
        if (inSameSquareAsBomb()) {
            bombToKick = getCurrentSquare().getBombInside();
        } else {
            Square bombSquare = null;
            if (mDirectionToMove == Direction.DOWN) {
                bombSquare = getCurrentSquare().getNextSquareDown();
            } else if (mDirectionToMove == Direction.UP) {
                bombSquare = getCurrentSquare().getNextSquareUp();
            } else if (mDirectionToMove == Direction.LEFT) {
                bombSquare = getCurrentSquare().getNextSquareLeft();
            } else if (mDirectionToMove == Direction.RIGHT) {
                bombSquare = getCurrentSquare().getNextSquareRight();
            }

            for (Movable movBomb : CommandCenter.getInstance().getMovBombs()) {
                if (movBomb.getCurrentSquare().equals(bombSquare)) {
                    bombToKick = (Bomb) movBomb;
                    break;
                }
            }
        }

        return bombToKick;
    }

    public int getBombCount() {
        return mBombCount;
    }

    public void useBomb() {
        mBombCount--;
    }

    public boolean hasBombToUse() {
        return mBombCount > 0;
    }

    public void addBombToUse() {
        mBombCount++;
    }

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

    public void setDirectionToMove(Direction direction) {
        mDirectionToMove = direction;
    }

    public void moveOn() {
        bMoving = true;
    }

    public void moveOff() {
        bMoving = false;
    }

    public int getBlastPower() {
        return mBlastPower;
    }

    public void increaseBlastPower() {
        mBlastPower++;
    }

    public void setProtected(boolean bParam) {
        if (bParam) {
            setProtectedCounter(100);
        }
        bProtected = bParam;
    }

    public boolean getProtected() {
        return bProtected;
    }

    // get the shape of the object
    private ArrayList<Point> getShapeAsCartesianPoints() {

        // define list to store points
        ArrayList<Point> pntCs = new ArrayList<>();

        // add each point to outline shape
        pntCs.add(new Point(1, 6));
        pntCs.add(new Point(2, 6));
        pntCs.add(new Point(2, 5));
        pntCs.add(new Point(4, 5));
        pntCs.add(new Point(4, 6));
        pntCs.add(new Point(5, 5));
        pntCs.add(new Point(6, 4));
        pntCs.add(new Point(6, -4));
        pntCs.add(new Point(5, -5));
        pntCs.add(new Point(4, -6));
        pntCs.add(new Point(4, -5));
        pntCs.add(new Point(2, -5));
        pntCs.add(new Point(2, -6));
        pntCs.add(new Point(1, -6));
        pntCs.add(new Point(0, -2));
        pntCs.add(new Point(-1, -5));
        pntCs.add(new Point(-2, -6));
        pntCs.add(new Point(-4, -6));
        pntCs.add(new Point(-4, -5));
        pntCs.add(new Point(-3, -5));
        pntCs.add(new Point(-3, -4));
        pntCs.add(new Point(-4, -3));
        pntCs.add(new Point(-5, -4));
        pntCs.add(new Point(-5, -5));
        pntCs.add(new Point(-6, -5));
        pntCs.add(new Point(-6, -2));
        pntCs.add(new Point(-5, -2));
        pntCs.add(new Point(-5, 2));
        pntCs.add(new Point(-6, 2));
        pntCs.add(new Point(-6, 5));
        pntCs.add(new Point(-5, 5));
        pntCs.add(new Point(-5, 4));
        pntCs.add(new Point(-4, 3));
        pntCs.add(new Point(-3, 4));
        pntCs.add(new Point(-3, 5));
        pntCs.add(new Point(-4, 5));
        pntCs.add(new Point(-4, 6));
        pntCs.add(new Point(-2, 6));
        pntCs.add(new Point(-1, 5));
        pntCs.add(new Point(0, 2));
        pntCs.add(new Point(1, 6));
        pntCs.add(new Point(2, 6));
        pntCs.add(new Point(2, 5));
        pntCs.add(new Point(4, 5));
        pntCs.add(new Point(4, -5));
        pntCs.add(new Point(2, -5));
        pntCs.add(new Point(2, -3));
        pntCs.add(new Point(3, -2));
        pntCs.add(new Point(3, 2));
        pntCs.add(new Point(2, 3));
        pntCs.add(new Point(2, 5));
        pntCs.add(new Point(2, 6));
        pntCs.add(new Point(1, 6));;

        // return the list
        return pntCs;
    }

}
