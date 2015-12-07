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

    // constant the dimension of the game
    public static final Dimension DIM = new Dimension(GameBoard.COL_COUNT * Square.SQUARE_LENGTH
                                                + Square.SQUARE_LENGTH * 4,
                                        GameBoard.ROW_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2);

    // constants for key codes
    private final int PAUSE = 80, // p key
            QUIT = 81, // q key
            LEFT = 37, // rotate left; left arrow
            RIGHT = 39, // rotate right; right arrow
            UP = 38, // thrust; up arrow
            DOWN = 40, // down arrow
            START = 83, // s key
            BOMB = 32, // space key
            KICK = 75, // k key
            MUTE = 77; // m-key mute

    // private instance variables
    private GamePanel gmpPanel;  // game panel
    private Thread thrAnim;  // thread for animation
    public final static int ANI_DELAY = 45; // milliseconds between screen -> animation speed
    private Clip clpMusicBackground; // background music clip

    private int nTick = 0;  // used for timing events
    public static Random R = new Random();  // random number



    // ===============================================
    // CONSTRUCTOR
    // ===============================================

    public Game() {
        // set the game panel, add key listener and assign background music clip
        gmpPanel = new GamePanel(DIM);
        gmpPanel.addKeyListener(this);
        clpMusicBackground = Sound.clipForLoopFactory("music-background.wav");
    }

    // ===============================================
    // METHODS
    // ===============================================

    // main method to enter the program
    public static void main(String args[]) {
        // uses the Event dispatch thread by default -> higher priority thread that handles key events
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Game game = new Game(); // construct game
                    game.fireUpAnimThread();  // start animation thread
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // start the animation thread
    private void fireUpAnimThread() {
        // if animation thread does not exist, then create it, pass a runnable (this), and start thread
        if (thrAnim == null) {
            thrAnim = new Thread(this);  // this is reference to anonymous Runnable created in main
            thrAnim.start();
        }
    }

    // implements runnable - must have run method
    public void run() {

        // lower anim thread's priority; let the "main" aka 'Event Dispatch' thread do what it needs to do first
        thrAnim.setPriority(Thread.MIN_PRIORITY);

        // assign the current time
        long lStartTime = System.currentTimeMillis();

        // this thread animates the scene
        while (Thread.currentThread() == thrAnim) {
            tick();
            // update takes the graphics
            gmpPanel.update(gmpPanel.getGraphics());

            // if the game is playing: check collisions, check to reveal exit, check new level, check game over
            if (CommandCenter.getInstance().isPlaying()) {
                checkCollisions();
                checkRevealExit();
                checkNewLevel();
                checkGameOver();
            }

            // sleep the thread -> creates animation effect (use try-catch block)
            try {
                // The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update)
                // between frames takes longer than ANI_DELAY, then the difference between lStartTime -
                // System.currentTimeMillis() will be negative, then zero will be the sleep time
                lStartTime += ANI_DELAY;
                Thread.sleep(Math.max(0,
                        lStartTime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                continue;  // just skip this frame if exception -- no big deal
            }
        }
    }

    //check for collions
    private void checkCollisions() {
        // variables to check for collisions (point and radius of each movable)
//        Point pntFriendCenter, pntEnemyCenter, pntBlastCenter, pntWallCenter;
//        int nFriendRadiux, nEnemyRadiux, nBlastRadiux, nWallRadiux;

        // check Blasts collisions: loop through each blast
        for (Movable movBlast : CommandCenter.getInstance().getMovBlasts()) {
            // get square for blast
            Square blastSquare = movBlast.getCurrentSquare();

            // check blast against Enemies: loop through each enemy
            for (Movable movEnemy : CommandCenter.getInstance().getMovEnemies()) {
                // get square for enemy
                Square enemySquare = movEnemy.getCurrentSquare();

                // collision if blast and enemy same square
                if (blastSquare.equals(enemySquare)) {
                    Blast blast = (Blast) movBlast;
                    Enemy enemy = (Enemy) movEnemy;

                    // check if this is first time this blast and enemy have collided && enemy NOT a shock
                    if (!blast.alreadyBlastedThisEnemy(enemy) && !(enemy instanceof Shock)) {
                        // connect the blast and enemy -> prevents multiple hits from same blast
                        // (blast exits until expiry and could collide with an enemy until expiration)
                        blast.addToBlastedEnemies(enemy);

                        // add hit to enemy, increase score
                        enemy.hitByBlast();
                        CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + enemy.getScore());

                        // add a bodyBlast -> visual effect
                        BodyBlast bodyBlast = new BodyBlast();
                        bodyBlast.setCenter(enemy.getCenter());
                        CommandCenter.getInstance().getOpsList().enqueue(bodyBlast, CollisionOp.Operation.ADD);

                        // check if enemy dead
                        if (enemy.isDead()) {
                            // check for power up -> create one if the enemy is dead & has a power up
                            if (enemy.containsPowerUp()) {
                                PowerUp powerUp = enemy.getPowerUpInside();
                                powerUp.setSquare(enemySquare);
                                CommandCenter.getInstance().getMovPowerUps().add(powerUp);
                            }

                            // remove enemy from OpsList
                            CommandCenter.getInstance().getOpsList().enqueue(movEnemy, CollisionOp.Operation.REMOVE);
                        }
                    }
                }
            }

            // check blast against walls: loop through all walls
            for (Movable movWall : CommandCenter.getInstance().getMovWalls()) {
                // get square for wall
                Square wallSquare = movWall.getCurrentSquare();

                // cast from movable to wall in order to check if breakable
                Wall wall = (Wall) movWall;

                // collision if blast and wall same square AND wall is breakable
                if (blastSquare.equals(wallSquare) && wall.isBreakable()) {
                    // check for power up -> create on if the wall has a power up
                    if (wall.containsPowerUp()) {
                        PowerUp powerUp = wall.getPowerUpInside();
                        powerUp.setSquare(wallSquare);
                        CommandCenter.getInstance().getMovPowerUps().add(powerUp);
                    }
                    // remove wall from OpsList
                    wall.getCurrentSquare().removeWall();
                    CommandCenter.getInstance().getOpsList().enqueue(movWall, CollisionOp.Operation.REMOVE);
                }
            }

            // check blast against bomberman
            if (CommandCenter.getInstance().getBomberman() != null) {
                // get square bomberman & bomberman square
                Bomberman bomberman = CommandCenter.getInstance().getBomberman();
                Square bombermanSquare = bomberman.getCurrentSquare();

                // collision if blast and bomberman same square AND bomberman not protected
                if (blastSquare.equals(bombermanSquare) && !bomberman.getProtected()) {
                    // add a body blast -> visual effect
                    BodyBlast bodyBlast = new BodyBlast();
                    bodyBlast.setCenter(bomberman.getCenter());
                    CommandCenter.getInstance().getOpsList().enqueue(bodyBlast, CollisionOp.Operation.ADD);

                    // remove bomberman from Ops List
                    CommandCenter.getInstance().getOpsList().enqueue(bomberman, CollisionOp.Operation.REMOVE);
                    CommandCenter.getInstance().spawnBomberman(false);
                    Sound.playSound("bombermanDie.wav");
                }
            }

            // check blast against bombs: loop through all bombs
            for (Movable movBomb : CommandCenter.getInstance().getMovBombs()) {
                // get square for bomb
                Square bombSquare = movBomb.getCurrentSquare();

                // collision if blast and bomb same square
                if (blastSquare.equals(bombSquare)) {
                    ((Bomb) movBomb).explode();
                }
            }

        }

        // check Enemies Collisions: loop through all enemies
        for (Movable movEnemy : CommandCenter.getInstance().getMovEnemies()) {
            // get square for enemy
            Square enemySquare = movEnemy.getCurrentSquare();

            // check enemies against bomberman
            if (CommandCenter.getInstance().getBomberman() != null) {
                // get square for bomberman
                Bomberman bomberman = CommandCenter.getInstance().getBomberman();
                Square bombermanSquare = bomberman.getCurrentSquare();

                // collision if blast and bomberman same square
                if (enemySquare.equals(bombermanSquare) && !bomberman.getProtected()) {
                    // add a body blast -> visual effect
                    BodyBlast bodyBlast = new BodyBlast();
                    bodyBlast.setCenter(bomberman.getCenter());
                    CommandCenter.getInstance().getOpsList().enqueue(bodyBlast, CollisionOp.Operation.ADD);

                    // remove bomberman from Ops List
                    CommandCenter.getInstance().getOpsList().enqueue(bomberman, CollisionOp.Operation.REMOVE);
                    CommandCenter.getInstance().spawnBomberman(false);
                    Sound.playSound("bombermanDie.wav");
                }
            }

            // check shocks against bombs -> shcosk explode bombs
            if (movEnemy instanceof Shock) {
                if (movEnemy.getCurrentSquare().containsBomb()) {
                    movEnemy.getCurrentSquare().getBombInside().explode();
                }
            }

        }

        // check Exit Collisions: loop through all exits (only 1)
        for (Movable movExit : CommandCenter.getInstance().getMovExits()) {
            // get square for enemy
            Square exitSquare = movExit.getCurrentSquare();

            // check exit against bomberman
            if (CommandCenter.getInstance().getBomberman() != null) {
                // get square for bomberman
                Square bombermanSquare = CommandCenter.getInstance().getBomberman().getCurrentSquare();

                // collision if exit and bomberman same square
                if (exitSquare.equals(bombermanSquare)) {
                    // add a body blast -> visual effect
                    BodyBlast bodyBlast = new BodyBlast();
                    bodyBlast.setCenter(exitSquare.getCenter());
                    CommandCenter.getInstance().getOpsList().enqueue(bodyBlast, CollisionOp.Operation.ADD);

                    // exit level
                    Sound.playSound("exitLevel.wav");
                    CommandCenter.getInstance().levelIsComplete();
                }
            }
        }

        // Check PowerUp Collisions: loop through all PowerUps
        for (Movable movPowerUp : CommandCenter.getInstance().getMovPowerUps()) {
            // get square for enemy
            Square powerUpSquare = movPowerUp.getCurrentSquare();

            // check exit against bomberman
            if (CommandCenter.getInstance().getBomberman() != null) {
                // get square for bomberman
                Square bombermanSquare = CommandCenter.getInstance().getBomberman().getCurrentSquare();

                // collision if exit and bomberman same square
                if (powerUpSquare.equals(bombermanSquare)) {
                    // add a body blast -> visual effect
                    BodyBlast bodyBlast = new BodyBlast();
                    bodyBlast.setCenter(bombermanSquare.getCenter());
                    bodyBlast.setColor(Color.YELLOW);
                    CommandCenter.getInstance().getOpsList().enqueue(bodyBlast, CollisionOp.Operation.ADD);

                    // process the power up (cast from Movable to PowerUp), then remove it
                    ((PowerUp) movPowerUp).process();
                    CommandCenter.getInstance().getOpsList().enqueue(movPowerUp, CollisionOp.Operation.REMOVE);
                }
            }
        }


        // Dequeue OpsList while it is NOT empty
        // (perform operations in serial to avoid mutating while iterating them above)
        while (!CommandCenter.getInstance().getOpsList().isEmpty()) {
            // assign the queue, movable, and operation
            CollisionOp cop = CommandCenter.getInstance().getOpsList().dequeue();
            Movable mov = cop.getMovable();
            CollisionOp.Operation operation = cop.getOperation();

            // define action based on team of movable
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
                    }
                    break;

                case EXIT:
                    if (operation == CollisionOp.Operation.ADD) {
                        CommandCenter.getInstance().getMovExits().add(mov);
                    } else {
                        CommandCenter.getInstance().getMovExits().remove(mov);
                    }
                    break;

                case DISPLAY:
                    if (operation == CollisionOp.Operation.ADD) {
                        CommandCenter.getInstance().getMovDisplays().add(mov);
                    } else {
                        CommandCenter.getInstance().getMovDisplays().remove(mov);
                    }
                    break;
            }

        }

        // garbage collect (the JVM will choose when and how to do this)
        System.gc();
    }

    // increment tick -> used for timing events in game
    public void tick() {
        if (nTick == Integer.MAX_VALUE)
            nTick = 0;
        else
            nTick++;
    }

    // get tick -> used for timing events in game
    public int getnTick() {
        return nTick;
    }

    // start a new game
    private void startGame() {
        // initialize game and start music (if muted)
        CommandCenter.getInstance().initGame();
        if (!CommandCenter.getInstance().isMuted())
            clpMusicBackground.start();
        clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // check if level is clear of all enemies: check if list of enemies > 0
    private boolean isLevelClear() {
        boolean bEnemyFree = true;
        if (CommandCenter.getInstance().getMovEnemies().size() > 0)
            bEnemyFree = false;
        return bEnemyFree;
    }

    // check if should reveal exit
    private void checkRevealExit() {
        boolean bIsActiveBomberman = CommandCenter.getInstance().getBomberman() != null;
        boolean bExitHasNotBeenSet = CommandCenter.getInstance().getMovExits().size() == 0;

        // check if there is a bomberman AND level is clear of enemies AND exit has not yet been revealed
        if (bIsActiveBomberman && bExitHasNotBeenSet && isLevelClear()) {
            // find the exit square -> if exit square does NOT have a wall, then add exit to the square
            for (Square square : CommandCenter.getInstance().getGameBoard().getSquares()) {
                if (square.isExit() && !square.isWall()) {
                    CommandCenter.getInstance().getOpsList().enqueue(new Exit(square), CollisionOp.Operation.ADD);
                    Sound.playSound("exitReveal.wav");
                }
            }
        }
    }

    // check for new level
    private void checkNewLevel() {
        // if exit level has been set to true
        if (CommandCenter.getInstance().levelIsComplete()) {
            // start new level if not at max level -> otherwise stop playing, stop paused, and set all levels complete
            // (these options will have GamePanel display victory screen)
            if (!CommandCenter.getInstance().currentLevelIsMaxLevel()) {
                CommandCenter.getInstance().startNextLevel();
            } else {
                CommandCenter.getInstance().setAllLevelsComplete(true);
                CommandCenter.getInstance().setPlaying(false);
                CommandCenter.getInstance().setPaused(false);
            }
        }
    }

    // check if the game is over
    private void checkGameOver() {
        if (CommandCenter.getInstance().isGameOver()) {
            Sound.playSound("gameOver.wav");
            CommandCenter.getInstance().setPlaying(false);
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
        // get the bomberman and key pressed
        Bomberman bomberman = CommandCenter.getInstance().getBomberman();
        int nKey = e.getKeyCode();
        // System.out.println(nKey);  // use to check key int value in console

        // START is only key that can be used while bomberman is null -> starts game
        if (nKey == START)
            if (!CommandCenter.getInstance().isPlaying())
                if (!CommandCenter.getInstance().allLevelsComplete()) {
                    startGame();
                } else {
                    // these settings will cause welcome screen to be displayed from victory screen
                    CommandCenter.getInstance().setPlaying(false);
                    CommandCenter.getInstance().setPaused(false);
                    CommandCenter.getInstance().setAllLevelsComplete(false);
                }

        // these keys are only valid if bomberman exists
        if (bomberman != null) {
            // switch action based on key value
            switch (nKey) {

                case PAUSE:
                    // pause the game
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
                    // quit the game
                    System.exit(0);
                    break;

                case UP:
                    // set direction to up & bomberman to moving (continues to move while key is pressed)
                    bomberman.setDirectionToMove(Bomberman.Direction.UP);
                    bomberman.moveOn();
                    break;

                case DOWN:
                    // set direction to down & bomberman to moving (continues to move while key is pressed)
                    bomberman.setDirectionToMove(Bomberman.Direction.DOWN);
                    bomberman.moveOn();
                    break;

                case LEFT:
                    // set the direction to left & bomberman to moving (continues to move while key is pressed)
                    bomberman.setDirectionToMove(Bomberman.Direction.LEFT);
                    bomberman.moveOn();
                    break;
                case RIGHT:
                    // set the direction to right & bomberman to moving (continues to move while key is pressed)
                    bomberman.setDirectionToMove(Bomberman.Direction.RIGHT);
                    bomberman.moveOn();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // get the bomberman and key pressed
        Bomberman bomberman = CommandCenter.getInstance().getBomberman();
        int nKey = e.getKeyCode();
        // System.out.println(nKey);  // use to check key int value in console

        // these keys are only valid if bomberman exists
        if (bomberman != null) {
            // switch action based on key value
            switch (nKey) {

                case BOMB:
                    // create new bomb in bomberman's current square
                    // bomberman must have available bomb and current square cannot contain a bomb
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
                    // kick a bomb
                    // bomberman must have kick ability bomb and be facing an adjacent bomb
                    if (bomberman.hasKickAbility() && bomberman.isFacingBomb()) {
                        bomberman.kickBomb();
                        Sound.playSound("bombKick.wav");
                    }

                case LEFT:
                    // stop bomberman from moving when direction key is released
                    bomberman.moveOff();
                    break;

                case RIGHT:
                    // stop bomberman from moving when direction key is released
                    bomberman.moveOff();
                    break;

                case UP:
                    // stop bomberman from moving when direction key is released
                    bomberman.moveOff();
                    break;

                case DOWN:
                    // stop bomberman from moving when direction key is released
                    bomberman.moveOff();
                    break;

                case MUTE:
                    // mute the background music
                    if (!CommandCenter.getInstance().getInstance().isMuted()) {
                        stopLoopingSounds(clpMusicBackground);
                        CommandCenter.getInstance().setMuted(false);
                    } else {
                        clpMusicBackground.start();
                        clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
                        CommandCenter.getInstance().setMuted(false);
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