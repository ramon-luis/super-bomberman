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

    public static final Dimension DIM = new Dimension(GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2 + 150,
            GameBoard.ROW_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2); //the dimension of the game.
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
            BOMB = 32, // space key
            KICK = 75, // k key
            MUTE = 77, // m-key mute


    // for possible future use
    // HYPER = 68, 					// d key
    // SHIELD = 65, 				// a key arrow
    // NUM_ENTER = 10, 				// hyp
    SPECIAL = 70;                    // fire special weapon;  F key

    private Clip clpThrust;
    private Clip clpMusicBackground;

    private static final int SPAWN_NEW_SHIP_FLOATER = 1200;


    // ===============================================
    // ==CONSTRUCTOR
    // ===============================================

    public Game() {

        gmpPanel = new GamePanel(DIM);
        gmpPanel.addKeyListener(this);
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


            if (CommandCenter.getInstance().isPlaying()) {
                //this might be a good place to check for collisions
                checkCollisions();
                checkNewLevel();
                //this might be a god place to check if the level is clear (no more enemies)
                //if the level is clear then spawn some big asteroids -- the number of asteroids
                //should increase with the level.
                checkRevealExit();
            }

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

        Point pntFriendCenter, pntEnemyCenter, pntBlastCenter, pntWallCenter;
        int nFriendRadiux, nEnemyRadiux, nBlastRadiux, nWallRadiux;


        // check Blasts
        for (Movable movBlast : CommandCenter.getInstance().getMovBlasts()) {
            // get square for blast
            Square blastSquare = movBlast.getCurrentSquare();

            // check blast against Enemies
            for (Movable movEnemy : CommandCenter.getInstance().getMovEnemies()) {
                // get square for enemy
                Square enemySquare = movEnemy.getCurrentSquare();

                // collision if blast and enemy same square
                if (blastSquare.equals(enemySquare)) {
                    Blast blast = (Blast) movBlast;
                    Enemy enemy = (Enemy) movEnemy;


                    // check if this is first time this blast and monster have collided
                    if (!blast.alreadyBlastedThisEnemy(enemy)) {
                        // connect the blast and monster -> prevents multiple hits from single blast
                        blast.addToBlastedMonsters(enemy);
                        // add hit to enemy
                        enemy.hitByBlast();
                        // increase score
                        CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + 100);

                        // check if monster dead
                        if (enemy.isDead()) {
                            // check for power up
                            if (enemy.containsPowerUp()) {
                                PowerUp powerUp = enemy.getPowerUpInside();
                                powerUp.setSquare(enemySquare);
                                CommandCenter.getInstance().getMovPowerUps().add(powerUp);
                            }

                            // remove for from OpsList
                            CommandCenter.getInstance().getOpsList().enqueue(movEnemy, CollisionOp.Operation.REMOVE);
                        }
                    }
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
                    // check for power up
                    if (wall.containsPowerUp()) {
                        PowerUp powerUp = wall.getPowerUpInside();
                        powerUp.setSquare(wallSquare);
                        CommandCenter.getInstance().getMovPowerUps().add(powerUp);
                    }
                    CommandCenter.getInstance().getOpsList().enqueue(movWall, CollisionOp.Operation.REMOVE);

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
                        Sound.playSound("bombermanDie.wav");
                    }
                }
            }

            // check blast against bombs
            for (Movable movBomb : CommandCenter.getInstance().getMovBombs()) {
                // get square for enemy
                Square bombSquare = movBomb.getCurrentSquare();

                // collision if blast and enemy same square
                if (blastSquare.equals(bombSquare)) {
                    ((Bomb) movBomb).explode();
                }
            }

        }

        // check Enemies
        for (Movable movEnemy : CommandCenter.getInstance().getMovEnemies()) {
            // get square for enemy
            Square enemySquare = movEnemy.getCurrentSquare();

            // check enemies against bomberman
            if (CommandCenter.getInstance().getBomberman() != null) {
                // get square for bomberman
                Square bombermanSquare = CommandCenter.getInstance().getBomberman().getCurrentSquare();

                // collision if blast and bomberman same square
                if (enemySquare.equals(bombermanSquare)) {
                    if (!CommandCenter.getInstance().getBomberman().getProtected()) {
                        CommandCenter.getInstance().getOpsList().enqueue(CommandCenter.getInstance().getBomberman(), CollisionOp.Operation.REMOVE);
                        CommandCenter.getInstance().spawnBomberman(false);
                        Sound.playSound("bombermanDie.wav");
                    }
                }
            }
        }

        // check Exit
        for (Movable movExit : CommandCenter.getInstance().getMovExits()) {
            // get square for enemy
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

        //check for collisions between bomberman and power ups
        for (Movable movPowerUp : CommandCenter.getInstance().getMovPowerUps()) {
            // get square for enemy
            Square powerUpSquare = movPowerUp.getCurrentSquare();

            // check exit against bomberman
            if (CommandCenter.getInstance().getBomberman() != null) {
                // get square for bomberman
                Square bombermanSquare = CommandCenter.getInstance().getBomberman().getCurrentSquare();

                // collision if exit and bomberman same square
                if (powerUpSquare.equals(bombermanSquare)) {
                    // process the power up (cast from Movable to PowerUp), then remove it
                    ((PowerUp) movPowerUp).process();
                    CommandCenter.getInstance().getOpsList().enqueue(movPowerUp, CollisionOp.Operation.REMOVE);
                }
            }
        }


        //we are dequeuing the opsList and performing operations in serial to avoid mutating the movable arraylists while iterating them above
        while (!CommandCenter.getInstance().getOpsList().isEmpty()) {
            CollisionOp cop = CommandCenter.getInstance().getOpsList().dequeue();
            Movable mov = cop.getMovable();
            CollisionOp.Operation operation = cop.getOperation();

            switch (mov.getTeam()) {
                case ENEMY:
                    if (operation == CollisionOp.Operation.ADD) {
                        CommandCenter.getInstance().getMovEnemies().add(mov);
                    } else {
                        CommandCenter.getInstance().getMovEnemies().remove(mov);
                        Sound.playSound("enemyDie.wav");
                    }
                    break;

                case FRIEND:
                    if (operation == CollisionOp.Operation.ADD) {
                        CommandCenter.getInstance().getMovFriends().add(mov);
                    } else {
                        CommandCenter.getInstance().getMovFriends().remove(mov);
                    }
                    break;

                case POWERUP:
                    if (operation == CollisionOp.Operation.ADD) {
                        CommandCenter.getInstance().getMovPowerUps().add(mov);
                    } else {
                        CommandCenter.getInstance().getMovPowerUps().remove(mov);
                    }
                    break;

                case BOMB:
                    if (operation == CollisionOp.Operation.ADD) {
                        CommandCenter.getInstance().getMovBombs().add(mov);
                    } else {
                        CommandCenter.getInstance().getMovBombs().remove(mov);
                    }
                    break;

                case BLAST:
                    if (operation == CollisionOp.Operation.ADD) {
                        CommandCenter.getInstance().getMovBlasts().add(mov);
                    } else {
                        CommandCenter.getInstance().getMovBlasts().remove(mov);
                    }
                    break;

                case WALL:
                    if (operation == CollisionOp.Operation.ADD) {
                        CommandCenter.getInstance().getMovWalls().add(mov);
                    } else {
                        CommandCenter.getInstance().getMovWalls().remove(mov);
                        mov.getCurrentSquare().removeWall();
                    }
                    break;

                case EXIT:
                    if (operation == CollisionOp.Operation.ADD) {
                        CommandCenter.getInstance().getMovExits().add(mov);
                    } else {
                        CommandCenter.getInstance().getMovExits().remove(mov);
                    }
            }

        }
        //a request to the JVM is made every frame to garbage collect, however, the JVM will choose when and how to do this
        System.gc();

    }


    //some methods for timing events in the game,
    //such as the appearance of UFOs, floaters (power-ups), etc.
    public void tick() {
        if (nTick == Integer.MAX_VALUE)
            nTick = 0;
        else
            nTick++;
    }

    public int getnTick() {
        return nTick;
    }


    // Called when user presses 's'
    private void startGame() {
        CommandCenter.getInstance().initGame();
        if (!bMuted)
            clpMusicBackground.start();
        clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
    }


    private void exitLevel() {
        bExitLevel = true;
    }

    private boolean isLevelClear() {
        boolean bEnemyFree = true;
        if (CommandCenter.getInstance().getMovEnemies().size() > 0)
            bEnemyFree = false;
        return bEnemyFree;
    }

    private void checkRevealExit() {
        if (CommandCenter.getInstance().getBomberman() != null && CommandCenter.getInstance().getMovExits().size() == 0) {
            if (isLevelClear()) {
                for (Square square : CommandCenter.getInstance().getGameBoard().getSquares()) {
                    if (square.isExit() && !square.isWall()) {
                        CommandCenter.getInstance().getOpsList().enqueue(new Exit(square), CollisionOp.Operation.ADD);
                        Sound.playSound("clearLevel.wav");
                    }
                }
            }
        }
    }

    private void checkNewLevel() {

        if (bExitLevel) {
            if (CommandCenter.getInstance().getBomberman() != null)
                CommandCenter.getInstance().getBomberman().setProtected(true);
            bExitLevel = false;
            if (!CommandCenter.getInstance().currentLevelIsMaxLevel()) {
                CommandCenter.getInstance().startNextLevel();
            } else {
                CommandCenter.getInstance().setAllLevelsComplete(true);
                CommandCenter.getInstance().setPlaying(false);
                CommandCenter.getInstance().setPaused(false);
            }

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
                    if (!CommandCenter.getInstance().allLevelsComplete()) {
                        CommandCenter.getInstance().setPaused(!CommandCenter.getInstance().isPaused());
                        if (CommandCenter.getInstance().isPaused())
                            stopLoopingSounds(clpMusicBackground);
                        else {
                            clpMusicBackground.start();
                            clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
                        }
                    }
                    break;

                case QUIT:
                    System.exit(0);
                    break;
                case UP:
                    // set direction to up
                    bomberman.setDirectionToMove(Bomberman.Direction.UP);

                    bomberman.moveOn();
                    break;

                case DOWN:
                    // set direction to up
                    bomberman.setDirectionToMove(Bomberman.Direction.DOWN);

                    bomberman.moveOn();
                    break;

                case LEFT:
                    // set the direction to left
                    bomberman.setDirectionToMove(Bomberman.Direction.LEFT);
                    bomberman.moveOn();

                    //fal.rotateLeft();
                    break;
                case RIGHT:
                    // set the direction to left
                    bomberman.setDirectionToMove(Bomberman.Direction.RIGHT);
                    // thrust so that it moves
                    bomberman.moveOn();

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
                case BOMB:
                    Square bombermanSquare = bomberman.getCurrentSquare();
                    if (bomberman.hasBombToUse() && !bombermanSquare.containsBomb()) {
                        Bomb bomb = new Bomb();
                        bomb.setCenter(bomberman.getCenter());
                        bombermanSquare.addBomb(bomb);
                        bomberman.useBomb();
                        CommandCenter.getInstance().getOpsList().enqueue(bomb, CollisionOp.Operation.ADD);
                        Sound.playSound("bloop.wav");
                    }
                    break;

                case KICK:
                    if (bomberman.hasKickAbility() && bomberman.nearBomb()) {
                        bomberman.kickBomb();
                    }



                    //special is a special weapon, current it just fires the cruise missile.
                case SPECIAL:
                    //CommandCenter.getInstance().getOpsList().enqueue(new Cruise(bomberman), CollisionOp.Operation.ADD);
                    //Sound.playSound("laser.wav");
                    break;

                case LEFT:
                    bomberman.moveOff();
                    bomberman.finishMove();
                    //fal.stopRotating();
                    break;
                case RIGHT:
                    bomberman.moveOff();
                    bomberman.finishMove();
                    //fal.stopRotating();
                    break;
                case UP:
                    bomberman.moveOff();
                    bomberman.finishMove();
                    break;

                case DOWN:
                    bomberman.moveOff();
                    bomberman.finishMove();
                    break;

                case MUTE:
                    if (!bMuted) {
                        stopLoopingSounds(clpMusicBackground);
                        bMuted = !bMuted;
                    } else {
                        clpMusicBackground.start();
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


