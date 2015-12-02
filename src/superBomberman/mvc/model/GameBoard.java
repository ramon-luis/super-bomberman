package superBomberman.mvc.model;

import java.util.*;

import superBomberman.mvc.model.Wall.WallType;

/**
 * Created by RAM0N on 11/28/15.
 */
public class GameBoard {

    // constants for size of game board
    public static final int ROW_COUNT = 13;
    public static final int COL_COUNT = 15;

    // private instance members
    private List<Square> mSquares;
    private List<PowerUp> mPowerUps;
    private List<Monster> mMonsters;
    private List<Wall> mBreakableWalls;

    private int mLevel;
    private int mPowerUpBombCount;  // # of powerUp bombs
    private int mPowerUpBlastCount;  // # of powerUp  blasts
    private int mMonsterSourceCount;  // # of monsters used as source for power up


    // constructor
    public GameBoard(int level) {
        // assign level
        mLevel = level;

        // initialize lists
        mSquares = new ArrayList<>(ROW_COUNT * COL_COUNT);
        mPowerUps = new ArrayList<>();
        mMonsters = new ArrayList<>();
        mBreakableWalls = new ArrayList<>(ROW_COUNT * COL_COUNT);

        // create the game board
        createLevel();  // squares, walls, and monsters
        createPowerUps();  // assigned to breakable walls & monsters
        createExit();  // assigned to random square with breakable wall (and no power up)
    }


    // get square based on row and col
    public Square getSquare(int row, int col) {
        for (Square square : mSquares)
            if (square.getRow() == row && square.getColumn() == col)
                return square;
        throw new IllegalArgumentException("Square not found");
    }

    // get all squares in list
    public List<Square> getSquares() {
        return mSquares;
    }


    // ****************
    //  HELPER METHODS
    // ****************

    // create level (squares, walls, and monsters)
    private void createLevel() {
        // define list of data maps for each square -> default is Level 1
        List<Map<Integer, Integer>> squareDataMaps = getSquareDataMapsLevel1();

        // update the square data based on level
        if (mLevel == 2)
            squareDataMaps = getSquareDataMapsLevel2();
//        else if (mLevel == 3)
//            squareDataMaps = getSquareDataMapsLevel3();

        // for through each row
        for (int i = 0; i < ROW_COUNT; i++) {
            // loop through data in matching map from map list
            for (Map.Entry<Integer, Integer> entry : squareDataMaps.get(i).entrySet()) {
                // create the square
                Square square = new Square(i, entry.getKey());
                mSquares.add(square);

                // add object (0 == do nothing, 1 == Solid Wall, 2 == Breakable Wall, 3 == Monster)
                if (entry.getValue() == 1 || entry.getValue() == 2) {
                    // define wall type, create wall, add to square, add to OpsList
                    WallType wallType = (entry.getValue() == 1) ? WallType.SOLID : WallType.BREAKABLE;
                    Wall wall = new Wall(square, wallType);
                    square.setInside(wall);
                    square.addWall();
                    if (wallType == WallType.BREAKABLE)
                        mBreakableWalls.add(wall);  // list is used for adding power ups
                    CommandCenter.getInstance().getOpsList().enqueue(wall, CollisionOp.Operation.ADD);
                } else if (entry.getValue() == 3) {
                    // create monster, add to OpsList
                    Monster monster = new Monster(square, Monster.MonsterType.ALIEN);
                    mMonsters.add(monster);  // list is used for adding power ups
                    CommandCenter.getInstance().getOpsList().enqueue(monster, CollisionOp.Operation.ADD);
                }
            }
        }
    }

