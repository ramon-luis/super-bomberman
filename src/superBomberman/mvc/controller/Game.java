package superBomberman.mvc.controller;

import superBomberman.mvc.view.GamePanel;
import superBomberman.sounds.Sound;
import superBomberman.mvc.model.*;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

	// ===============================================
	// FIELDS
	// ===============================================

	public static final Dimension DIM = new Dimension(GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2 + 150, GameBoard.ROW_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH/2); //the dimension of the game.
	private GamePanel gmpPanel;
	public static Random R = new Random();
	public final static int ANI_DELAY = 45; // milliseconds between screen
											// updates (animation)
	private Thread thrAnim;
	private int nLevel = 1;
	private int nTick = 0;

	private boolean bMuted = true;

	private boolean bExitLevel = false;
	

	private final int PAUSE = 80, // p key
			QUIT = 81, // q key
			LEFT = 37, // rotate left; left arrow
			RIGHT = 39, // rotate right; right arrow
			UP = 38, // thrust; up arrow
			DOWN = 40, // down arrow
			START = 83, // s key
			FIRE = 32, // space key
			MUTE = 77, // m-key mute

	// for possible future use
	// HYPER = 68, 					// d key
	// SHIELD = 65, 				// a key arrow
	// NUM_ENTER = 10, 				// hyp
	 SPECIAL = 70; 					// fire special weapon;  F key

	private Clip clpThrust;
	private Clip clpMusicBackground;

	private static final int SPAWN_NEW_SHIP_FLOATER = 1200;



	// ===============================================
	// ==CONSTRUCTOR
	// ===============================================

	public Game() {

		gmpPanel = new GamePanel(DIM);
		gmpPanel.addKeyListener(this);
		clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
		clpMusicBackground = Sound.clipForLoopFactory("music-background.wav");
	

	}

	// ===============================================
	// ==METHODS
	// ===============================================

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
					public void run() {
						try {
							Game game = new Game(); // construct itself
							game.fireUpAnimThread();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void fireUpAnimThread() { // called initially
		if (thrAnim == null) {
			thrAnim = new Thread(this); // pass the thread a runnable object (this)
			thrAnim.start();
		}
	}

	// implements runnable - must have run method
	public void run() {

		// lower this thread's priority; let the "main" aka 'Event Dispatch'
		// thread do what it needs to do first
		thrAnim.setPriority(Thread.MIN_PRIORITY);

		// and get the current time
		long lStartTime = System.currentTimeMillis();

		// this thread animates the scene
		while (Thread.currentThread() == thrAnim) {
			tick();
			//spawnNewShipFloater();

			gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must 
														// surround the sleep() in a try/catch block
														// this simply controls delay time between 
														// the frames of the animation



			//this might be a good place to check for collisions
			checkCollisions();
			//this might be a god place to check if the level is clear (no more foes)
			//if the level is clear then spawn some big asteroids -- the number of asteroids 
			//should increase with the level. 
			checkNewLevel();

			try {
				// The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update) 
				// between frames takes longer than ANI_DELAY, then the difference between lStartTime - 
				// System.currentTimeMillis() will be negative, then zero will be the sleep time
				lStartTime += ANI_DELAY;
				Thread.sleep(Math.max(0,
						lStartTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				// just skip this frame -- no big deal
				continue;
			}
		} // end while
	} // end run


	private void checkCollisions() {

		Point pntFriendCenter, pntFoeCenter, pntBlastCenter, pntWallCenter;
		int nFriendRadiux, nFoeRadiux, nBlastRadiux, nWallRadiux;


		// check Blasts
		for (Movable movBlast : CommandCenter.getInstance().getMovBlasts()) {
			// get square for blast
			Square blastSquare = movBlast.getCurrentSquare();

			// check blast against Foes
			for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {
				// get square for foe
				Square foeSquare = movFoe.getCurrentSquare();

				// collision if blast and foe same square
				if (blastSquare.equals(foeSquare)) {
					CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + 100);
					CommandCenter.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);
				}
			}

			// check blast against walls
			for (Movable movWall : CommandCenter.getInstance().getMovWalls()) {
				// get square for wall
				Square wallSquare = movWall.getCurrentSquare();

				// cast from movable to wall in order to check if breakable
				Wall wall = (Wall) movWall;
				// collision if blast and wall same square AND wall is breakable
				if (blastSquare.equals(wallSquare) && wall.isBreakable()) {
					CommandCenter.getInstance().getOpsList().enqueue(movWall, CollisionOp.Operation.REMOVE);

					// check to reveal exit
					if (wallSquare.isExit()) {
						CommandCenter.getInstance().getOpsList().enqueue(new Exit(wallSquare), CollisionOp.Operation.ADD);
					}
				}
			}


			// check blast against bomberman
			if (CommandCenter.getInstance().getBomberman() != null) {
				// get square bomberman
				Square bombermanSquare = CommandCenter.getInstance().getBomberman().getCurrentSquare();

				// collision if blast and bomberman same square
				if (blastSquare.equals(bombermanSquare)) {
					if (!CommandCenter.getInstance().getBomberman().getProtected()) {
						CommandCenter.getInstance().getOpsList().enqueue(CommandCenter.getInstance().getBomberman(), CollisionOp.Operation.REMOVE);
						CommandCenter.getInstance().spawnBomberman(false);
					}
				}
			}
		}

		// check Foes
		for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {
			// get square for foe
			Square foeSquare = movFoe.getCurrentSquare();

			// check foes against bomberman
			if (CommandCenter.getInstance().getBomberman() != null) {
				// get square for bomberman
				Square bombermanSquare = CommandCenter.getInstance().getBomberman().getCurrentSquare();

				// collision if blast and bomberman same square
				if (foeSquare.equals(bombermanSquare)) {
					if (!CommandCenter.getInstance().getBomberman().getProtected()) {
						CommandCenter.getInstance().getOpsList().enqueue(CommandCenter.getInstance().getBomberman(), CollisionOp.Operation.REMOVE);
						CommandCenter.getInstance().spawnBomberman(false);
					}
				}
			}
		}

		// check Exit
		for (Movable movExit : CommandCenter.getInstance().getMovExits()) {
			// get square for foe
			Square exitSquare = movExit.getCurrentSquare();

			// check exit against bomberman
			if (CommandCenter.getInstance().getBomberman() != null) {
				// get square for bomberman
				Square bombermanSquare = CommandCenter.getInstance().getBomberman().getCurrentSquare();

				// collision if exit and bomberman same square
				if (exitSquare.equals(bombermanSquare)) {
					exitLevel();
				}
			}
		}

		//check for collisions between bomberman and floaters
		// CODE HERE



		//we are dequeuing the opsList and performing operations in serial to avoid mutating the movable arraylists while iterating them above
		while(!CommandCenter.getInstance().getOpsList().isEmpty()){
			CollisionOp cop =  CommandCenter.getInstance().getOpsList().dequeue();
			Movable mov = cop.getMovable();
			CollisionOp.Operation operation = cop.getOperation();

			switch (mov.getTeam()){
				case FOE:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovFoes().add(mov);
					} else {
						CommandCenter.getInstance().getMovFoes().remove(mov);
					}

					break;
				case FRIEND:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovFriends().add(mov);
					} else {
						CommandCenter.getInstance().getMovFriends().remove(mov);
					}
					break;

				case FLOATER:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovFloaters().add(mov);
					} else {
						CommandCenter.getInstance().getMovFloaters().remove(mov);
					}
					break;

				case BOMB:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovBombs().add(mov);
					} else {
						CommandCenter.getInstance().getMovBombs().remove(mov);
						mov.getCurrentSquare().removeBomb();
						CommandCenter.getInstance().getBomberman().addBombToUse();
					}
					break;
				case BLAST:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovBlasts().add(mov);
					} else {
						CommandCenter.getInstance().getMovBlasts().remove(mov);
					}
					break;
				case WALL:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovWalls().add(mov);
					} else {
						CommandCenter.getInstance().getMovWalls().remove(mov);
						mov.getCurrentSquare().removeWall();
					}
					break;
				case EXIT:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovExits().add(mov);
					} else {
						CommandCenter.getInstance().getMovExits().remove(mov);
					}
			}

		}
		//a request to the JVM is made every frame to garbage collect, however, the JVM will choose when and how to do this
		System.gc();
		
	}//end meth

	private void killFoe(Movable movFoe) {
		
		if (movFoe instanceof Asteroid){

			//we know this is an Asteroid, so we can cast without threat of ClassCastException
			Asteroid astExploded = (Asteroid)movFoe;
			//big asteroid 
			if(astExploded.getSize() == 0){
				//spawn two medium Asteroids
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);

			} 
			//medium size aseroid exploded
			else if(astExploded.getSize() == 1){
				//spawn three small Asteroids
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);

			}

		} 

		//remove the original Foe
		CommandCenter.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);

	}

	//some methods for timing events in the game,
	//such as the appearance of UFOs, floaters (power-ups), etc. 
	public void tick() {
		if (nTick == Integer.MAX_VALUE)
			nTick = 0;
		else
			nTick++;
	}

	public int getTick() {
		return nTick;
	}

	private void spawnNewShipFloater() {
		//make the appearance of power-up dependent upon ticks and levels
		//the higher the level the more frequent the appearance
		if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 7) == 0) {
			//Cc.getInstance().getMovFloaters().enqueue(new NewShipFloater());
			CommandCenter.getInstance().getOpsList().enqueue(new NewShipFloater(), CollisionOp.Operation.ADD);
		}
	}

	// Called when user presses 's'
	private void startGame() {
		CommandCenter.getInstance().clearAll();
		CommandCenter.getInstance().initGame();
		CommandCenter.getInstance().setLevel(1);
		CommandCenter.getInstance().setPlaying(true);
		CommandCenter.getInstance().setPaused(false);
		//if (!bMuted)
		   // clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
	}

	//this method spawns new asteroids
	private void spawnAsteroids(int nNum) {
		for (int nC = 0; nC < nNum; nC++) {
			//Asteroids with size of zero are big
			CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(0), CollisionOp.Operation.ADD);

		}
	}
	
	private void exitLevel() {
		bExitLevel = true;
	}

	private boolean isLevelClear(){
		//if there are no more Asteroids on the screen
		boolean bAsteroidFree = true;
		for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {
			if (movFoe instanceof Asteroid){
				bAsteroidFree = false;
				break;
			}
		}
		
		return bAsteroidFree;

		
	}
	
	private void checkNewLevel(){
		
		if (bExitLevel){
			if (CommandCenter.getInstance().getBomberman() !=null)
				CommandCenter.getInstance().getBomberman().setProtected(true);
			bExitLevel = false;
			CommandCenter.getInstance().startNextLevel();

		}
	}
	
	
	

	// Varargs for stopping looping-music-clips
	private static void stopLoopingSounds(Clip... clpClips) {
		for (Clip clp : clpClips) {
			clp.stop();
		}
	}

	// ===============================================
	// KEYLISTENER METHODS
	// ===============================================

	@Override
	public void keyPressed(KeyEvent e) {
		Bomberman bomberman = CommandCenter.getInstance().getBomberman();
		int nKey = e.getKeyCode();
		// System.out.println(nKey);

		if (nKey == START && !CommandCenter.getInstance().isPlaying())
			startGame();

		if (bomberman != null) {

			switch (nKey) {
			case PAUSE:
				CommandCenter.getInstance().setPaused(!CommandCenter.getInstance().isPaused());
				if (CommandCenter.getInstance().isPaused())
					stopLoopingSounds(clpMusicBackground, clpThrust);
				else
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			case QUIT:
				System.exit(0);
				break;
			case UP:
				// set direction to up
				bomberman.setDirectionToMove(Bomberman.Direction.UP);

				bomberman.thrustOn();
				if (!CommandCenter.getInstance().isPaused())
					clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
				break;

				case DOWN:
					// set direction to up
					bomberman.setDirectionToMove(Bomberman.Direction.DOWN);

					bomberman.thrustOn();
					if (!CommandCenter.getInstance().isPaused())
						clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
					break;

			case LEFT:
				// set the direction to left
				bomberman.setDirectionToMove(Bomberman.Direction.LEFT);
				// thrust so that it moves
				bomberman.thrustOn();
				if (!CommandCenter.getInstance().isPaused())
					clpThrust.loop(Clip.LOOP_CONTINUOUSLY);

				//fal.rotateLeft();
				break;
			case RIGHT:
				// set the direction to left
				bomberman.setDirectionToMove(Bomberman.Direction.RIGHT);
				// thrust so that it moves
				bomberman.thrustOn();
				if (!CommandCenter.getInstance().isPaused())
					clpThrust.loop(Clip.LOOP_CONTINUOUSLY);

				//fal.rotateRight();
				break;

			// possible future use
			// case KILL:
			// case SHIELD:
			// case NUM_ENTER:

			default:
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Bomberman bomberman = CommandCenter.getInstance().getBomberman();
		int nKey = e.getKeyCode();
		 System.out.println(nKey);

		if (bomberman != null) {
			switch (nKey) {
			case FIRE:


				if (CommandCenter.getInstance().getBomberman().hasBombToUse() && !CommandCenter.getInstance().getBomberman().getCurrentSquare().containsBomb()) {
					CommandCenter.getInstance().getOpsList().enqueue(new Bomb(bomberman), CollisionOp.Operation.ADD);
				}

				Sound.playSound("laser.wav");
				break;
				
			//special is a special weapon, current it just fires the cruise missile. 
			case SPECIAL:
				CommandCenter.getInstance().getOpsList().enqueue(new Cruise(bomberman), CollisionOp.Operation.ADD);
				//Sound.playSound("laser.wav");
				break;
				
			case LEFT:
				bomberman.thrustOff();
				clpThrust.stop();
				bomberman.finishMove();
				//fal.stopRotating();
				break;
			case RIGHT:
				bomberman.thrustOff();
				clpThrust.stop();
				bomberman.finishMove();
				//fal.stopRotating();
				break;
			case UP:
				bomberman.thrustOff();
				clpThrust.stop();
				bomberman.finishMove();
				break;

				case DOWN:
					bomberman.thrustOff();
					clpThrust.stop();
					bomberman.finishMove();
					break;

			case MUTE:
				if (!bMuted){
					stopLoopingSounds(clpMusicBackground);
					bMuted = !bMuted;
				} 
				else {
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
					bMuted = !bMuted;
				}
				break;
				
				
			default:
				break;
			}
		}
	}

	@Override
	// Just need it b/c of KeyListener implementation
	public void keyTyped(KeyEvent e) {
	}



}


