package edu.uchicago.cs.java.finalproject.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by RAM0N on 11/27/15.
 */
public class Wall extends Sprite {
//    fills entire box
//    public static final int WALL_RADIUS = (int) Math.sqrt(2 * (Square.SQUARE_LENGTH * Square.SQUARE_LENGTH));

    public enum Type {SOLID, BREAKABLE}

    public static final int WALL_RADIUS = Square.SQUARE_LENGTH / 2;
    private Type mType;

    public Wall (Square square, Type type) {

        // call to super necessary?
        //super();
        setTeam(Team.WALL);

        mType = type;

        // create list points for shape
        ArrayList<Point> pntCs = new ArrayList<>();
        pntCs.add(new Point(1,1));
        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(-1,-1));
        pntCs.add(new Point(-1,1));

        // assign Polar Points
        assignPolarPoints(pntCs);

        // set color, radius, and center
        Color wallColor = (type == Type.BREAKABLE ? Color.CYAN : Color.BLUE);
        setColor(wallColor);
        setCenter(square.getCenter());
        setRadius(WALL_RADIUS);

    }

    public boolean isBreakable() {
        return mType == Type.BREAKABLE;
    }

    public void draw(Graphics g) {
        super.draw(g);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

}
