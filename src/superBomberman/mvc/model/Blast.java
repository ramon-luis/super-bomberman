package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by RAM0N on 11/20/15.
 */
public class Blast extends Sprite {


    private static final int RADIUS = (int) Math.sqrt(2 * (Square.SQUARE_LENGTH * Square.SQUARE_LENGTH)) / 2;
    private static final int EXPIRE = 20;
    private boolean mIsRed;


    public Blast(Square square) {

        // call super constructor and set team
        super();
        setTeam(Team.BLAST);

        //define the points on cartesian grid
        ArrayList<Point> pntCs = new ArrayList<>();

        pntCs.add(new Point(-10,10));
        pntCs.add(new Point(-9,7));
        pntCs.add(new Point(-8,8));
        pntCs.add(new Point(-7,7));
        pntCs.add(new Point(-6,10));
        pntCs.add(new Point(-5,7));
        pntCs.add(new Point(-4,8));
        pntCs.add(new Point(-3,7));
        pntCs.add(new Point(-2,10));
        pntCs.add(new Point(-1,7));
        pntCs.add(new Point(0,8));
        pntCs.add(new Point(1,7));
        pntCs.add(new Point(2,10));
        pntCs.add(new Point(3,7));
        pntCs.add(new Point(4,8));
        pntCs.add(new Point(5,7));
        pntCs.add(new Point(6,10));
        pntCs.add(new Point(7,7));
        pntCs.add(new Point(8,8));
        pntCs.add(new Point(9,7));
        pntCs.add(new Point(10,10));

        pntCs.add(new Point(10,10));
        pntCs.add(new Point(9,9));
        pntCs.add(new Point(10,8));
        pntCs.add(new Point(9,7));
        pntCs.add(new Point(10,6));
        pntCs.add(new Point(9,5));
        pntCs.add(new Point(10,4));
        pntCs.add(new Point(9,3));
        pntCs.add(new Point(10,2));
        pntCs.add(new Point(9,1));
        pntCs.add(new Point(10,0));
        pntCs.add(new Point(9,-1));
        pntCs.add(new Point(10,-2));
        pntCs.add(new Point(9,-3));
        pntCs.add(new Point(10,-4));
        pntCs.add(new Point(9,-5));
        pntCs.add(new Point(10,-6));
        pntCs.add(new Point(9,-7));
        pntCs.add(new Point(10,-8));
        pntCs.add(new Point(9,-9));
        pntCs.add(new Point(10,-10));

        pntCs.add(new Point(10,-10));
        pntCs.add(new Point(9,-9));
        pntCs.add(new Point(8,-10));
        pntCs.add(new Point(7,-9));
        pntCs.add(new Point(6,-10));
        pntCs.add(new Point(5,-9));
        pntCs.add(new Point(4,-10));
        pntCs.add(new Point(3,-9));
        pntCs.add(new Point(2,-10));
        pntCs.add(new Point(1,-9));
        pntCs.add(new Point(0,-10));
        pntCs.add(new Point(-1,-9));
        pntCs.add(new Point(-2,-10));
        pntCs.add(new Point(-3,-9));
        pntCs.add(new Point(-4,-10));
        pntCs.add(new Point(-5,-9));
        pntCs.add(new Point(-6,-10));
        pntCs.add(new Point(-7,-9));
        pntCs.add(new Point(-8,-10));
        pntCs.add(new Point(-9,-9));
        pntCs.add(new Point(-10,-10));

        pntCs.add(new Point(-10,-10));
        pntCs.add(new Point(-9,-9));
        pntCs.add(new Point(-10,-8));
        pntCs.add(new Point(-9,-7));
        pntCs.add(new Point(-10,-6));
        pntCs.add(new Point(-9,-5));
        pntCs.add(new Point(-10,-4));
        pntCs.add(new Point(-9,-3));
        pntCs.add(new Point(-10,-2));
        pntCs.add(new Point(-9,-1));
        pntCs.add(new Point(-10,0));
        pntCs.add(new Point(-9,1));
        pntCs.add(new Point(-10,2));
        pntCs.add(new Point(-9,3));
        pntCs.add(new Point(-10,4));
        pntCs.add(new Point(-9,5));
        pntCs.add(new Point(-10,6));
        pntCs.add(new Point(-9,7));
        pntCs.add(new Point(-10,8));
        pntCs.add(new Point(-9,9));
        pntCs.add(new Point(-10,10));

        // assign polar points from cartesian points
        assignPolarPoints(pntCs);

        // set expiration and starting radius
        setExpire(EXPIRE);
        setRadius(RADIUS);

        // set center of object based on location of bomb
        setCenter(square.getCenter());

        CommandCenter.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.ADD);

    }


    public void move(){
        // call super method move
        super.move();

        // if expired, then remove
        // if first half of life -> grow radius
        // if second half of life -> shrink radius
        if (getExpire() == 0)
            CommandCenter.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
        else {


			if (getExpire() > EXPIRE / 2 && getRadius() < Square.SQUARE_LENGTH / 2) {
				setRadius(getRadius() + 1);
			} else {
				setRadius(getRadius() - 1);
			}
            // move towards expiry
            setExpire(getExpire() - 1);
        }
    }

    @Override
    public void draw(Graphics g) {
        //super.draw(g);


        // set color to be random size & fill oval with random color
        Color cFill = (mIsRed) ? Color.RED : Color.ORANGE;

        int iDrawX = (int) getCenter().getX() - RADIUS;
        int iDrawY = (int) getCenter().getY() - RADIUS;

        g.setColor(cFill);
//        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        g.fillOval(iDrawX, iDrawY, RADIUS, RADIUS);

        if (getExpire() % 4 == 0) {
            if (mIsRed)
                flashOrange();
            else
                flashRed();
        }

    }

    public void flashRed() {
        mIsRed = true;
    }

    public void flashOrange() {
        mIsRed = false;
    }



}
