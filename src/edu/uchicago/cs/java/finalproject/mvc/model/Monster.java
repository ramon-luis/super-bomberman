package edu.uchicago.cs.java.finalproject.mvc.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by RAM0N on 11/28/15.
 */
public class Monster extends Sprite {

    public enum Direction {UP, DOWN, LEFT, RIGHT};
    private Direction mDirectionToMove;
    private int mRow;
    private int mColumn;
    private Square mCurrentSquare;

    private final int THRUST = 5;
    private final int RADIUS = (int) Math.sqrt(2 * (Square.SQUARE_LENGTH * Square.SQUARE_LENGTH)) / 2;

        public Monster(Square startingSquare) {

            super();
            setTeam(Team.FOE);

            ArrayList<Point> pntCs = new ArrayList<Point>();

            pntCs.add(new Point(-7, -7));
            pntCs.add(new Point(-6, -7));
            pntCs.add(new Point(-6, -6));
            pntCs.add(new Point(-5, -6));
            pntCs.add(new Point(-5, -5));
            pntCs.add(new Point(-4, -5));
            pntCs.add(new Point(-4, -6));
            pntCs.add(new Point(-3, -6));
            pntCs.add(new Point(-3, -7));
            pntCs.add(new Point(-1, -7));
            pntCs.add(new Point(-1, -5));
            pntCs.add(new Point(1, -5));
            pntCs.add(new Point(1, -7));
            pntCs.add(new Point(3, -7));
            pntCs.add(new Point(3, -6));
            pntCs.add(new Point(4, -6));
            pntCs.add(new Point(4, -5));
            pntCs.add(new Point(5, -5));
            pntCs.add(new Point(5, -6));
            pntCs.add(new Point(6, -6));
            pntCs.add(new Point(6, -7));
            pntCs.add(new Point(7, -7));
            pntCs.add(new Point(7, 2));
            pntCs.add(new Point(6, 2));
            pntCs.add(new Point(6, 4));
            pntCs.add(new Point(5, 4));
            pntCs.add(new Point(5, 5));
            pntCs.add(new Point(4, 5));
            pntCs.add(new Point(4, 6));
            pntCs.add(new Point(2, 6));
            pntCs.add(new Point(2, 7));
            pntCs.add(new Point(-2, 7));
            pntCs.add(new Point(-2, 6));
            pntCs.add(new Point(-4, 6));
            pntCs.add(new Point(-4, 5));
            pntCs.add(new Point(-5, 5));
            pntCs.add(new Point(-5, 4));
            pntCs.add(new Point(-6, 4));
            pntCs.add(new Point(-6, 2));
            pntCs.add(new Point(-7, 2));

            assignPolarPoints(pntCs);

            setColor(Color.RED);
            setCenter(startingSquare.getCenter());
            setRadius(RADIUS);
            setRandomDirection();

        }

    public void move() {

            int iAdjustRow = 0;
            int iAdjustColumn = 0;
            double dAdjustX = 0;
            double dAdjustY = 0;



            if (mDirectionToMove == Direction.DOWN) {
                iAdjustRow = 1;
                dAdjustY = THRUST;
            } else if (mDirectionToMove == Direction.UP) {
                iAdjustRow = -1;
                dAdjustY = -THRUST;
            } else if (mDirectionToMove == Direction.LEFT) {
                iAdjustColumn = -1;
                dAdjustX = -THRUST;
            } else if (mDirectionToMove == Direction.RIGHT) {
                iAdjustColumn = 1;
                dAdjustX = THRUST;
            }

            int iNextRow = getCurrentSquare().getRow() + iAdjustRow;
            int iNextCol = getCurrentSquare().getColumn() + iAdjustColumn;

            Square targetSquare = CommandCenter.getInstance().getGameBoard().getSquare(iNextRow, iNextCol);

            if (!targetSquare.isBlocked() || !isPastSquareMidPoint()) {
                setDeltaX(dAdjustX);
                setDeltaY(dAdjustY);
                super.move();
            }
			else {
				setCenter(getCurrentSquare().getCenter());
                setRandomDirection();
			}
        }

    private void setRandomDirection() {
        int iMin = 1;
        int iMax = 4;
        int iRandomChoice = ThreadLocalRandom.current().nextInt(iMin, iMax + 1);

        Direction randomDirection = Direction.UP;
        if (iRandomChoice == 1)
            randomDirection = Direction.RIGHT;
        else if (iRandomChoice == 2)
            randomDirection = Direction.DOWN;
        else if (iRandomChoice == 3)
            randomDirection = Direction.LEFT;

        setDirectionToMove(randomDirection);
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


    public void draw(Graphics g)
        {
            super.draw(g);
            g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        }



    }





