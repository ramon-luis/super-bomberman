package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by RAM0N on 11/20/15.
 */
public class Blast extends Sprite {


    private static final int RADIUS = Square.SQUARE_LENGTH / 4;
    private static final int EXPIRE = 10;
    private boolean mIsRed;


    public Blast(Point point) {

        // call super constructor and set team
        super();
        setTeam(Team.BLAST);

        //define the points on cartesian grid
        ArrayList<Point> pntCs = new ArrayList<>();

        pntCs.add(new Point(0,1)); //top point
        pntCs.add(new Point(1,0));
        pntCs.add(new Point(0,-1));
        pntCs.add(new Point(-1,0));

        // assign polar points from cartesian points
        assignPolarPoints(pntCs);

        // set expiration and starting radius
        setExpire(EXPIRE);
        setRadius(RADIUS);

        // set center of object based on location of bomb
        setCenter(point);

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
        // set color to be random size & fill oval with random color
        Color cFill = (mIsRed) ? Color.RED : Color.ORANGE;

        int iDrawX = (int) getCenter().getX() - RADIUS;
        int iDrawY = (int) getCenter().getY() - RADIUS;

        g.setColor(cFill);
        g.fillOval(iDrawX, iDrawY, getRadius() * 2, getRadius() * 2);

        if (mIsRed)
            flashOrange();
        else
            flashRed();

    }

    public void flashRed() {
        mIsRed = true;
    }

    public void flashOrange() {
        mIsRed = false;
    }



}
