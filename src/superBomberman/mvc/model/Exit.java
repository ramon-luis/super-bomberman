package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Exit object is created on a square once all enemies are defeated
 * Exit is randomly hidden under breakable wall when assigned at gameboard creation
 */
public class Exit extends Sprite {

    public static final int RADIUS = Square.SQUARE_LENGTH / 2;
    private Color mExitColor;

    public Exit (Square square) {

        setTeam(Team.EXIT);

        // create list points for shape
        ArrayList<Point> pntCs = new ArrayList<>();
        pntCs.add(new Point(1,1));
        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(-1,-1));
        pntCs.add(new Point(-1,1));

        // assign Polar Points
        assignPolarPoints(pntCs);

        // set color, radius, and center
        setColor(Color.WHITE);
        setCenter(square.getCenter());
        setSize(RADIUS);

    }

    public void draw(Graphics g) {
        // alternate colors
        if (mExitColor == Color.BLACK) {
            mExitColor = Color.WHITE;
        } else {
            mExitColor = Color.BLACK;
        }
        super.draw(g);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);

        g.setColor(mExitColor);
        int iDrawX = (int) getCenter().getX() - RADIUS / 2;
        int iDrawY = (int) getCenter().getY() - RADIUS / 2;
        g.fillRect(iDrawX, iDrawY, RADIUS, RADIUS);
    }

}
