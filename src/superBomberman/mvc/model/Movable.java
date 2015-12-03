package superBomberman.mvc.model;

import java.awt.*;

public interface Movable {

	public static enum Team {
		FRIEND, ENEMY, POWERUP, BOMB, WALL, BLAST, EXIT
	}

	//for the game to move and draw movable objects
	public void move();
	public void draw(Graphics g);

	//for collision detection
	public Point getCenter();
	public int getSize();
	public Team getTeam();
	public Square getCurrentSquare();

} //end Movable
