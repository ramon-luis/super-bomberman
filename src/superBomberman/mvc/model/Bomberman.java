package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;


public class Bomberman extends Sprite {

	// ==============================================================
	// FIELDS 
	// ==============================================================

	public enum Direction {UP, DOWN, LEFT, RIGHT};
	private Direction mDirectionToMove;
	private int mRow;
	private int mColumn;
	private Square mCurrentSquare;

	private int mBombCount;

	private final int THRUST = 10;
	private final int RADIUS = (int) Math.sqrt(2 * (Square.SQUARE_LENGTH * Square.SQUARE_LENGTH)) / 2;
	private final int MOVES_PER_SQUARE = Square.SQUARE_LENGTH / THRUST;

	final int DEGREE_STEP = 7;
	
	private boolean bShield = false;
	private boolean bFlame = false;
	private boolean bProtected; //for fade in and out
	
	private boolean bThrusting = false;
	private boolean bTurningRight = false;
	private boolean bTurningLeft = false;
	
	private int nShield;
			
	private final double[] FLAME = { 23 * Math.PI / 24 + Math.PI / 2,
			Math.PI + Math.PI / 2, 25 * Math.PI / 24 + Math.PI / 2 };

	private int[] nXFlames = new int[FLAME.length];
	private int[] nYFlames = new int[FLAME.length];

	private Point[] pntFlames = new Point[FLAME.length];

	
	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public Bomberman() {
		super();
		setTeam(Team.FRIEND);
		mBombCount = 1;

		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		// draw the bomberman
		pntCs.add(new Point(0,5));
		pntCs.add(new Point(1,6));
		pntCs.add(new Point(2,6));
		pntCs.add(new Point(2,5));
		pntCs.add(new Point(4,5));
		pntCs.add(new Point(4,6));
		pntCs.add(new Point(5,5));
		pntCs.add(new Point(6,4));
		pntCs.add(new Point(6,-4));
		pntCs.add(new Point(5,-5));
		pntCs.add(new Point(4,-6));
		pntCs.add(new Point(4,-5));
		pntCs.add(new Point(2,-5));
		pntCs.add(new Point(2,-6));
		pntCs.add(new Point(1,-6));
		pntCs.add(new Point(0,-2));

		pntCs.add(new Point(-1,-5));
		pntCs.add(new Point(-2,-6));
		pntCs.add(new Point(-4,-6));
		pntCs.add(new Point(-4,-5));
		pntCs.add(new Point(-3,-5));

		pntCs.add(new Point(-3,-4));
		pntCs.add(new Point(-4,-3));
		pntCs.add(new Point(-5,-4));
		pntCs.add(new Point(-5,-5));
		pntCs.add(new Point(-6,-5));
		pntCs.add(new Point(-6,-2));
		pntCs.add(new Point(-5,-2));
		pntCs.add(new Point(-5,2));

		pntCs.add(new Point(-6,2));
		pntCs.add(new Point(-6,5));
		pntCs.add(new Point(-5,5));
		pntCs.add(new Point(-5,4));
		pntCs.add(new Point(-4,3));
		pntCs.add(new Point(-3,4));
		pntCs.add(new Point(-3,5));
		pntCs.add(new Point(-4,5));
		pntCs.add(new Point(-4,6));
		pntCs.add(new Point(-2,6));
		pntCs.add(new Point(-1,5));
		pntCs.add(new Point(0,2));
		pntCs.add(new Point(1,6));


		pntCs.add(new Point(2,6));
		pntCs.add(new Point(2,5));
		pntCs.add(new Point(4,5));
		pntCs.add(new Point(4,-5));
		pntCs.add(new Point(2,-5));
		pntCs.add(new Point(2,-3));
		pntCs.add(new Point(3,-2));
		pntCs.add(new Point(3,2));
		pntCs.add(new Point(2,3));
		pntCs.add(new Point(2,5));

		// assign to polar points
		assignPolarPoints(pntCs);

		setColor(Color.white);

		// place in bottom upper left corner
		mRow = 1;
		mColumn = 1;
		mCurrentSquare = CommandCenter.getInstance().getGameBoard().getSquare(mRow, mColumn);
		setCenter(mCurrentSquare.getCenter());

		//put falcon in the middle.
		//setCenter(new Point(Game.DIM.width / 2, Game.DIM.height / 2));
		
		//with random orientation
		//setOrientation(Game.R.nextInt(360));

		// set orientation to straight up
		//setOrientation(270);

		//this is the size of the falcon
		setRadius(RADIUS);

		//these are falcon specific
		setProtected(true);
		setFadeValue(0);
	}
	
	
	// ==============================================================
	// METHODS 
	// ==============================================================

