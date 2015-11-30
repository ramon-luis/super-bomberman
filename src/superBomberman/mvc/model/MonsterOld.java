package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by RAM0N on 11/28/15.
 */
public class MonsterOld extends Sprite {

        public enum Direction {UP, DOWN, LEFT, RIGHT}
    
        public static final int RADIUS = Square.SQUARE_LENGTH / 2;


        private Point pGhostCenter;
        private Direction mDirection; // 0- left, 1- up, 2- right, 3-down
        private int nGhostSpeed = 3; //note to change this below in tunnel checker
        private Color ghostColor = Color.RED;


        //turn trigger booleans and variables
        private boolean bTurnsQueued;
        private boolean toTurnDown;
        private boolean toTurnLeft;
        private boolean toTurnUp;
        private boolean toTurnRight;
        private boolean bRecentTurn;
        private int nTurnTick;
        private int nXTurn;
        private int nYTurn;

        //mode change booleans
        private boolean bFirstScatter;
        private boolean bFirstChase;
        private boolean bFirstScared;
        private boolean bInsideBox;
        private boolean bRespawn;

        //scatter target square information
        //private final Point scatterTargetSquare = CommandCenter.gameBoardSquares[1][1].getCenter();

        public MonsterOld(Square square)
        {

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

            setColor(ghostColor);

            setCenter(square.getCenter());

            //set initial direction index (left)
            mDirection = Direction.LEFT;
            setOrientation(270);

            setRadius(RADIUS);


        }

