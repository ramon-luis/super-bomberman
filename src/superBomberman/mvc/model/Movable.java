package superBomberman.mvc.model;

import java.awt.*;

public interface Movable {

    // enum for team of each movable -> used to handle collision logic
    enum Team {
        FRIEND, ENEMY, POWERUP, BOMB, WALL, BLAST, EXIT
    }

    // animation methods: animation occurs as objects are moved and drawn
    void move();  // moves the location of an object
    void draw(Graphics g);  // draws the object

    // getter methods
    Point getCenter();  //for collision detection & placement
    int getSize();  // get the size of an object
    Team getTeam();  // get the team of the object
    Square getCurrentSquare();  // for placement in draw

}