	public void move() {

		if (bThrusting) {
			bFlame = true;

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

			System.out.println("Current square: " + getCurrentSquare());

			System.out.println("Target square: " + targetSquare);

			if (!targetSquare.isBlocked() || !isPastSquareMidPoint()) {
				setDeltaX(dAdjustX);
				setDeltaY(dAdjustY);
				super.move();

			}
//			else {
//				setCenter(getCurrentSquare().getCenter());
//			}
		}


		//implementing the fadeInOut functionality - added by Dmitriy
		if (getProtected()) {
			setFadeValue(getFadeValue() + 3);
		}
		if (getFadeValue() == 255) {
			setProtected(false);
		}

	} //end move


	public void finishMove() {

		//setCenter(getCurrentSquare().getCenter());
//
//		int iAdjustRow = 0;
//		int iAdjustColumn = 0;
//
//		if (mDirectionToMove == Direction.DOWN ) {
//			iAdjustRow = 1;
//		} else if (mDirectionToMove == Direction.UP ) {
//			iAdjustRow = -1;
//		} else if (mDirectionToMove == Direction.LEFT ) {
//			iAdjustColumn = -1;
//		} else if (mDirectionToMove == Direction.RIGHT ) {
//			iAdjustColumn = 1;
//		}
//
//
//		int iNextRow = getCurrentSquare().getRow() + iAdjustRow;
//		int iNextCol = getCurrentSquare().getColumn() + iAdjustColumn;
//		Square finalSquare = CommandCenter.gameBoardSquares[iNextRow][iNextCol];
//		if (!finalSquare.isBlocked()) {
//			setCenter(finalSquare.getCenter());
//		}

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

	public void thrustOn() {
		bThrusting = true;
	}

	public void thrustOff() {
		bThrusting = false;
		bFlame = false;
	}

	private int adjustColor(int nCol, int nAdj) {
		if (nCol - nAdj <= 0) {
			return 0;
		} else {
			return nCol - nAdj;
		}
	}

	public void draw(Graphics g) {

		//does the fading at the beginning or after hyperspace
		Color colShip;
		if (getFadeValue() == 255) {
			colShip = Color.white;
		} else {
			colShip = new Color(adjustColor(getFadeValue(), 200), adjustColor(
					getFadeValue(), 175), getFadeValue());
		}

//		//shield on
//		if (bShield && nShield > 0) {
//
//			setShield(getShield() - 1);
//
//			g.setColor(Color.cyan);
//			g.drawOval(getCenter().x - getRadius(),
//					getCenter().y - getRadius(), getRadius() * 2,
//					getRadius() * 2);
//
//		} //end if shield

		//thrusting
		if (bFlame) {
			g.setColor(colShip);
			//the flame
			for (int nC = 0; nC < FLAME.length; nC++) {
				if (nC % 2 != 0) //odd
				{
					pntFlames[nC] = new Point((int) (getCenter().x + 2
							* getRadius()
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])), (int) (getCenter().y - 2
							* getRadius()
							* Math.cos(Math.toRadians(getOrientation())
									+ FLAME[nC])));

				} else //even
				{
					pntFlames[nC] = new Point((int) (getCenter().x + getRadius()
							* 1.1
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])),
							(int) (getCenter().y - getRadius()
									* 1.1
									* Math.cos(Math.toRadians(getOrientation())
											+ FLAME[nC])));

				} //end even/odd else

			} //end for loop

			for (int nC = 0; nC < FLAME.length; nC++) {
				nXFlames[nC] = pntFlames[nC].x;
				nYFlames[nC] = pntFlames[nC].y;

			} //end assign flame points

			//g.setColor( Color.white );
			g.fillPolygon(nXFlames, nYFlames, FLAME.length);

		} //end if flame

		drawShipWithColor(g, colShip);

	} //end draw()

	public void drawShipWithColor(Graphics g, Color col) {
		super.draw(g);
		g.setColor(col);
		g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
		//g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}


	public void setProtected(boolean bParam) {
		if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}

	public void setProtected(boolean bParam, int n) {
		if (bParam && n % 3 == 0) {
			setFadeValue(n);
		} else if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}	

	public boolean getProtected() {return bProtected;}
	public void setShield(int n) {nShield = n;}
	public int getShield() {return nShield;}	
	
} //end class