        public void move()
        {

            int iAdjustRow = 0;
            int iAdjustColumn = 0;
            double dAdjustX = 0;
            double dAdjustY = 0;

            if (mDirection == Direction.DOWN) {
                iAdjustRow = 1;
                dAdjustY = nGhostSpeed;
            } else if (mDirection == Direction.UP) {
                iAdjustRow = -1;
                dAdjustY = -nGhostSpeed;
            } else if (mDirection == Direction.LEFT) {
                iAdjustColumn = -1;
                dAdjustX = -nGhostSpeed;
            } else if (mDirection == Direction.RIGHT) {
                iAdjustColumn = 1;
                dAdjustX = nGhostSpeed;

            }

            int iNextRow = getCurrentSquare().getRow() + iAdjustRow;
            int iNextCol = getCurrentSquare().getColumn() + iAdjustColumn;

            Square targetSquare = CommandCenter.getInstance().getGameBoard().getSquare(iNextRow,iNextCol);

            if (!targetSquare.isBlocked()) {
                setDeltaX(dAdjustX);
                setDeltaY(dAdjustY);
                super.move();

            }

                if (toTurnDown)
                {

                    if (mDirection == Direction.LEFT)
                    {
                        if (pGhostCenter.x <= nXTurn)
                        {
                            mDirection = Direction.DOWN;
                            // setCenter(new Point(nXTurn, pGhostCenter.y));
                            toTurnDown = false;
                            bTurnsQueued = false;
                            bRecentTurn = true;
                            this.move();
                        }
                    }
                    else if (mDirection == Direction.RIGHT)
                    {
                        if (pGhostCenter.x >= nXTurn)
                        {
                            mDirection = Direction.DOWN;
                            //setCenter(new Point(nXTurn, pGhostCenter.y));
                            toTurnDown = false;
                            bTurnsQueued = false;
                            bRecentTurn = true;
                            this.move();
                        }
                    }
                }

                if (toTurnUp)
                {

                    if (mDirection == Direction.LEFT)
                    {
                        if (pGhostCenter.x <= nXTurn)
                        {
                            mDirection = Direction.UP;
                            //setCenter(new Point(nXTurn, pGhostCenter.y));
                            toTurnUp = false;
                            bTurnsQueued = false;
                            bRecentTurn = true;
                            this.move();
                        }
                    }
                    else if (mDirection == Direction.RIGHT)
                    {
                        if (pGhostCenter.x >= nXTurn)
                        {
                            mDirection = Direction.UP;
                            //setCenter(new Point(nXTurn, pGhostCenter.y));
                            toTurnUp = false;
                            bTurnsQueued = false;
                            bRecentTurn = true;
                            this.move();
                        }
                    }
                }

                if (toTurnRight)
                {
                    if (mDirection == Direction.UP)
                    {
                        if (pGhostCenter.y <= nYTurn)
                        {
                            mDirection = Direction.RIGHT;
                            //setCenter(new Point(pGhostCenter.x, nYTurn));
                            toTurnRight = false;
                            bTurnsQueued = false;
                            bRecentTurn = true;
                            this.move();
                        }
                    }
                    else if (mDirection == Direction.DOWN)
                    {
                        if (pGhostCenter.y >= nYTurn)
                        {
                            mDirection = Direction.RIGHT;
                            //setCenter(new Point(pGhostCenter.x, nYTurn));
                            toTurnRight = false;
                            bTurnsQueued = false;
                            bRecentTurn = true;
                            this.move();
                        }
                    }
                }

                if (toTurnLeft)
                {
                    if (mDirection == Direction.UP)
                    {
                        if (pGhostCenter.y <= nYTurn)
                        {
                            mDirection = Direction.LEFT;
                            //setCenter(new Point(pGhostCenter.x, nYTurn));
                            toTurnLeft = false;
                            bTurnsQueued = false;
                            bRecentTurn = true;
                            this.move();
                        }
                    }
                    if (mDirection == Direction.DOWN)
                    {
                        if (pGhostCenter.y >= nYTurn)
                        {
                            mDirection = Direction.LEFT;
                            //setCenter(new Point(pGhostCenter.x, nYTurn));
                            toTurnLeft = false;
                            bTurnsQueued = false;
                            bRecentTurn = true;
                            this.move();
                        }
                    }
                }



            //chase();

        }

//        public void scatter()
//        {
//            nGhostSpeed = 3;
//            setColor(ghostColor);
//
//            //reverses ghost direction if the scatter call is not the initial scatter
//            if (bFirstScatter)
//            {
//                if (!bTurnsQueued && !bRecentTurn) //reverse direction if turn queue is not loaded
//                {
//                    mDirection = (mDirection + 2) % 4;
//                }
//
//            }
//            bFirstScatter = false;
//            bFirstChase = true;
//            bFirstScared = true;
//
//            Point scatterTarget = scatterTargetSquare;
//            TargetSpace targetSquare = new TargetSpace(scatterTarget.x, scatterTarget.y);
//            Point currPnt = getGhostSpaceCoord();
//
//            //target spaces for tests for next move
//            TargetSpace L = new TargetSpace(currPnt.x - 2, currPnt.y);
//            TargetSpace LU = new TargetSpace(currPnt.x - 1, currPnt.y - 1);
//            TargetSpace U = new TargetSpace(currPnt.x, currPnt.y - 2);
//            TargetSpace UR = new TargetSpace(currPnt.x + 1, currPnt.y - 1);
//            TargetSpace R = new TargetSpace(currPnt.x + 2, currPnt.y);
//            TargetSpace RD = new TargetSpace(currPnt.x + 1, currPnt.y + 1);
//            TargetSpace D = new TargetSpace(currPnt.x, currPnt.y + 2);
//            TargetSpace DL = new TargetSpace(currPnt.x - 1, currPnt.y + 1);
//
//            //Adjacent target spaces
//            TargetSpace AL = new TargetSpace(currPnt.x -1, currPnt.y);
//            TargetSpace AU = new TargetSpace(currPnt.x, currPnt.y - 1);
//            TargetSpace AR = new TargetSpace(currPnt.x + 1, currPnt.y);
//            TargetSpace AD = new TargetSpace(currPnt.x, currPnt.y + 1);
//
//
//            if (mDirection == Direction.LEFT && !bTurnsQueued)
//            {
//                //first handle cases with no choice to be made
//
//                if (LU.isBlocked() && L.isBlocked() && !DL.isBlocked()) // turn down at corner
//                {
//                    toTurnDown = true;
//                    bTurnsQueued = true;
//                    Point turn = AL.getCenter();
//                    nXTurn = turn.x;
//
//                }
//                if (DL.isBlocked() && L.isBlocked() && !LU.isBlocked()) // turn up at corner
//                {
//                    toTurnUp = true;
//                    bTurnsQueued = true;
//                    Point turn = AL.getCenter();
//                    nXTurn = turn.x;
//                }
//                if (L.isBlocked() && !LU.isBlocked() && !DL.isBlocked()) //handle two way intersection with wall in front
//                {
//                    // get center point of both possible squares to go to
//                    Point option1 = LU.getCenter();
//                    Point option2 = DL.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    else
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//
//                }
//                if (DL.isBlocked() && !L.isBlocked() && !LU.isBlocked()) //handle 3way junction up/left
//                {
//                    // get center point of both possible squares to go to
//                    Point option1 = LU.getCenter();
//                    Point option2 = L.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//                if (!DL.isBlocked() && !L.isBlocked() && LU.isBlocked()) //handle 3way junction down/left
//                {
//                    // get center point of both possible squares to go to
//                    Point option1 = DL.getCenter();
//                    Point option2 = L.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//                if (!L.isBlocked() && !LU.isBlocked() && !DL.isBlocked()) // 4 way intersection
//                {
//                    Point option1 = LU.getCenter();
//                    Point option2 = DL.getCenter();
//                    Point option3 = L.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//                    double dist3 = getDistance(option3, target);
//
//                    if(dist1 < dist2 && dist1 < dist3)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    else if (dist2 < dist1 && dist2 < dist3)
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//
//                }
//
//            }
//            if (mDirection == Direction.UP && !bTurnsQueued)
//            {
//                //first handle no choice turns
//                if(LU.isBlocked() && U.isBlocked() && !UR.isBlocked()) // turn right at corner
//                {
//                    toTurnRight = true;
//                    bTurnsQueued = true;
//                    Point turn = AU.getCenter();
//                    nYTurn = turn.y;
//                }
//                if(U.isBlocked() && UR.isBlocked() && !LU.isBlocked()) // turn left at corner
//                {
//                    toTurnLeft = true;
//                    bTurnsQueued = true;
//                    Point turn = AU.getCenter();
//                    nYTurn = turn.y;
//                }
//                if(U.isBlocked() && !LU.isBlocked() && !UR.isBlocked())//T intersection
//                {
//                    Point option1 = LU.getCenter();
//                    Point option2 = UR.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                    else
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if(LU.isBlocked() && !U.isBlocked() && !UR.isBlocked())//3way intersection up, right
//                {
//                    Point option1 = U.getCenter();
//                    Point option2 = UR.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 > dist2)
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if(!LU.isBlocked() && !U.isBlocked() && UR.isBlocked())//3way intersection up, left
//                {
//                    Point option1 = LU.getCenter();
//                    Point option2 = U.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if (!LU.isBlocked() && !U.isBlocked() && !UR.isBlocked()) //4way intersection
//                {
//                    Point option1 = LU.getCenter();
//                    Point option2 = UR.getCenter();
//                    Point option3 = U.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//                    double dist3 = getDistance(option3, target);
//
//                    if (dist1 < dist2 && dist1 < dist3)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                    else if (dist2 < dist1 && dist2 < dist3)
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//
//            }
//            if (mDirection == Direction.RIGHT && !bTurnsQueued)
//            {
//                if(RD.isBlocked() && R.isBlocked() && !UR.isBlocked())//turn up at corner
//                {
//                    toTurnUp = true;
//                    bTurnsQueued = true;
//                    Point turn = AR.getCenter();
//                    nXTurn = turn.x;
//                }
//                if(UR.isBlocked() && R.isBlocked() && !RD.isBlocked())//turn down at corner
//                {
//                    toTurnDown = true;
//                    bTurnsQueued = true;
//                    Point turn = AR.getCenter();
//                    nXTurn = turn.x;
//                }
//                if(R.isBlocked() && !UR.isBlocked() && !RD.isBlocked()) //T intersection
//                {
//                    Point option1 = UR.getCenter();
//                    Point option2 = RD.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    else
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//                if (RD.isBlocked() && !R.isBlocked() && !UR.isBlocked())//3 way right up
//                {
//                    Point option1 = UR.getCenter();
//                    Point option2 = R.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//                if (UR.isBlocked() && !R.isBlocked() && !RD.isBlocked())//3 way right down
//                {
//                    Point option1 = RD.getCenter();
//                    Point option2 = R.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//                if (!UR.isBlocked() && !R.isBlocked() && !RD.isBlocked()) //4way intersection
//                {
//                    Point option1 = UR.getCenter();
//                    Point option2 = RD.getCenter();
//                    Point option3 = R.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//                    double dist3 = getDistance(option3, target);
//
//                    if (dist1 < dist2 && dist1 < dist3)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    else if (dist2 < dist1 && dist2 < dist3)
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//            }
//            if (mDirection == Direction.DOWN && !bTurnsQueued)
//            {
//                if(RD.isBlocked() && D.isBlocked() && !DL.isBlocked()) //turn left at corner
//                {
//                    toTurnLeft = true;
//                    bTurnsQueued = true;
//                    Point turn = AD.getCenter();
//                    nYTurn = turn.y;
//                }
//                if (DL.isBlocked() && D.isBlocked() && !RD.isBlocked()) //turn right at corner
//                {
//                    toTurnRight = true;
//                    bTurnsQueued = true;
//                    Point turn = AD.getCenter();
//                    nYTurn = turn.y;
//                }
//                if (D.isBlocked() && !RD.isBlocked() && !DL.isBlocked()) // T intersection
//                {
//                    Point option1 = DL.getCenter();
//                    Point option2 = RD.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y; //DEBUG
//                    }
//                    else
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y; //debug
//                    }
//                }
//                if (DL.isBlocked() && !D.isBlocked() && !RD.isBlocked()) //3 way intersection down right
//                {
//                    Point option1 = RD.getCenter();
//                    Point option2 = D.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if (!DL.isBlocked() && !D.isBlocked() && RD.isBlocked()) //3 way intersection down left
//                {
//                    Point option1 = DL.getCenter();
//                    Point option2 = D.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//
//                    if (dist1 < dist2)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if (!DL.isBlocked() && !D.isBlocked() && !RD.isBlocked()) //4 way intersection
//                {
//                    Point option1 = DL.getCenter();
//                    Point option2 = RD.getCenter();
//                    Point option3 = D.getCenter();
//                    Point target = targetSquare.getCenter();
//                    double dist1 = getDistance(option1, target);
//                    double dist2 = getDistance(option2, target);
//                    double dist3 = getDistance(option3, target);
//
//                    if (dist1 < dist2 && dist1 < dist3)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                    else if (dist2 < dist1 && dist2 < dist3)
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//            }
//
//
//        }

