package superBomberman.mvc.model;

import superBomberman.sounds.Sound;

import java.util.ArrayList;
import java.util.List;


public class CommandCenter {


    // ===============================================
    // FIELDS
    // ===============================================

    // private instance variables for the gameboard
    private GameBoard mGameBoard;
    private int nLevel;
    private boolean bLevelIsComplete;
    public static final int MAX_LEVEL = 7;
    private boolean mAllLevelsComplete;

    // private instance variables for the bomberman & score
    private Bomberman mBomberman;
    private int nNumBombermans;
    private long lScore;

    // private instance variables for status of the gameplay
    private boolean bPlaying;
    private boolean bPaused;
    private boolean bMuted;

    // array lists to store all game objects -> set initial capacity
    private List<Movable> movBombs = new ArrayList<>(300);
    private List<Movable> movBlasts = new ArrayList<>(300);
    private List<Movable> movWalls = new ArrayList<>(300);
    private List<Movable> movFriends = new ArrayList<>();
    private List<Movable> movEnemies = new ArrayList<>(200);
    private List<Movable> movPowerUps = new ArrayList<>(50);
    private List<Movable> movExits = new ArrayList<>();
    private List<Movable> movDisplays = new ArrayList<>();

    // private instance variables for game operations list & command center instance
    private GameOpsList opsList = new GameOpsList();
    private static CommandCenter instance = null;


    // ===============================================
    // CONSTRUCTOR
    // ===============================================

    // Constructor made private - static Utility class only
    private CommandCenter() {
    }


    // ===============================================
    // METHODS
    // ===============================================

    // get an instance of the command center
    public static CommandCenter getInstance() {
        if (instance == null) {
            instance = new CommandCenter();
        }
        return instance;
    }

    // get the game operations list
    public GameOpsList getOpsList() {
        return opsList;
    }

    // get the active bomberman object & lists of all other movables
    public Bomberman getBomberman() {
        return mBomberman;
    }
    public List<Movable> getMovBombs() {
        return movBombs;
    }
    public List<Movable> getMovBlasts() {
        return movBlasts;
    }
    public List<Movable> getMovWalls() {
        return movWalls;
    }
    public List<Movable> getMovFriends() {
        return movFriends;
    }
    public List<Movable> getMovEnemies() {
        return movEnemies;
    }
    public List<Movable> getMovPowerUps() {
        return movPowerUps;
    }
    public List<Movable> getMovExits() {return movExits;}
    public List<Movable> getMovDisplays() {return movDisplays;}

    // remove all moveable objects from their respective lists
    public void clearAll() {
        movBombs.clear();
        movBlasts.clear();
        movWalls.clear();
        movFriends.clear();
        movEnemies.clear();
        movPowerUps.clear();
        movExits.clear();
        movDisplays.clear();
    }

    // initialize the game -> occurs 1x at very start of game
    public void initGame() {
        clearAll();  // clear all objects from movable lists
        setLevel(7);  // set level to start at 1
        setLevelIsComplete(false);  // level is not yet complete
        setScore(0);  // start with score of 0
        setNumBombermans(3);  // 3 lives
        setGameBoard();  // set the gameboard
        spawnBomberman(true);  // spawn a new bomberman
        setPlaying(true);  // set the game to playing
        setPaused(false);  // game not paused
        setAllLevelsComplete(false);  // all levels complete is false
    }

    // start the next level
    public void startNextLevel() {
        clearAll();
        setScore(getScore() + 1000 * getLevel() + getNumBombermans() * 1000);
        setLevel(getLevel() + 1);
        setLevelIsComplete(false);
        setNumBombermans(3);
        setGameBoard();  // set gameboard each time -> method uses current level
        spawnBomberman(true);
    }

    // get the current gameboard
    public GameBoard getGameBoard() {
        return mGameBoard;
    }

    // set the game board -> creates a new gameboard object full of squares that have walls, enemies & powerups
    public void setGameBoard() {
		mGameBoard = new GameBoard(getLevel());
    }

    // create a new bomberman
    public void spawnBomberman(boolean bFirst) {
        // check if player has lives left
        if (getNumBombermans() != 0) {
            // create the bombermna, set the center & add to ops list
            mBomberman = new Bomberman();
            mBomberman.setCenter(getGameBoard().getSquare(1,1).getCenter());
            opsList.enqueue(mBomberman, CollisionOp.Operation.ADD);
            if (!bFirst)
                setNumBombermans(getNumBombermans() - 1);  // don't remove a life if this is start of game
        }
        Sound.playSound("shipspawn.wav");
    }

    // get the current level
    public int getLevel() {
        return nLevel;
    }

    // set the current level
    public void setLevel(int n) {
        nLevel = n;
    }

    // check if current level is complete
    public boolean levelIsComplete() {
        return bLevelIsComplete;
    }

    // set current level as complete (true
    public void setLevelIsComplete(boolean bParam) {
        bLevelIsComplete = bParam;
    }

    // check if current level is max level
    public boolean currentLevelIsMaxLevel() {
        return getLevel() == MAX_LEVEL;
    }

    // check if all levels have been completed
    public boolean allLevelsComplete() {
        return mAllLevelsComplete;
    }

    // set all levels as completed
    public void setAllLevelsComplete(boolean bParam) {
        mAllLevelsComplete = bParam;
    }

    // set the current score
    public long getScore() {
        return lScore;
    }

    // set the current score
    public void setScore(long lParam) {
        lScore = lParam;
    }

    // get the number of lives remaining
    public int getNumBombermans() {
        return nNumBombermans;
    }

    // set the number of lives remaining
    public void setNumBombermans(int nParam) {
        nNumBombermans = nParam;
    }

    // check if game is over -> no more lives
    public boolean isGameOver() {
        if (getNumBombermans() == 0) {
            return true;
        }
        return false;
    }

    // check if game is being played
    public boolean isPlaying() {
        return bPlaying;
    }

    // set game is being played
    public void setPlaying(boolean bPlaying) {
        this.bPlaying = bPlaying;
    }

    // check if game is paused
    public boolean isPaused() {
        return bPaused;
    }

    // set game is paused
    public void setPaused(boolean bPaused) {
        this.bPaused = bPaused;
    }

    // check if game is muted
    public boolean isMuted() {
        return bMuted;
    }

    // set game is muted
    public void setMuted(boolean bParam) {
        bMuted = bParam;
    }
}
