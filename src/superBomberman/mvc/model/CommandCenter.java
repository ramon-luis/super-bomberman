package superBomberman.mvc.model;

import superBomberman.sounds.Sound;

import java.util.ArrayList;
import java.util.List;


public class CommandCenter {

    public static final int MAX_LEVEL = 7;
    private boolean mAllLevelsComplete;

    private GameBoard mGameBoard;

    private int nNumBombermans;
    private int nLevel;
    private long lScore;
    private Bomberman mBomberman;
    private boolean bPlaying;
    private boolean bPaused;

    // These ArrayLists with capacities set
    private List<Movable> movBombs = new ArrayList<>(300);
    private List<Movable> movBlasts = new ArrayList<>(300);
    private List<Movable> movWalls = new ArrayList<Movable>(300);
    private List<Movable> movFriends = new ArrayList<Movable>(100);
    private List<Movable> movEnemies = new ArrayList<Movable>(200);
    private List<Movable> movPowerUps = new ArrayList<Movable>(50);
    private List<Movable> movExits = new ArrayList<>();
    private List<Movable> movDisplays = new ArrayList<>();

    private GameOpsList opsList = new GameOpsList();

    //added by Dmitriy
    private static CommandCenter instance = null;

    // Constructor made private - static Utility class only
    private CommandCenter() {
    }


    public static CommandCenter getInstance() {
        if (instance == null) {
            instance = new CommandCenter();
        }
        return instance;
    }


    public void initGame() {
        clearAll();
        setLevel(1);
        setScore(0);
        setNumBombermans(3);
        setGameBoard();
        spawnBomberman(true);
        setPlaying(true);
        setPaused(false);
        setAllLevelsComplete(false);
    }



    public void startNextLevel() {
        clearAll();
        setScore(getScore() + 1000 * getLevel() + getNumBombermans() * 1000);
        setLevel(getLevel() + 1);
        setNumBombermans(3);
        setGameBoard();
        spawnBomberman(true);
    }

    public GameBoard getGameBoard() {
        return mGameBoard;
    }

    // draw the game board
    public void setGameBoard() {
		mGameBoard = new GameBoard(getLevel());
    }


    // The parameter is true if this is for the beginning of the game, otherwise false
    // When you spawn a new bomberman, you need to decrement its number
    public void spawnBomberman(boolean bFirst) {
        if (getNumBombermans() != 0) {
            mBomberman = new Bomberman();
            mBomberman.setCenter(getGameBoard().getSquare(1,1).getCenter());
            //movFriends.enqueue(mBomberman);
            opsList.enqueue(mBomberman, CollisionOp.Operation.ADD);
            if (!bFirst)
                setNumBombermans(getNumBombermans() - 1);
        }

        Sound.playSound("shipspawn.wav");

    }

    public GameOpsList getOpsList() {
        return opsList;
    }

    public void setOpsList(GameOpsList opsList) {
        this.opsList = opsList;
    }

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

    public boolean currentLevelIsMaxLevel() {
        return getLevel() == MAX_LEVEL;
    }

    public void setAllLevelsComplete(boolean bParam) {
        mAllLevelsComplete = bParam;
    }

    public boolean allLevelsComplete() {
        return mAllLevelsComplete;
    }

    public boolean isPlaying() {
        return bPlaying;
    }

    public void setPlaying(boolean bPlaying) {
        this.bPlaying = bPlaying;
    }

    public boolean isPaused() {
        return bPaused;
    }

    public void setPaused(boolean bPaused) {
        this.bPaused = bPaused;
    }

    public boolean isGameOver() {        //if the number of bombermans is zero, then game over
        if (getNumBombermans() == 0) {
            return true;
        }
        return false;
    }

    public int getLevel() {
        return nLevel;
    }

    public long getScore() {
        return lScore;
    }

    public void setScore(long lParam) {
        lScore = lParam;
    }

    public void setLevel(int n) {
        nLevel = n;
    }

    public int getNumBombermans() {
        return nNumBombermans;
    }

    public void setNumBombermans(int nParam) {
        nNumBombermans = nParam;
    }

    public Bomberman getBomberman() {
        return mBomberman;
    }

    public void setBomberman(Bomberman bombermanParam) {
        mBomberman = bombermanParam;
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
}