        //CHASE MODE--------------------------------------------
//        public void chase()
//        {
//            //set speed
//            nGhostSpeed = 3;
//            setColor(ghostColor);
//
//
//            if (CommandCenter.getInstance().getBomberman() != null)
//            {
//
//                Square falconSquare = CommandCenter.getInstance().getBomberman().getCurrentSquare();
//
//                int iCurrentRow = getCurrentSquare().getRow();
//                int iCurrentCol = getCurrentSquare().getColumn();
//
//                //target spaces for tests for next move
//                Square L = CommandCenter.gameBoardSquares[iCurrentRow][iCurrentCol - 1];
//                Square LU = CommandCenter.gameBoardSquares[iCurrentRow - 1][iCurrentCol - 1];
//                Square U = CommandCenter.gameBoardSquares[iCurrentRow - 2][iCurrentCol];
//                Square UR = CommandCenter.gameBoardSquares[iCurrentRow - 1][iCurrentCol + 1];
//                Square R = CommandCenter.gameBoardSquares[iCurrentRow][iCurrentCol + 2];
//                Square RD = CommandCenter.gameBoardSquares[iCurrentRow + 1][iCurrentCol + 1];
//                Square D = CommandCenter.gameBoardSquares[iCurrentRow + 2][iCurrentCol];
//                Square DL = CommandCenter.gameBoardSquares[iCurrentRow + 1][iCurrentCol - 1];
//
//                //Adjacent target spaces
//                Square AL = CommandCenter.gameBoardSquares[iCurrentRow][iCurrentCol - 1];
//                Square AU = CommandCenter.gameBoardSquares[iCurrentRow - 1][iCurrentCol];
//                Square AR = CommandCenter.gameBoardSquares[iCurrentRow][iCurrentCol + 1];
//                Square AD = CommandCenter.gameBoardSquares[iCurrentRow + 1][iCurrentCol];
//
//                if (mDirection == Direction.LEFT && !bTurnsQueued)
//                {
//                    //first handle cases with no choice to be made
//
//                    if (LU.isBlocked() && L.isBlocked() && !DL.isBlocked()) // turn down at corner
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x; //+ TargetSpace.TS_WIDTH / 2;  //DEBUG check why this needs to be added
//
//                    }
//                    if (DL.isBlocked() && L.isBlocked() && !LU.isBlocked()) // turn up at corner
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    if (L.isBlocked() && !LU.isBlocked() && !DL.isBlocked()) //handle two way intersection with wall in front
//                    {
//                        // get center point of both possible squares to go to
//                        Point option1 = LU.getCenter();
//                        Point option2 = DL.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnUp = true;
//                            bTurnsQueued = true;
//                            Point turn = AL.getCenter();
//                            nXTurn = turn.x;
//                        } else
//                        {
//                            toTurnDown = true;
//                            bTurnsQueued = true;
//                            Point turn = AL.getCenter();
//                            nXTurn = turn.x;
//                        }
//
//                    }
//                    if (DL.isBlocked() && !L.isBlocked() && !LU.isBlocked()) //handle 3way junction up/left
//                    {
//                        // get center point of both possible squares to go to
//                        Point option1 = LU.getCenter();
//                        Point option2 = L.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnUp = true;
//                            bTurnsQueued = true;
//                            Point turn = AL.getCenter();
//                            nXTurn = turn.x;
//                        }
//                    }
//                    if (!DL.isBlocked() && !L.isBlocked() && LU.isBlocked()) //handle 3way junction down/left
//                    {
//                        // get center point of both possible squares to go to
//                        Point option1 = DL.getCenter();
//                        Point option2 = L.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnDown = true;
//                            bTurnsQueued = true;
//                            Point turn = AL.getCenter();
//                            nXTurn = turn.x;
//                        }
//                    }
//                    if (!L.isBlocked() && !LU.isBlocked() && !DL.isBlocked()) // 4 way intersection
//                    {
//                        Point option1 = LU.getCenter();
//                        Point option2 = DL.getCenter();
//                        Point option3 = L.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//                        double dist3 = getDistance(option3, target);
//
//                        if (dist1 < dist2 && dist1 < dist3)
//                        {
//                            toTurnUp = true;
//                            bTurnsQueued = true;
//                            Point turn = AL.getCenter();
//                            nXTurn = turn.x;
//                        } else if (dist2 < dist1 && dist2 < dist3)
//                        {
//                            toTurnDown = true;
//                            bTurnsQueued = true;
//                            Point turn = AL.getCenter();
//                            nXTurn = turn.x;
//                        }
//
//                    }
//
//                }
//                if (mDirection == Direction.UP && !bTurnsQueued)
//                {
//                    //first handle no choice turns
//                    if (LU.isBlocked() && U.isBlocked() && !UR.isBlocked()) // turn right at corner
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                    if (U.isBlocked() && UR.isBlocked() && !LU.isBlocked()) // turn left at corner
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                    if (U.isBlocked() && !LU.isBlocked() && !UR.isBlocked())//T intersection
//                    {
//                        Point option1 = LU.getCenter();
//                        Point option2 = UR.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnLeft = true;
//                            bTurnsQueued = true;
//                            Point turn = AU.getCenter();
//                            nYTurn = turn.y;
//                        } else
//                        {
//                            toTurnRight = true;
//                            bTurnsQueued = true;
//                            Point turn = AU.getCenter();
//                            nYTurn = turn.y;
//                        }
//                    }
//                    if (LU.isBlocked() && !U.isBlocked() && !UR.isBlocked())//3way intersection up, right
//                    {
//                        Point option1 = U.getCenter();
//                        Point option2 = UR.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 > dist2)
//                        {
//                            toTurnRight = true;
//                            bTurnsQueued = true;
//                            Point turn = AU.getCenter();
//                            nYTurn = turn.y;
//                        }
//                    }
//                    if (!LU.isBlocked() && !U.isBlocked() && UR.isBlocked())//3way intersection up, left
//                    {
//                        Point option1 = LU.getCenter();
//                        Point option2 = U.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnLeft = true;
//                            bTurnsQueued = true;
//                            Point turn = AU.getCenter();
//                            nYTurn = turn.y;
//                        }
//                    }
//                    if (!LU.isBlocked() && !U.isBlocked() && !UR.isBlocked()) //4way intersection
//                    {
//                        Point option1 = LU.getCenter();
//                        Point option2 = UR.getCenter();
//                        Point option3 = U.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//                        double dist3 = getDistance(option3, target);
//
//                        if (dist1 < dist2 && dist1 < dist3)
//                        {
//                            toTurnLeft = true;
//                            bTurnsQueued = true;
//                            Point turn = AU.getCenter();
//                            nYTurn = turn.y;
//                        } else if (dist2 < dist1 && dist2 < dist3)
//                        {
//                            toTurnRight = true;
//                            bTurnsQueued = true;
//                            Point turn = AU.getCenter();
//                            nYTurn = turn.y;
//                        }
//                    }
//
//                }
//                if (mDirection == Direction.RIGHT && !bTurnsQueued)
//                {
//                    if (RD.isBlocked() && R.isBlocked() && !UR.isBlocked())//turn up at corner
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    if (UR.isBlocked() && R.isBlocked() && !RD.isBlocked())//turn down at corner
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    if (R.isBlocked() && !UR.isBlocked() && !RD.isBlocked()) //T intersection
//                    {
//                        Point option1 = UR.getCenter();
//                        Point option2 = RD.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnUp = true;
//                            bTurnsQueued = true;
//                            Point turn = AR.getCenter();
//                            nXTurn = turn.x;
//                        } else
//                        {
//                            toTurnDown = true;
//                            bTurnsQueued = true;
//                            Point turn = AR.getCenter();
//                            nXTurn = turn.x;
//                        }
//                    }
//                    if (RD.isBlocked() && !R.isBlocked() && !UR.isBlocked())//3 way right up
//                    {
//                        Point option1 = UR.getCenter();
//                        Point option2 = R.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnUp = true;
//                            bTurnsQueued = true;
//                            Point turn = AR.getCenter();
//                            nXTurn = turn.x;
//                        }
//                    }
//                    if (UR.isBlocked() && !R.isBlocked() && !RD.isBlocked())//3 way right down
//                    {
//                        Point option1 = RD.getCenter();
//                        Point option2 = R.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnDown = true;
//                            bTurnsQueued = true;
//                            Point turn = AR.getCenter();
//                            nXTurn = turn.x;
//                        }
//                    }
//                    if (!UR.isBlocked() && !R.isBlocked() && !RD.isBlocked()) //4way intersection
//                    {
//                        Point option1 = UR.getCenter();
//                        Point option2 = RD.getCenter();
//                        Point option3 = R.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//                        double dist3 = getDistance(option3, target);
//
//                        if (dist1 < dist2 && dist1 < dist3)
//                        {
//                            toTurnUp = true;
//                            bTurnsQueued = true;
//                            Point turn = AR.getCenter();
//                            nXTurn = turn.x;
//                        } else if (dist2 < dist1 && dist2 < dist3)
//                        {
//                            toTurnDown = true;
//                            bTurnsQueued = true;
//                            Point turn = AR.getCenter();
//                            nXTurn = turn.x;
//                        }
//                    }
//                }
//                if (mDirection == Direction.DOWN && !bTurnsQueued)
//                {
//                    if (RD.isBlocked() && D.isBlocked() && !DL.isBlocked()) //turn left at corner
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;  //CHANGED TO DEBUG
//                    }
//                    if (DL.isBlocked() && D.isBlocked() && !RD.isBlocked()) //turn right at corner
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                    if (D.isBlocked() && !RD.isBlocked() && !DL.isBlocked()) // T intersection
//                    {
//                        Point option1 = DL.getCenter();
//                        Point option2 = RD.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnLeft = true;
//                            bTurnsQueued = true;
//                            Point turn = AD.getCenter();
//                            nYTurn = turn.y;
//                        } else
//                        {
//                            toTurnRight = true;
//                            bTurnsQueued = true;
//                            Point turn = AD.getCenter();
//                            nYTurn = turn.y;
//                        }
//                    }
//                    if (DL.isBlocked() && !D.isBlocked() && !RD.isBlocked()) //3 way intersection down right
//                    {
//                        Point option1 = RD.getCenter();
//                        Point option2 = D.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnRight = true;
//                            bTurnsQueued = true;
//                            Point turn = AD.getCenter();
//                            nYTurn = turn.y;
//                        }
//                    }
//                    if (!DL.isBlocked() && !D.isBlocked() && RD.isBlocked()) //3 way intersection down left
//                    {
//                        Point option1 = DL.getCenter();
//                        Point option2 = D.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//
//                        if (dist1 < dist2)
//                        {
//                            toTurnLeft = true;
//                            bTurnsQueued = true;
//                            Point turn = AD.getCenter();
//                            nYTurn = turn.y;
//                        }
//                    }
//                    if (!DL.isBlocked() && !D.isBlocked() && !RD.isBlocked()) //4 way intersection
//                    {
//                        Point option1 = DL.getCenter();
//                        Point option2 = RD.getCenter();
//                        Point option3 = D.getCenter();
//                        Point target = falconSquare.getCenter();
//                        double dist1 = getDistance(option1, target);
//                        double dist2 = getDistance(option2, target);
//                        double dist3 = getDistance(option3, target);
//
//                        if (dist1 < dist2 && dist1 < dist3)
//                        {
//                            toTurnLeft = true;
//                            bTurnsQueued = true;
//                            Point turn = AD.getCenter();
//                            nYTurn = turn.y;
//                        } else if (dist2 < dist1 && dist2 < dist3)
//                        {
//                            toTurnRight = true;
//                            bTurnsQueued = true;
//                            Point turn = AD.getCenter();
//                            nYTurn = turn.y;
//                        }
//                    }
//                }
//            }
//        }
//
//        public void frightened()
//        {
//            nGhostSpeed = 2;
//
//
//            //store a random number for random turning
//            Random ran = new Random();
//            int nRandom = ran.nextInt(100) + 1;
//
//            int iCurrentRow = getCurrentSquare().getRow();
//            int iCurrentCol = getCurrentSquare().getColumn();
//
//            //target spaces for tests for next move
//            Square L = CommandCenter.gameBoardSquares[iCurrentRow][iCurrentCol - 1];
//            Square LU = CommandCenter.gameBoardSquares[iCurrentRow - 1][iCurrentCol - 1];
//            Square U = CommandCenter.gameBoardSquares[iCurrentRow - 2][iCurrentCol];
//            Square UR = CommandCenter.gameBoardSquares[iCurrentRow - 1][iCurrentCol + 1];
//            Square R = CommandCenter.gameBoardSquares[iCurrentRow][iCurrentCol + 2];
//            Square RD = CommandCenter.gameBoardSquares[iCurrentRow + 1][iCurrentCol + 1];
//            Square D = CommandCenter.gameBoardSquares[iCurrentRow + 2][iCurrentCol];
//            Square DL = CommandCenter.gameBoardSquares[iCurrentRow + 1][iCurrentCol - 1];
//
//            //Adjacent target spaces
//            Square AL = CommandCenter.gameBoardSquares[iCurrentRow][iCurrentCol - 1];
//            Square AU = CommandCenter.gameBoardSquares[iCurrentRow - 1][iCurrentCol];
//            Square AR = CommandCenter.gameBoardSquares[iCurrentRow][iCurrentCol + 1];
//            Square AD = CommandCenter.gameBoardSquares[iCurrentRow + 1][iCurrentCol];
//
//
//
//            if (mDirection == Direction.LEFT && !bTurnsQueued)
//            {
//
//                //first handle cases with no choice to be made
//                if (LU.isBlocked() && L.isBlocked() && !DL.isBlocked()) // turn down at corner
//                {
//                    toTurnDown = true;
//                    bTurnsQueued = true;
//                    Point turn = AL.getCenter();
//                    nXTurn = turn.x;
//
//                }
//                if (DL.isBlocked() && L.isBlocked() && !LU.isBlocked()) // turn up at corner
//                {
//                    toTurnUp = true;
//                    bTurnsQueued = true;
//                    Point turn = AL.getCenter();
//                    nXTurn = turn.x;
//                }
//                if (L.isBlocked() && !LU.isBlocked() && !DL.isBlocked()) //handle two way intersection with wall in front
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    else
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//
//                }
//                if (DL.isBlocked() && !L.isBlocked() && !LU.isBlocked()) //handle 3way junction up/left
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//                if (!DL.isBlocked() && !L.isBlocked() && LU.isBlocked()) //handle 3way junction down/left
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//                if (!L.isBlocked() && !LU.isBlocked() && !DL.isBlocked()) // 4 way intersection
//                {
//
//                    if(nRandom % 3 == 0)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    else if (nRandom % 2 == 0)
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AL.getCenter();
//                        nXTurn = turn.x;
//                    }
//
//                }
//
//            }
//            if (mDirection == Direction.UP && !bTurnsQueued)
//            {
//                //first handle no choice turns
//                if(LU.isBlocked() && U.isBlocked() && !UR.isBlocked()) // turn right at corner
//                {
//                    toTurnRight = true;
//                    bTurnsQueued = true;
//                    Point turn = AU.getCenter();
//                    nYTurn = turn.y;
//                }
//                if(U.isBlocked() && UR.isBlocked() && !LU.isBlocked()) // turn left at corner
//                {
//                    toTurnLeft = true;
//                    bTurnsQueued = true;
//                    Point turn = AU.getCenter();
//                    nYTurn = turn.y;
//                }
//                if(U.isBlocked() && !LU.isBlocked() && !UR.isBlocked())//T intersection
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                    else
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if(LU.isBlocked() && !U.isBlocked() && !UR.isBlocked())//3way intersection up, right
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if(!LU.isBlocked() && !U.isBlocked() && UR.isBlocked())//3way intersection up, left
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if (!LU.isBlocked() && !U.isBlocked() && !UR.isBlocked()) //4way intersection
//                {
//
//                    if (nRandom % 3 == 0)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                    else if (nRandom % 2 == 0)
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AU.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//
//            }
//            if (mDirection == Direction.RIGHT && !bTurnsQueued)
//            {
//                if(RD.isBlocked() && R.isBlocked() && !UR.isBlocked())//turn up at corner
//                {
//                    toTurnUp = true;
//                    bTurnsQueued = true;
//                    Point turn = AR.getCenter();
//                    nXTurn = turn.x;
//                }
//                if(UR.isBlocked() && R.isBlocked() && !RD.isBlocked())//turn down at corner
//                {
//                    toTurnDown = true;
//                    bTurnsQueued = true;
//                    Point turn = AR.getCenter();
//                    nXTurn = turn.x;
//                }
//                if(R.isBlocked() && !UR.isBlocked() && !RD.isBlocked()) //T intersection
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    else
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//                if (RD.isBlocked() && !R.isBlocked() && !UR.isBlocked())//3 way right up
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//                if (UR.isBlocked() && !R.isBlocked() && !RD.isBlocked())//3 way right down
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//                if (!UR.isBlocked() && !R.isBlocked() && !RD.isBlocked()) //4way intersection
//                {
//
//                    if (nRandom % 3 == 0)
//                    {
//                        toTurnUp = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                    else if (nRandom % 2 == 0)
//                    {
//                        toTurnDown = true;
//                        bTurnsQueued = true;
//                        Point turn = AR.getCenter();
//                        nXTurn = turn.x;
//                    }
//                }
//            }
//            if (mDirection == Direction.DOWN && !bTurnsQueued)
//            {
//                if(RD.isBlocked() && D.isBlocked() && !DL.isBlocked()) //turn left at corner
//                {
//                    toTurnLeft = true;
//                    bTurnsQueued = true;
//                    Point turn = AD.getCenter();
//                    nYTurn = turn.y;
//                }
//                if (DL.isBlocked() && D.isBlocked() && !RD.isBlocked()) //turn right at corner
//                {
//                    toTurnRight = true;
//                    bTurnsQueued = true;
//                    Point turn = AD.getCenter();
//                    nYTurn = turn.y;
//                }
//                if (D.isBlocked() && !RD.isBlocked() && !DL.isBlocked()) // T intersection
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                    else
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if (DL.isBlocked() && !D.isBlocked() && !RD.isBlocked()) //3 way intersection down right
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if (!DL.isBlocked() && !D.isBlocked() && RD.isBlocked()) //3 way intersection down left
//                {
//
//                    if (nRandom % 2 == 0)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//                if (!DL.isBlocked() && !D.isBlocked() && !RD.isBlocked()) //4 way intersection
//                {
//
//                    if (nRandom % 3 == 0)
//                    {
//                        toTurnLeft = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                    else if (nRandom % 2 == 0)
//                    {
//                        toTurnRight = true;
//                        bTurnsQueued = true;
//                        Point turn = AD.getCenter();
//                        nYTurn = turn.y;
//                    }
//                }
//            }
//
//
//        }

        public void draw(Graphics g)
        {
            super.draw(g);
            g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        }


        public Point getGhostSpaceCoord()
        {
            return getCenter();
        }

        public double getDistance(Point a, Point b)
        {
            double leg1 = Math.abs(a.x - b.x);
            double leg2 = Math.abs(a.y - b.y);
            return (Math.sqrt((leg1*leg1) + (leg2*leg2)));
        }

        public void setRespawn(boolean bRespawn)
        {
            this.bRespawn = bRespawn;
        }

        public boolean getRespawn()
        {
            return bRespawn;
        }

    }





