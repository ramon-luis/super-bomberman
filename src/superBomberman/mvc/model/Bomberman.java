package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;


public class Bomberman extends Sprite {

    // ==============================================================
    // FIELDS
    // ==============================================================

    private Direction mDirectionToMove;

    private int mBombCount;
    private Color mColor;
    private int mProtectedCounter;

    private final int SPEED = 10;
    private final int RADIUS = Square.SQUARE_LENGTH / 2 + 5;

    private int mBlastPower;
    private boolean mHasKickAbility;

    private boolean bProtected; //for fade in and out

    private boolean bMoving = false;
    private boolean bTurningRight = false;
    private boolean bTurningLeft = false;

    private int nShield;

    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================


    public Bomberman() {
        super();
        setTeam(Team.FRIEND);
        mBombCount = 1;
        mBlastPower = 1;
        mDirectionToMove = Direction.UP;

        ArrayList<Point> pntCs = new ArrayList<Point>();

        // draw the bomberman
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
        pntCs.add(new Point(1, 6));

        // assign to polar points
        assignPolarPoints(pntCs);

        setColor(Color.white);
        setSize(RADIUS);
        setProtected(true);
        setFadeValue(0);
    }


    // ==============================================================
    // METHODS
    // ==============================================================

    public void move() {

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


        //implementing the fadeInOut functionality - added by Dmitriy
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

    } //end move


    public boolean isCenteredForMove() {
        boolean bCentered = false;
        if (mDirectionToMove == Direction.DOWN || mDirectionToMove == Direction.UP) {
            bCentered = isInHorizontalCenterOfSquare();
        } else if (mDirectionToMove == Direction.LEFT || mDirectionToMove == Direction.RIGHT) {
            bCentered = isInVerticalCenterOfSquare();
        }
        return bCentered;
    }

    public boolean isInRangeForMove() {
        boolean bInRange = false;
        if (mDirectionToMove == Direction.DOWN || mDirectionToMove == Direction.UP) {
            bInRange = isInHorizontalRange();
        } else if (mDirectionToMove == Direction.LEFT || mDirectionToMove == Direction.RIGHT) {
            bInRange = isInVerticalRange();
        }
        return bInRange;
    }


    public void setProtectedCounter(int number) {
        mProtectedCounter = number;
    }

    public int getProtectedCounter() {
        return mProtectedCounter;
    }

    public void finishMove() {
//		if (mDirectionToMove == Direction.DOWN || mDirectionToMove == Direction.UP) {
//			setCenter(new Point((int) getCurrentSquare().getCenter().getX(), (int) getCenter().getY()));
//		} else if (mDirectionToMove == Direction.LEFT || mDirectionToMove == Direction.RIGHT) {
//			setCenter(new Point((int) getCenter().getX(), (int) getCurrentSquare().getCenter().getY()));
//		}
//		System.out.println("center updated");
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

    public boolean nearBomb() {
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

    private int adjustColor(int nCol, int nAdj) {
        if (nCol - nAdj <= 0) {
            return 0;
        } else {
            return nCol - nAdj;
        }
    }

    public void draw(Graphics g) {


//		//shield on
//		if (bShield && nShield > 0) {
//
//			setShield(getShield() - 1);
//
//			g.setColor(Color.cyan);
//			g.drawOval(getCenter().x - getSize(),
//					getCenter().y - getSize(), getSize() * 2,
//					getSize() * 2);
//
//		} //end if shield


        super.draw(g);
        g.setColor(mColor);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);

    } //end draw()


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

    public void setShield(int n) {
        nShield = n;
    }

    public int getShield() {
        return nShield;
    }


} //end class