    // create power ups and add them to monsters and breakable walls
    private void createPowerUps() {
        // set power up counts based on level
        setPowerUpCount();

        // make sure that there are enough sources for power ups
        if ((mPowerUpBombCount + mPowerUpBlastCount - mMonsterSourceCount) > mBreakableWalls.size() || mMonsterSourceCount > mMonsters.size())
            throw new IllegalArgumentException("power up sources exceed available monsters & walls");

        // create power up bombs
        for (int i = 0; i < mPowerUpBombCount; i++) {
            mPowerUps.add(new PowerUpBomb());
        }

        // create power up blasts
        for (int i = 0; i < mPowerUpBlastCount; i++) {
            mPowerUps.add(new PowerUpBlast());
        }

        // shuffle power ups, monsters, and breakable walls so that power ups are randomly assigned
        Collections.shuffle(mPowerUps);
        Collections.shuffle(mMonsters);
        Collections.shuffle(mBreakableWalls);

        // add power ups to monsters
        for (int i = 0; i < mMonsterSourceCount; i++) {
            mMonsters.get(i).setPowerUpInside(mPowerUps.get(0));
            mPowerUps.remove(0);
        }

        // add remaining power ups to breakable walls
        int iRemainingPowerUpCount = mPowerUps.size();
        for (int i = 0; i < iRemainingPowerUpCount; i++) {
            mBreakableWalls.get(i).setPowerUpInside(mPowerUps.get(0));
            mPowerUps.remove(0);
        }

    }

    // set the number of power ups and sources
    private void setPowerUpCount() {
        // set count of Power Ups and how many monsters will be sources
        // breakable walls are source for remaining power ups
        mPowerUpBombCount = 4;
        mPowerUpBlastCount = 4;
        mMonsterSourceCount = 2;  // this should be less than total power ups

        // update power up counts based on level
        if (mLevel == 2){
            mPowerUpBombCount = 4;
            mPowerUpBlastCount = 5;
            mMonsterSourceCount = 3;
        } else if (mLevel == 3) {
            mPowerUpBombCount = 4;
            mPowerUpBlastCount = 4;
            mMonsterSourceCount = 2;
        }
    }

    // create exit -> not revealed until all enemies defeated
    private void createExit() {
        // shuffle list of squares so that exit is randomly assigned
        Collections.shuffle(mSquares);

        // assign exit to first square that has a breakable wall and does NOT contain a power up
        for (Square square : mSquares) {
            if (square.isWall() && !square.isSolidWall() && !((Wall) square.getInside()).containsPowerUp()) {
                    square.addExit();
                    break;
            }
        }
    }

    // ******************************
    //  GAMEBOARD DATA FOR EACH LEVEL
    // ******************************

