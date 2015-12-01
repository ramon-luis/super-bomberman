package superBomberman.mvc.model;

import superBomberman.sounds.Sound;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * creates Bomb object
 * Bomb exists in single square (one per square) -> shared square with Bomberman at first
 * Bomb blocks movement of Monster and Bomberman (once moves away from square)
 * Bomb has limited life -> creates blasts at expiration
 */


public class Bomb extends Sprite {

    // constants for radius & life (expiration)
    private final int RADIUS = Square.SQUARE_LENGTH / 2;
    private final int EXPIRE = 30; // life of object before expiry

    // private instance members -> used to alternate sizes and colors
    private boolean mIsSmall;
    private boolean mIsRedWick;
    private boolean mIsExploded;

    // constructor
    public Bomb(Square square) {
        // call super constructor and set team
        super();
        setTeam(Team.BOMB);

        //define the points on a cartesian grid
        ArrayList<Point> pntCs = new ArrayList<>();
        pntCs.add(new Point(10, 0));
        pntCs.add(new Point(11, -1));
        pntCs.add(new Point(12, -2));
        pntCs.add(new Point(12, -3));
        pntCs.add(new Point(12, -4));
        pntCs.add(new Point(13, -5));
        pntCs.add(new Point(14, -6));
        pntCs.add(new Point(14, -7));
        pntCs.add(new Point(14, -8));
        pntCs.add(new Point(15, -9));
        pntCs.add(new Point(15, -10));
        pntCs.add(new Point(14, -10));
        pntCs.add(new Point(14, -9));
        pntCs.add(new Point(13, -8));
        pntCs.add(new Point(13, -7));
        pntCs.add(new Point(13, -6));
        pntCs.add(new Point(12, -5));
        pntCs.add(new Point(11, -4));
        pntCs.add(new Point(11, -3));
        pntCs.add(new Point(11, -2));
        pntCs.add(new Point(10, -1));
        pntCs.add(new Point(9, 0));

        // assign polar points from cartesian points
        assignPolarPoints(pntCs);

        // set expiration and starting radius
        setExpire(EXPIRE);
        setSize(RADIUS);

        // set center of object based on location of falcon
        setCenter(square.getCenter());
        getCurrentSquare().addBomb();
        CommandCenter.getInstance().getBomberman().useBomb();
    }

    @Override
    public void move() {
        super.move();

        // if expired, then explode
        if (getExpire() == 0) {
            explode();  // create blast in each direction & remove from queue
        } else {
            setExpire(getExpire() - 1);  // tick closer to expiration
        }
    }

    @Override
    public void draw(Graphics g) {
        // call super, update bomb size, update wick color
        super.draw(g);
        updateBombSize();
        updateWickColor();

        // set size of circle -> alternates between smaller and larger
        int iSize = (mIsSmall) ? RADIUS - 2 : RADIUS;

        // set colors for bomb and wick
        Color cBombFill = Color.DARK_GRAY;
        Color cWickFill = (mIsRedWick) ? Color.RED : Color.WHITE;

        // set coordinates to draw the circle of bomb
        int iDrawX = (int) getCenter().getX() - iSize + 10;
        int iDrawY = (int) getCenter().getY() - iSize + 10;

        // set color & draw circle for bomb
        g.setColor(cBombFill);
        g.fillOval(iDrawX, iDrawY, iSize * 2 - 15, iSize * 2 - 15);

        // set color & draw wick
        g.setColor(cWickFill);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

    // create blasts and remove bomb
    public void explode() {
        // check if bomb has already been exploded
        if (!mIsExploded) {
            // get map of squares that should contain blasts and their direction from current square
            Map<Square, Direction> blastSquares = getBlastSquares();

            // add a blast (with direction) for each square in map to the OpsList
            for (Square blastSquare : blastSquares.keySet()) {
                CommandCenter.getInstance().getOpsList().enqueue(
                        new Blast(blastSquare, blastSquares.get(blastSquare)), CollisionOp.Operation.ADD);
            }

            // remove from queue
            getCurrentSquare().removeBomb();
            Sound.playSound("blast.wav");
            CommandCenter.getInstance().getBomberman().addBombToUse();
            CommandCenter.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);

            // OpsList does not process fast enough -> need to set Bomb to be exploded to prevent multiple collisions
            mIsExploded = !mIsExploded;
        }
    }

    // ****************
    //  HELPER METHODS
    // ****************

    // alternate the size of the bomb
    private void updateBombSize() {
        if (getExpire() % 5 == 0)
            mIsSmall = !mIsSmall;
    }

    // alternate the color of the bomb wick
    private void updateWickColor() {
        if (getExpire() % 5 == 0)
                mIsRedWick = !mIsRedWick;
    }

    // returns a map of squares and directions used to create new blasts from the bomb
    private Map<Square, Direction> getBlastSquares() {
        // set power for blasts -> based on Bomberman attribute
        int iBlastPower = CommandCenter.getInstance().getBomberman().getBlastPower();

        // create map to return & add current square
        Map blastSquares = new HashMap<>();
        blastSquares.put(getCurrentSquare(), null);

        // loop through each direction
        for (Direction direction : Direction.values()) {
            // within each direction: loop until blast size matches blast power
            for (int iSquareOffset = 1; iSquareOffset <= iBlastPower; iSquareOffset++) {
                // get a blast square
                Square blastSquare = getBlastSquare(iSquareOffset, direction);
                // if blast square is not a solid wall, then add to map & continue
                if (!blastSquare.isSolidWall()) {
                    blastSquares.put(blastSquare, direction);
                } else {
                    break;  // else move onto different direction (blast stopped by solid wall)
                }
            }
        }

        // return the map of squares and directions
        return blastSquares;
    }

    // returns a single square -> used to place a Blast
    private Square getBlastSquare(int offSet, Direction direction) {
        // get the row and col for current square (i.e. bomb location)
        int iRow = getCurrentSquare().getRow();
        int iCol = getCurrentSquare().getColumn();

        // variables to define what square will have the blast
        int iColAdjust = 0;
        int iRowAdjust = 0;

        // adjust the row & col based on the direction
        if (direction == Direction.LEFT) {
            iColAdjust = -offSet;
        } else if (direction == Direction.RIGHT) {
            iColAdjust = offSet;
        } else if (direction == Direction.DOWN) {
            iRowAdjust = -offSet;
        } else if (direction == Direction.UP) {
            iRowAdjust = offSet;
        }

        // return the square
        return CommandCenter.getInstance().getGameBoard().getSquare(iRow + iRowAdjust, iCol + iColAdjust);
    }

}