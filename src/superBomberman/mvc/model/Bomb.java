package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * creates Bomb object
 */


public class Bomb extends Sprite {

	private final int RADIUS = Square.SQUARE_LENGTH / 2;

	private final int EXPIRE = 20; // life of object before expiry
	private boolean mIsSmall;
	private boolean mIsRedWick;
	private boolean mIsDetached;

public Bomb(Bomberman fal){

		// call super constructor and set team
		super();
	    setTeam(Team.BOMB);
		
		//define the points on a cartesian grid
		ArrayList<Point> pntCs = new ArrayList<>();

	pntCs.add(new Point(10,0));
	pntCs.add(new Point(11,-1));
	pntCs.add(new Point(12,-2));
	pntCs.add(new Point(12,-3));
	pntCs.add(new Point(12,-4));
	pntCs.add(new Point(13,-5));
	pntCs.add(new Point(14,-6));
	pntCs.add(new Point(14,-7));
	pntCs.add(new Point(14,-8));
	pntCs.add(new Point(15,-9));
	pntCs.add(new Point(15,-10));
	pntCs.add(new Point(14,-10));
	pntCs.add(new Point(14,-9));
	pntCs.add(new Point(13,-8));
	pntCs.add(new Point(13,-7));
	pntCs.add(new Point(13,-6));
	pntCs.add(new Point(12,-5));
	pntCs.add(new Point(11,-4));
	pntCs.add(new Point(11,-3));
	pntCs.add(new Point(11,-2));
	pntCs.add(new Point(10,-1));
	pntCs.add(new Point(9,0));

		// assign polar points from cartesian points
		assignPolarPoints(pntCs);

		// set expiration and starting radius
	    setExpire(EXPIRE);
	    setRadius(RADIUS);
	    
		// set center of object based on location of falcon
	    setCenter(fal.getCurrentSquare().getCenter());
		getCurrentSquare().addBomb();
		CommandCenter.getInstance().getBomberman().useBomb();

	}

	public void move(){
		// call super method move
		super.move();

		// if expired, then remove
		// if first half of life -> grow radius
		// if second half of life -> shrink radius
		if (getExpire() == 0) {

			// create blast at center, and in each direction
			int iRow = getCurrentSquare().getRow();
			int iCol = getCurrentSquare().getColumn();

			Square squareCenter = getCurrentSquare();
			Square squareLeft = CommandCenter.getInstance().getGameBoard().getSquare(iRow, iCol - 1);
			Square squareRight = CommandCenter.getInstance().getGameBoard().getSquare(iRow, iCol + 1);
			Square squareUp = CommandCenter.getInstance().getGameBoard().getSquare(iRow + 1, iCol);
			Square squareDown = CommandCenter.getInstance().getGameBoard().getSquare(iRow - 1, iCol);

			Square[] blastSquares = {squareCenter, squareLeft, squareRight, squareUp, squareDown};

			for (Square square : blastSquares) {
				if (!square.isSolidWall())
					CommandCenter.getInstance().getOpsList().enqueue(new Blast(square), CollisionOp.Operation.ADD);

			}

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
		super.draw(g);

		int iSize = (mIsSmall) ? RADIUS - 2 : RADIUS;


		Color cBombFill = Color.WHITE;
		Color cWickFill = (mIsRedWick) ? Color.RED : Color.WHITE;

		int iDrawX = (int) getCenter().getX() - iSize + 10;
		int iDrawY = (int) getCenter().getY() - iSize + 10;

		g.setColor(cBombFill);
		g.fillOval(iDrawX, iDrawY, iSize * 2 - 15, iSize * 2 - 15);

		g.setColor(cWickFill);

		g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);

		if (getExpire() % 10 == 0) {
			if (mIsSmall)
				mIsSmall = false;
			else
				mIsSmall = true;
		}
		if (mIsRedWick)
			mIsRedWick = false;
		else
			mIsRedWick = true;


	}




}