    // Level 1: get list of maps with data for each square
    private List<Map<Integer, Integer>> getSquareDataMapsLevel1() {
        // each map represents a row
        // each entry contains col# and integer used to assign interior (wall, monster, etc)

        // potential powerup squares 31
        // potential powerup monsters 5

        // create col map for row 0
        Map<Integer, Integer> colIndices0 = new HashMap<>();
        colIndices0.put(0, 1);
        colIndices0.put(1, 1);
        colIndices0.put(2, 1);
        colIndices0.put(3, 1);
        colIndices0.put(4, 1);
        colIndices0.put(5, 1);
        colIndices0.put(6, 1);
        colIndices0.put(7, 1);
        colIndices0.put(8, 1);
        colIndices0.put(9, 1);
        colIndices0.put(10, 1);
        colIndices0.put(11, 1);
        colIndices0.put(12, 1);
        colIndices0.put(13, 1);
        colIndices0.put(14, 1);

        // create col map for row 1
        Map<Integer, Integer> colIndices1 = new HashMap<>();
        colIndices1.put(0, 1);
        colIndices1.put(1, 0);
        colIndices1.put(2, 0);
        colIndices1.put(3, 2);
        colIndices1.put(4, 1);
        colIndices1.put(5, 0);
        colIndices1.put(6, 0);
        colIndices1.put(7, 3);
        colIndices1.put(8, 0);
        colIndices1.put(9, 0);
        colIndices1.put(10, 1);
        colIndices1.put(11, 2);
        colIndices1.put(12, 0);
        colIndices1.put(13, 0);
        colIndices1.put(14, 1);

        // create col map for row 2
        Map<Integer, Integer> colIndices2 = new HashMap<>();
        colIndices2.put(0, 1);
        colIndices2.put(1, 0);
        colIndices2.put(2, 1);
        colIndices2.put(3, 0);
        colIndices2.put(4, 0);
        colIndices2.put(5, 0);
        colIndices2.put(6, 2);
        colIndices2.put(7, 1);
        colIndices2.put(8, 2);
        colIndices2.put(9, 0);
        colIndices2.put(10, 0);
        colIndices2.put(11, 0);
        colIndices2.put(12, 1);
        colIndices2.put(13, 0);
        colIndices2.put(14, 1);

        // create col map for row 3
        Map<Integer, Integer> colIndices3 = new HashMap<>();
        colIndices3.put(0, 1);
        colIndices3.put(1, 2);
        colIndices3.put(2, 2);
        colIndices3.put(3, 0);
        colIndices3.put(4, 1);
        colIndices3.put(5, 1);
        colIndices3.put(6, 0);
        colIndices3.put(7, 1);
        colIndices3.put(8, 0);
        colIndices3.put(9, 1);
        colIndices3.put(10, 1);
        colIndices3.put(11, 0);
        colIndices3.put(12, 2);
        colIndices3.put(13, 2);
        colIndices3.put(14, 1);

        // create col map for row 4
        Map<Integer, Integer> colIndices4 = new HashMap<>();
        colIndices4.put(0, 1);
        colIndices4.put(1, 2);
        colIndices4.put(2, 1);
        colIndices4.put(3, 0);
        colIndices4.put(4, 1);
        colIndices4.put(5, 1);
        colIndices4.put(6, 0);
        colIndices4.put(7, 1);
        colIndices4.put(8, 0);
        colIndices4.put(9, 1);
        colIndices4.put(10, 1);
        colIndices4.put(11, 0);
        colIndices4.put(12, 1);
        colIndices4.put(13, 2);
        colIndices4.put(14, 1);

        // create col map for row 5
        Map<Integer, Integer> colIndices5 = new HashMap<>();
        colIndices5.put(0, 1);
        colIndices5.put(1, 2);
        colIndices5.put(2, 1);
        colIndices5.put(3, 2);
        colIndices5.put(4, 0);
        colIndices5.put(5, 0);
        colIndices5.put(6, 3);
        colIndices5.put(7, 1);
        colIndices5.put(8, 3);
        colIndices5.put(9, 0);
        colIndices5.put(10, 0);
        colIndices5.put(11, 2);
        colIndices5.put(12, 1);
        colIndices5.put(13, 2);
        colIndices5.put(14, 1);

        // create col map for row 6
        Map<Integer, Integer> colIndices6 = new HashMap<>();
        colIndices6.put(0, 1);
        colIndices6.put(1, 2);
        colIndices6.put(2, 1);
        colIndices6.put(3, 0);
        colIndices6.put(4, 1);
        colIndices6.put(5, 1);
        colIndices6.put(6, 0);
        colIndices6.put(7, 1);
        colIndices6.put(8, 0);
        colIndices6.put(9, 1);
        colIndices6.put(10, 1);
        colIndices6.put(11, 0);
        colIndices6.put(12, 1);
        colIndices6.put(13, 2);
        colIndices6.put(14, 1);

        // create col map for row 7
        Map<Integer, Integer> colIndices7 = new HashMap<>();
        colIndices7.put(0, 1);
        colIndices7.put(1, 2);
        colIndices7.put(2, 2);
        colIndices7.put(3, 0);
        colIndices7.put(4, 1);
        colIndices7.put(5, 1);
        colIndices7.put(6, 0);
        colIndices7.put(7, 1);
        colIndices7.put(8, 0);
        colIndices7.put(9, 1);
        colIndices7.put(10, 1);
        colIndices7.put(11, 0);
        colIndices7.put(12, 2);
        colIndices7.put(13, 2);
        colIndices7.put(14, 1);

        // create col map for row 8
        Map<Integer, Integer> colIndices8 = new HashMap<>();
        colIndices8.put(0, 1);
        colIndices8.put(1, 0);
        colIndices8.put(2, 1);
        colIndices8.put(3, 0);
        colIndices8.put(4, 0);
        colIndices8.put(5, 2);
        colIndices8.put(6, 2);
        colIndices8.put(7, 2);
        colIndices8.put(8, 2);
        colIndices8.put(9, 2);
        colIndices8.put(10, 0);
        colIndices8.put(11, 0);
        colIndices8.put(12, 1);
        colIndices8.put(13, 0);
        colIndices8.put(14, 1);

        // create col map for row 9
        Map<Integer, Integer> colIndices9 = new HashMap<>();
        colIndices9.put(0, 1);
        colIndices9.put(1, 0);
        colIndices9.put(2, 1);
        colIndices9.put(3, 1);
        colIndices9.put(4, 1);
        colIndices9.put(5, 1);
        colIndices9.put(6, 1);
        colIndices9.put(7, 2);
        colIndices9.put(8, 1);
        colIndices9.put(9, 1);
        colIndices9.put(10, 1);
        colIndices9.put(11, 1);
        colIndices9.put(12, 1);
        colIndices9.put(13, 0);
        colIndices9.put(14, 1);

        // create col map for row 10
        Map<Integer, Integer> colIndices10 = new HashMap<>();
        colIndices10.put(0, 1);
        colIndices10.put(1, 3);
        colIndices10.put(2, 0);
        colIndices10.put(3, 0);
        colIndices10.put(4, 1);
        colIndices10.put(5, 1);
        colIndices10.put(6, 2);
        colIndices10.put(7, 2);
        colIndices10.put(8, 2);
        colIndices10.put(9, 1);
        colIndices10.put(10, 1);
        colIndices10.put(11, 0);
        colIndices10.put(12, 0);
        colIndices10.put(13, 3);
        colIndices10.put(14, 1);

        // create col map for row 11
        Map<Integer, Integer> colIndices11 = new HashMap<>();
        colIndices11.put(0, 1);
        colIndices11.put(1, 0);
        colIndices11.put(2, 1);
        colIndices11.put(3, 2);
        colIndices11.put(4, 2);
        colIndices11.put(5, 0);
        colIndices11.put(6, 0);
        colIndices11.put(7, 1);
        colIndices11.put(8, 0);
        colIndices11.put(9, 0);
        colIndices11.put(10, 2);
        colIndices11.put(11, 2);
        colIndices11.put(12, 1);
        colIndices11.put(13, 0);
        colIndices11.put(14, 1);

        // create col map for row 12
        Map<Integer, Integer> colIndices12 = new HashMap<>();
        colIndices12.put(0, 1);
        colIndices12.put(1, 1);
        colIndices12.put(2, 1);
        colIndices12.put(3, 1);
        colIndices12.put(4, 1);
        colIndices12.put(5, 1);
        colIndices12.put(6, 1);
        colIndices12.put(7, 1);
        colIndices12.put(8, 1);
        colIndices12.put(9, 1);
        colIndices12.put(10, 1);
        colIndices12.put(11, 1);
        colIndices12.put(12, 1);
        colIndices12.put(13, 1);
        colIndices12.put(14, 1);

        // place all column maps into a list
        List<Map<Integer, Integer>> colIndexMaps = new ArrayList<>();
        colIndexMaps.add(colIndices0);
        colIndexMaps.add(colIndices1);
        colIndexMaps.add(colIndices2);
        colIndexMaps.add(colIndices3);
        colIndexMaps.add(colIndices4);
        colIndexMaps.add(colIndices5);
        colIndexMaps.add(colIndices6);
        colIndexMaps.add(colIndices7);
        colIndexMaps.add(colIndices8);
        colIndexMaps.add(colIndices9);
        colIndexMaps.add(colIndices10);
        colIndexMaps.add(colIndices11);
        colIndexMaps.add(colIndices12);

        return colIndexMaps;
    }

