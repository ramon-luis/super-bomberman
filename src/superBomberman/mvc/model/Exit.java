package superBomberman.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by RAM0N on 11/29/15.
 */
public class Exit extends Sprite {

    public static final int RADIUS = Square.SQUARE_LENGTH / 2;


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
        Color wallColor = Color.YELLOW;
        setColor(wallColor);
        setCenter(square.getCenter());
        setRadius(RADIUS);

    }

    public void draw(Graphics g) {
        super.draw(g);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

}
