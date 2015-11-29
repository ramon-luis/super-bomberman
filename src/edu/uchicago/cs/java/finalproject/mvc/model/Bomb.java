package edu.uchicago.cs.java.finalproject.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * creates Bomb object
 */


public class Bomb extends Sprite {

	private final int RADIUS = Square.SQUARE_LENGTH / 2;

	private final int EXPIRE = 80; // life of object before expiry
	private boolean mIsRed;
	private boolean mIsDetached;

public Bomb(Falcon fal){

		// call super constructor and set team
		super();
	    setTeam(Team.BOMB);
		
		//define the points on a cartesian grid
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
	    
		// set center of object based on location of falcon
	    setCenter(fal.getCurrentSquare().getCenter());
		getCurrentSquare().addBomb();
		CommandCenter.getInstance().getFalcon().useBomb();

	}

	public void move(){
		// call super method move
		super.move();

		// if expired, then remove
		// if first half of life -> grow radius
		// if second half of life -> shrink radius
		if (getExpire() == 0) {

			// create blast at center, and in each direction
			int iAdjust = Square.SQUARE_LENGTH;
			Point pUp = new Point(getCenter().x, getCenter().y - iAdjust);
			Point pDown = new Point(getCenter().x, getCenter().y + iAdjust);
			Point pLeft = new Point(getCenter().x - iAdjust, getCenter().y);
			Point pRight = new Point(getCenter().x + iAdjust, getCenter().y);

			CommandCenter.getInstance().getOpsList().enqueue(new Blast(getCenter()), CollisionOp.Operation.ADD);
			CommandCenter.getInstance().getOpsList().enqueue(new Blast(pUp), CollisionOp.Operation.ADD);
			CommandCenter.getInstance().getOpsList().enqueue(new Blast(pDown), CollisionOp.Operation.ADD);
			CommandCenter.getInstance().getOpsList().enqueue(new Blast(pLeft), CollisionOp.Operation.ADD);
			CommandCenter.getInstance().getOpsList().enqueue(new Blast(pRight), CollisionOp.Operation.ADD);

			// remove from queue
			CommandCenter.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);

		} else {
//			if (getExpire() > EXPIRE / 2) {
//				setRadius(getRadius() + 3);
//			} else {
//				setRadius(getRadius() - 3);
//			}
			// move towards expiry
			setExpire(getExpire() - 1);
		}
	}

	@Override
	public void draw(Graphics g) {
		// set color to be random size & fill oval with random color
		Color cFill = (mIsRed) ? Color.RED : Color.WHITE;

		int iDrawX = (int) getCenter().getX() - Square.SQUARE_LENGTH / 2;
		int iDrawY = (int) getCenter().getY() - Square.SQUARE_LENGTH / 2;

		g.setColor(cFill);
		g.fillOval(iDrawX, iDrawY, getRadius() * 2, getRadius() * 2);

		if (mIsRed)
			flashWhite();
		else
			flashRed();

	}

	public void flashRed() {
		mIsRed = true;
	}

	public void flashWhite() {
		mIsRed = false;
	}


}