    // Level 2: get list of maps with data for each square
    private List<Map<Integer, Integer>> getSquareDataMapsLevel2() {
        // each map represents a row
        // each entry contains col# and integer used to assign interior (wall, monster, etc)


        // potential powerup squares 31
        // potential powerup monsters 6

        // create col map for row 0
        Map<Integer, Integer> colIndices0 = new HashMap<>();
        colIndices0.put(0, 1);
        colIndices0.put(1, 1);
        colIndices0.put(2, 1);
        colIndices0.put(3, 1);
        colIndices0.put(4, 1);
        colIndices0.put(5, 1);
        colIndices0.put(6, 1);
        colIndices0.put(7, 1);
        colIndices0.put(8, 1);
        colIndices0.put(9, 1);
        colIndices0.put(10, 1);
        colIndices0.put(11, 1);
        colIndices0.put(12, 1);
        colIndices0.put(13, 1);
        colIndices0.put(14, 1);

        // create col map for row 1
        Map<Integer, Integer> colIndices1 = new HashMap<>();
        colIndices1.put(0, 1);
        colIndices1.put(1, 0);
        colIndices1.put(2, 0);
        colIndices1.put(3, 0);
        colIndices1.put(4, 0);
        colIndices1.put(5, 2);
        colIndices1.put(6, 2);
        colIndices1.put(7, 2);
        colIndices1.put(8, 2);
        colIndices1.put(9, 2);
        colIndices1.put(10, 0);
        colIndices1.put(11, 0);
        colIndices1.put(12, 0);
        colIndices1.put(13, 3);
        colIndices1.put(14, 1);

        // create col map for row 2
        Map<Integer, Integer> colIndices2 = new HashMap<>();
        colIndices2.put(0, 1);
        colIndices2.put(1, 0);
        colIndices2.put(2, 1);
        colIndices2.put(3, 1);
        colIndices2.put(4, 1);
        colIndices2.put(5, 0);
        colIndices2.put(6, 1);
        colIndices2.put(7, 0);
        colIndices2.put(8, 1);
        colIndices2.put(9, 0);
        colIndices2.put(10, 1);
        colIndices2.put(11, 1);
        colIndices2.put(12, 1);
        colIndices2.put(13, 0);
        colIndices2.put(14, 1);

        // create col map for row 3
        Map<Integer, Integer> colIndices3 = new HashMap<>();
        colIndices3.put(0, 1);
        colIndices3.put(1, 1);
        colIndices3.put(2, 1);
        colIndices3.put(3, 1);
        colIndices3.put(4, 1);
        colIndices3.put(5, 0);
        colIndices3.put(6, 1);
        colIndices3.put(7, 0);
        colIndices3.put(8, 1);
        colIndices3.put(9, 0);
        colIndices3.put(10, 1);
        colIndices3.put(11, 1);
        colIndices3.put(12, 1);
        colIndices3.put(13, 0);
        colIndices3.put(14, 1);

        // create col map for row 4
        Map<Integer, Integer> colIndices4 = new HashMap<>();
        colIndices4.put(0, 1);
        colIndices4.put(1, 0);
        colIndices4.put(2, 0);
        colIndices4.put(3, 3);
        colIndices4.put(4, 0);
        colIndices4.put(5, 0);
        colIndices4.put(6, 1);
        colIndices4.put(7, 3);
        colIndices4.put(8, 1);
        colIndices4.put(9, 0);
        colIndices4.put(10, 0);
        colIndices4.put(11, 0);
        colIndices4.put(12, 0);
        colIndices4.put(13, 0);
        colIndices4.put(14, 1);

        // create col map for row 5
        Map<Integer, Integer> colIndices5 = new HashMap<>();
        colIndices5.put(0, 1);
        colIndices5.put(1, 2);
        colIndices5.put(2, 1);
        colIndices5.put(3, 1);
        colIndices5.put(4, 1);
        colIndices5.put(5, 2);
        colIndices5.put(6, 1);
        colIndices5.put(7, 2);
        colIndices5.put(8, 1);
        colIndices5.put(9, 2);
        colIndices5.put(10, 1);
        colIndices5.put(11, 1);
        colIndices5.put(12, 1);
        colIndices5.put(13, 2);
        colIndices5.put(14, 1);

        // create col map for row 6
        Map<Integer, Integer> colIndices6 = new HashMap<>();
        colIndices6.put(0, 1);
        colIndices6.put(1, 2);
        colIndices6.put(2, 0);
        colIndices6.put(3, 0);
        colIndices6.put(4, 0);
        colIndices6.put(5, 2);
        colIndices6.put(6, 2);
        colIndices6.put(7, 2);
        colIndices6.put(8, 2);
        colIndices6.put(9, 2);
        colIndices6.put(10, 0);
        colIndices6.put(11, 0);
        colIndices6.put(12, 0);
        colIndices6.put(13, 2);
        colIndices6.put(14, 1);

        // create col map for row 7
        Map<Integer, Integer> colIndices7 = new HashMap<>();
        colIndices7.put(0, 1);
        colIndices7.put(1, 2);
        colIndices7.put(2, 1);
        colIndices7.put(3, 1);
        colIndices7.put(4, 1);
        colIndices7.put(5, 2);
        colIndices7.put(6, 1);
        colIndices7.put(7, 2);
        colIndices7.put(8, 1);
        colIndices7.put(9, 2);
        colIndices7.put(10, 1);
        colIndices7.put(11, 1);
        colIndices7.put(12, 1);
        colIndices7.put(13, 2);
        colIndices7.put(14, 1);

        // create col map for row 8
        Map<Integer, Integer> colIndices8 = new HashMap<>();
        colIndices8.put(0, 1);
        colIndices8.put(1, 0);
        colIndices8.put(2, 0);
        colIndices8.put(3, 3);
        colIndices8.put(4, 0);
        colIndices8.put(5, 0);
        colIndices8.put(6, 1);
        colIndices8.put(7, 0);
        colIndices8.put(8, 1);
        colIndices8.put(9, 0);
        colIndices8.put(10, 0);
        colIndices8.put(11, 3);
        colIndices8.put(12, 0);
        colIndices8.put(13, 0);
        colIndices8.put(14, 1);

        // create col map for row 9
        Map<Integer, Integer> colIndices9 = new HashMap<>();
        colIndices9.put(0, 1);
        colIndices9.put(1, 0);
        colIndices9.put(2, 1);
        colIndices9.put(3, 1);
        colIndices9.put(4, 1);
        colIndices9.put(5, 2);
        colIndices9.put(6, 1);
        colIndices9.put(7, 0);
        colIndices9.put(8, 1);
        colIndices9.put(9, 2);
        colIndices9.put(10, 1);
        colIndices9.put(11, 1);
        colIndices9.put(12, 1);
        colIndices9.put(13, 0);
        colIndices9.put(14, 1);

        // create col map for row 10
        Map<Integer, Integer> colIndices10 = new HashMap<>();
        colIndices10.put(0, 1);
        colIndices10.put(1, 2);
        colIndices10.put(2, 1);
        colIndices10.put(3, 1);
        colIndices10.put(4, 1);
        colIndices10.put(5, 2);
        colIndices10.put(6, 1);
        colIndices10.put(7, 2);
        colIndices10.put(8, 1);
        colIndices10.put(9, 2);
        colIndices10.put(10, 1);
        colIndices10.put(11, 1);
        colIndices10.put(12, 1);
        colIndices10.put(13, 2);
        colIndices10.put(14, 1);

        // create col map for row 11
        Map<Integer, Integer> colIndices11 = new HashMap<>();
        colIndices11.put(0, 1);
        colIndices11.put(1, 2);
        colIndices11.put(2, 2);
        colIndices11.put(3, 0);
        colIndices11.put(4, 0);
        colIndices11.put(5, 0);
        colIndices11.put(6, 0);
        colIndices11.put(7, 3);
        colIndices11.put(8, 0);
        colIndices11.put(9, 0);
        colIndices11.put(10, 0);
        colIndices11.put(11, 0);
        colIndices11.put(12, 2);
        colIndices11.put(13, 2);
        colIndices11.put(14, 1);

        // create col map for row 12
        Map<Integer, Integer> colIndices12 = new HashMap<>();
        colIndices12.put(0, 1);
        colIndices12.put(1, 1);
        colIndices12.put(2, 1);
        colIndices12.put(3, 1);
        colIndices12.put(4, 1);
        colIndices12.put(5, 1);
        colIndices12.put(6, 1);
        colIndices12.put(7, 1);
        colIndices12.put(8, 1);
        colIndices12.put(9, 1);
        colIndices12.put(10, 1);
        colIndices12.put(11, 1);
        colIndices12.put(12, 1);
        colIndices12.put(13, 1);
        colIndices12.put(14, 1);

        // place all column maps into a list
        List<Map<Integer, Integer>> colIndexMaps = new ArrayList<>();
        colIndexMaps.add(colIndices0);
        colIndexMaps.add(colIndices1);
        colIndexMaps.add(colIndices2);
        colIndexMaps.add(colIndices3);
        colIndexMaps.add(colIndices4);
        colIndexMaps.add(colIndices5);
        colIndexMaps.add(colIndices6);
        colIndexMaps.add(colIndices7);
        colIndexMaps.add(colIndices8);
        colIndexMaps.add(colIndices9);
        colIndexMaps.add(colIndices10);
        colIndexMaps.add(colIndices11);
        colIndexMaps.add(colIndices12);

        return colIndexMaps;
    }

}
