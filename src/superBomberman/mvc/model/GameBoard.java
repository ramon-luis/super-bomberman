package superBomberman.mvc.model;

import java.util.*;

import superBomberman.mvc.model.Wall.WallType;

/**
 * GameBoard object contains grid of squares
 * Squares can comtain various movable objects such as walls, enemies, bombs, etc.
 * A new GameBoard is created for each level
 * game board consists of 143 playable squares (13 wide x 11 tall) and an exterior wall (0 & 14 for x values, 0 & 12 for y values)
 */

public class GameBoard {

    // ===============================================
    // FIELDS
    // ===============================================

    // constants for size of game board
    public static final int ROW_COUNT = 13;
    public static final int COL_COUNT = 15;

    // private instance members
    private List<Square> mSquares;
    private List<PowerUp> mPowerUps;
    private List<Enemy> mEnemies;
    private List<Wall> mBreakableWalls;

    private int mLevel;
    private int mPowerUpBombCount;  // # of powerUp bombs
    private int mPowerUpBlastCount;  // # of powerUp blasts
    private int mPowerUpKickCount;  // # of powerUp kicks
    private int mEnemyPowerUpSourceCount;  // # of enemies used as source for power up

    // ===============================================
    // CONSTRUCTOR
    // ===============================================
    public GameBoard(int level) {
        // assign level
        mLevel = level;

        // initialize lists
        mSquares = new ArrayList<>(ROW_COUNT * COL_COUNT);
        mPowerUps = new ArrayList<>();
        mEnemies = new ArrayList<>();
        mBreakableWalls = new ArrayList<>(ROW_COUNT * COL_COUNT);

        // create the game board
        createLevel();  // squares, walls, and monsters
        createPowerUps();  // assigned to breakable walls & monsters
        createExit();  // assigned to random square with breakable wall (and no power up)
    }

    // ===============================================
    // METHODS
    // ===============================================

    public int getLevel() {
        return mLevel;
    }

    public List<PowerUp> getPowerUps() {
        return mPowerUps;
    }

    public List<Enemy> getEnemies() {
        return mEnemies;
    }

    public List<Wall> getBreakableWalls() {
        return mBreakableWalls;
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


    // ===============================================
    // HELPER METHODS
    // ===============================================

    // create level (squares, walls, and monsters)
    private void createLevel() {
        // define list of data maps for each square -> default is Level 1
        List<Map<Integer, Integer>> squareDataMaps = getSquareDataMapsLevel1();

        // update the square data based on level
        if (mLevel == 2)
            squareDataMaps = getSquareDataMapsLevel2();
        else if (mLevel == 3)
            squareDataMaps = getSquareDataMapsLevel3();
        else if (mLevel == 4)
            squareDataMaps = getSquareDataMapsLevel4();
        else if (mLevel == 5)
            squareDataMaps = getSquareDataMapsLevel5();
        else if (mLevel == 6)
            squareDataMaps = getSquareDataMapsLevel6();
        else if (mLevel == 7)
            squareDataMaps = getSquareDataMapsLevel7();

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
                    // create soldier, add to OpsList
                    Soldier soldier = new Soldier();
                    soldier.setCenterFromSquare(square);
                    mEnemies.add(soldier);  // list is used for adding power ups
                    CommandCenter.getInstance().getOpsList().enqueue(soldier, CollisionOp.Operation.ADD);
                } else if (entry.getValue() == 4) {
                    // create alien, add to OpsList
                    Alien alien = new Alien();
                    alien.setCenterFromSquare(square);
                    mEnemies.add(alien);  // list is used for adding power ups
                    CommandCenter.getInstance().getOpsList().enqueue(alien, CollisionOp.Operation.ADD);
                } else if (entry.getValue() == 5) {
                    // create drone, add to OpsList
                    Drone drone = new Drone();
                    drone.setCenterFromSquare(square);
                    mEnemies.add(drone);  // list is used for adding power ups
                    CommandCenter.getInstance().getOpsList().enqueue(drone, CollisionOp.Operation.ADD);
                }




            }
        }
    }

    // create power ups and add them to monsters and breakable walls
    private void createPowerUps() {
        // set power up counts based on level
        setPowerUpCount();

        // make sure that there are enough sources for power ups
        if ((mPowerUpBombCount + mPowerUpBlastCount - mEnemyPowerUpSourceCount) > mBreakableWalls.size() || mEnemyPowerUpSourceCount > mEnemies.size())
            throw new IllegalArgumentException("power up sources exceed available enemies & walls");

        // create power up bombs
        for (int i = 0; i < mPowerUpBombCount; i++) {
            mPowerUps.add(new PowerUpBomb());
        }

        // create power up blasts
        for (int i = 0; i < mPowerUpBlastCount; i++) {
            mPowerUps.add(new PowerUpBlast());
        }

        // create power up blasts
        for (int i = 0; i < mPowerUpKickCount; i++) {
            mPowerUps.add(new PowerUpKick());
        }

        // shuffle power ups, monsters, and breakable walls so that power ups are randomly assigned
        Collections.shuffle(mPowerUps);
        Collections.shuffle(mEnemies);
        Collections.shuffle(mBreakableWalls);

        // add power ups to monsters
        for (int i = 0; i < mEnemyPowerUpSourceCount; i++) {
            mEnemies.get(i).setPowerUpInside(mPowerUps.get(0));
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
        mPowerUpBombCount = 3;
        mPowerUpBlastCount = 4;
        mEnemyPowerUpSourceCount = 2;  // this should be less than total power ups

        // update power up counts based on level
        if (mLevel == 2){
            mPowerUpBombCount = 4;
            mPowerUpBlastCount = 5;
            mEnemyPowerUpSourceCount = 3;
        } else if (mLevel == 3) {
            mPowerUpBombCount = 5;
            mPowerUpBlastCount = 5;
            mPowerUpKickCount = 1;
            mEnemyPowerUpSourceCount = 3;
        } else if (mLevel == 4) {
            mPowerUpBombCount = 6;
            mPowerUpBlastCount = 6;
            mPowerUpKickCount = 1;
            mEnemyPowerUpSourceCount = 4;
        } else if (mLevel == 5) {
            mPowerUpBombCount = 6;
            mPowerUpBlastCount = 8;
            mPowerUpKickCount = 2;
            mEnemyPowerUpSourceCount = 5;
        } else if (mLevel == 6) {
            mPowerUpBombCount = 7;
            mPowerUpBlastCount = 10;
            mPowerUpKickCount = 2;
            mEnemyPowerUpSourceCount = 5;
        } else if (mLevel == 7) {
            mPowerUpBombCount = 10;
            mPowerUpBlastCount = 15;
            mPowerUpKickCount = 4;
            mEnemyPowerUpSourceCount = 7;
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

    // ===============================================
    // GAMEBOARD DATA FOR EACH LEVEL
    // ===============================================

    // Each col is mapped by row -> the entire map represents cols for a specific row
    // The map contains col index + inside of square based on code below:
        // 0 == EMPTY
        // 1 == SOLID WALL
        // 2 == BREAKABLE WALL
        // 3 == SOLDIER
        // 4 == ALIEN
        // 5 == DRONE


    // Level 1: get list of maps with data for each square
    private List<Map<Integer, Integer>> getSquareDataMapsLevel1() {
        // each map represents a row
        // each entry contains col# and integer used to assign interior (wall, monster, etc)

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
        colIndices8.put(1, 2);
        colIndices8.put(2, 1);
        colIndices8.put(3, 0);
        colIndices8.put(4, 2);
        colIndices8.put(5, 2);
        colIndices8.put(6, 0);
        colIndices8.put(7, 2);
        colIndices8.put(8, 0);
        colIndices8.put(9, 2);
        colIndices8.put(10, 2);
        colIndices8.put(11, 0);
        colIndices8.put(12, 1);
        colIndices8.put(13, 2);
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
        colIndices10.put(2, 1);
        colIndices10.put(3, 0);
        colIndices10.put(4, 1);
        colIndices10.put(5, 1);
        colIndices10.put(6, 2);
        colIndices10.put(7, 2);
        colIndices10.put(8, 2);
        colIndices10.put(9, 1);
        colIndices10.put(10, 1);
        colIndices10.put(11, 0);
        colIndices10.put(12, 1);
        colIndices10.put(13, 3);
        colIndices10.put(14, 1);

        // create col map for row 11
        Map<Integer, Integer> colIndices11 = new HashMap<>();
        colIndices11.put(0, 1);
        colIndices11.put(1, 0);
        colIndices11.put(2, 0);
        colIndices11.put(3, 0);
        colIndices11.put(4, 2);
        colIndices11.put(5, 0);
        colIndices11.put(6, 0);
        colIndices11.put(7, 1);
        colIndices11.put(8, 0);
        colIndices11.put(9, 0);
        colIndices11.put(10, 2);
        colIndices11.put(11, 0);
        colIndices11.put(12, 0);
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
        colIndices1.put(4, 2);
        colIndices1.put(5, 2);
        colIndices1.put(6, 2);
        colIndices1.put(7, 0);
        colIndices1.put(8, 2);
        colIndices1.put(9, 2);
        colIndices1.put(10, 2);
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
        colIndices6.put(5, 0);
        colIndices6.put(6, 0);
        colIndices6.put(7, 2);
        colIndices6.put(8, 0);
        colIndices6.put(9, 0);
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
        colIndices8.put(5, 2);
        colIndices8.put(6, 1);
        colIndices8.put(7, 0);
        colIndices8.put(8, 1);
        colIndices8.put(9, 2);
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


    // Level 3: get list of maps with data for each square
    private List<Map<Integer, Integer>> getSquareDataMapsLevel3() {
        // each map represents a row
        // each entry contains col# and integer used to assign interior (wall, monster, etc)


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
        colIndices1.put(4, 2);
        colIndices1.put(5, 0);
        colIndices1.put(6, 0);
        colIndices1.put(7, 2);
        colIndices1.put(8, 0);
        colIndices1.put(9, 0);
        colIndices1.put(10, 2);
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
        colIndices2.put(4, 2);
        colIndices2.put(5, 1);
        colIndices2.put(6, 0);
        colIndices2.put(7, 1);
        colIndices2.put(8, 0);
        colIndices2.put(9, 1);
        colIndices2.put(10, 2);
        colIndices2.put(11, 1);
        colIndices2.put(12, 1);
        colIndices2.put(13, 0);
        colIndices2.put(14, 1);

        // create col map for row 3
        Map<Integer, Integer> colIndices3 = new HashMap<>();
        colIndices3.put(0, 1);
        colIndices3.put(1, 0);
        colIndices3.put(2, 0);
        colIndices3.put(3, 2);
        colIndices3.put(4, 1);
        colIndices3.put(5, 1);
        colIndices3.put(6, 0);
        colIndices3.put(7, 2);
        colIndices3.put(8, 0);
        colIndices3.put(9, 1);
        colIndices3.put(10, 1);
        colIndices3.put(11, 2);
        colIndices3.put(12, 0);
        colIndices3.put(13, 0);
        colIndices3.put(14, 1);

        // create col map for row 4
        Map<Integer, Integer> colIndices4 = new HashMap<>();
        colIndices4.put(0, 1);
        colIndices4.put(1, 1);
        colIndices4.put(2, 1);
        colIndices4.put(3, 2);
        colIndices4.put(4, 2);
        colIndices4.put(5, 2);
        colIndices4.put(6, 0);
        colIndices4.put(7, 1);
        colIndices4.put(8, 0);
        colIndices4.put(9, 2);
        colIndices4.put(10, 2);
        colIndices4.put(11, 2);
        colIndices4.put(12, 1);
        colIndices4.put(13, 1);
        colIndices4.put(14, 1);

        // create col map for row 5
        Map<Integer, Integer> colIndices5 = new HashMap<>();
        colIndices5.put(0, 1);
        colIndices5.put(1, 0);
        colIndices5.put(2, 1);
        colIndices5.put(3, 1);
        colIndices5.put(4, 1);
        colIndices5.put(5, 1);
        colIndices5.put(6, 0);
        colIndices5.put(7, 4);
        colIndices5.put(8, 0);
        colIndices5.put(9, 1);
        colIndices5.put(10, 1);
        colIndices5.put(11, 1);
        colIndices5.put(12, 1);
        colIndices5.put(13, 0);
        colIndices5.put(14, 1);

        // create col map for row 6
        Map<Integer, Integer> colIndices6 = new HashMap<>();
        colIndices6.put(0, 1);
        colIndices6.put(1, 0);
        colIndices6.put(2, 3);
        colIndices6.put(3, 0);
        colIndices6.put(4, 1);
        colIndices6.put(5, 1);
        colIndices6.put(6, 2);
        colIndices6.put(7, 1);
        colIndices6.put(8, 2);
        colIndices6.put(9, 1);
        colIndices6.put(10, 1);
        colIndices6.put(11, 0);
        colIndices6.put(12, 3);
        colIndices6.put(13, 0);
        colIndices6.put(14, 1);

        // create col map for row 7
        Map<Integer, Integer> colIndices7 = new HashMap<>();
        colIndices7.put(0, 1);
        colIndices7.put(1, 0);
        colIndices7.put(2, 1);
        colIndices7.put(3, 0);
        colIndices7.put(4, 0);
        colIndices7.put(5, 2);
        colIndices7.put(6, 0);
        colIndices7.put(7, 3);
        colIndices7.put(8, 0);
        colIndices7.put(9, 2);
        colIndices7.put(10, 0);
        colIndices7.put(11, 0);
        colIndices7.put(12, 1);
        colIndices7.put(13, 0);
        colIndices7.put(14, 1);

        // create col map for row 8
        Map<Integer, Integer> colIndices8 = new HashMap<>();
        colIndices8.put(0, 1);
        colIndices8.put(1, 2);
        colIndices8.put(2, 2);
        colIndices8.put(3, 1);
        colIndices8.put(4, 1);
        colIndices8.put(5, 1);
        colIndices8.put(6, 0);
        colIndices8.put(7, 1);
        colIndices8.put(8, 0);
        colIndices8.put(9, 1);
        colIndices8.put(10, 1);
        colIndices8.put(11, 1);
        colIndices8.put(12, 2);
        colIndices8.put(13, 2);
        colIndices8.put(14, 1);

        // create col map for row 9
        Map<Integer, Integer> colIndices9 = new HashMap<>();
        colIndices9.put(0, 1);
        colIndices9.put(1, 1);
        colIndices9.put(2, 0);
        colIndices9.put(3, 0);
        colIndices9.put(4, 2);
        colIndices9.put(5, 2);
        colIndices9.put(6, 0);
        colIndices9.put(7, 0);
        colIndices9.put(8, 0);
        colIndices9.put(9, 2);
        colIndices9.put(10, 2);
        colIndices9.put(11, 0);
        colIndices9.put(12, 0);
        colIndices9.put(13, 1);
        colIndices9.put(14, 1);

        // create col map for row 10
        Map<Integer, Integer> colIndices10 = new HashMap<>();
        colIndices10.put(0, 1);
        colIndices10.put(1, 1);
        colIndices10.put(2, 0);
        colIndices10.put(3, 1);
        colIndices10.put(4, 2);
        colIndices10.put(5, 1);
        colIndices10.put(6, 1);
        colIndices10.put(7, 2);
        colIndices10.put(8, 1);
        colIndices10.put(9, 1);
        colIndices10.put(10, 1);
        colIndices10.put(11, 2);
        colIndices10.put(12, 1);
        colIndices10.put(13, 1);
        colIndices10.put(14, 1);

        // create col map for row 11
        Map<Integer, Integer> colIndices11 = new HashMap<>();
        colIndices11.put(0, 1);
        colIndices11.put(1, 3);
        colIndices11.put(2, 0);
        colIndices11.put(3, 0);
        colIndices11.put(4, 0);
        colIndices11.put(5, 0);
        colIndices11.put(6, 2);
        colIndices11.put(7, 2);
        colIndices11.put(8, 2);
        colIndices11.put(9, 0);
        colIndices11.put(10, 0);
        colIndices11.put(11, 0);
        colIndices11.put(12, 0);
        colIndices11.put(13, 3);
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


    // Level 4: get list of maps with data for each square
    private List<Map<Integer, Integer>> getSquareDataMapsLevel4() {
        // each map represents a row
        // each entry contains col# and integer used to assign interior (wall, monster, etc)


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
        colIndices1.put(4, 2);
        colIndices1.put(5, 2);
        colIndices1.put(6, 0);
        colIndices1.put(7, 2);
        colIndices1.put(8, 0);
        colIndices1.put(9, 2);
        colIndices1.put(10, 2);
        colIndices1.put(11, 0);
        colIndices1.put(12, 0);
        colIndices1.put(13, 0);
        colIndices1.put(14, 1);

        // create col map for row 2
        Map<Integer, Integer> colIndices2 = new HashMap<>();
        colIndices2.put(0, 1);
        colIndices2.put(1, 1);
        colIndices2.put(2, 1);
        colIndices2.put(3, 1);
        colIndices2.put(4, 2);
        colIndices2.put(5, 1);
        colIndices2.put(6, 0);
        colIndices2.put(7, 1);
        colIndices2.put(8, 0);
        colIndices2.put(9, 1);
        colIndices2.put(10, 2);
        colIndices2.put(11, 1);
        colIndices2.put(12, 1);
        colIndices2.put(13, 0);
        colIndices2.put(14, 1);

        // create col map for row 3
        Map<Integer, Integer> colIndices3 = new HashMap<>();
        colIndices3.put(0, 1);
        colIndices3.put(1, 0);
        colIndices3.put(2, 2);
        colIndices3.put(3, 3);
        colIndices3.put(4, 0);
        colIndices3.put(5, 0);
        colIndices3.put(6, 0);
        colIndices3.put(7, 2);
        colIndices3.put(8, 0);
        colIndices3.put(9, 0);
        colIndices3.put(10, 0);
        colIndices3.put(11, 4);
        colIndices3.put(12, 2);
        colIndices3.put(13, 0);
        colIndices3.put(14, 1);

        // create col map for row 4
        Map<Integer, Integer> colIndices4 = new HashMap<>();
        colIndices4.put(0, 1);
        colIndices4.put(1, 0);
        colIndices4.put(2, 1);
        colIndices4.put(3, 1);
        colIndices4.put(4, 1);
        colIndices4.put(5, 2);
        colIndices4.put(6, 1);
        colIndices4.put(7, 0);
        colIndices4.put(8, 1);
        colIndices4.put(9, 2);
        colIndices4.put(10, 1);
        colIndices4.put(11, 1);
        colIndices4.put(12, 1);
        colIndices4.put(13, 0);
        colIndices4.put(14, 1);

        // create col map for row 5
        Map<Integer, Integer> colIndices5 = new HashMap<>();
        colIndices5.put(0, 1);
        colIndices5.put(1, 3);
        colIndices5.put(2, 0);
        colIndices5.put(3, 2);
        colIndices5.put(4, 0);
        colIndices5.put(5, 2);
        colIndices5.put(6, 2);
        colIndices5.put(7, 0);
        colIndices5.put(8, 2);
        colIndices5.put(9, 2);
        colIndices5.put(10, 0);
        colIndices5.put(11, 2);
        colIndices5.put(12, 0);
        colIndices5.put(13, 3);
        colIndices5.put(14, 1);

        // create col map for row 6
        Map<Integer, Integer> colIndices6 = new HashMap<>();
        colIndices6.put(0, 1);
        colIndices6.put(1, 1);
        colIndices6.put(2, 1);
        colIndices6.put(3, 1);
        colIndices6.put(4, 0);
        colIndices6.put(5, 2);
        colIndices6.put(6, 1);
        colIndices6.put(7, 2);
        colIndices6.put(8, 1);
        colIndices6.put(9, 2);
        colIndices6.put(10, 0);
        colIndices6.put(11, 1);
        colIndices6.put(12, 1);
        colIndices6.put(13, 1);
        colIndices6.put(14, 1);

        // create col map for row 7
        Map<Integer, Integer> colIndices7 = new HashMap<>();
        colIndices7.put(0, 1);
        colIndices7.put(1, 3);
        colIndices7.put(2, 0);
        colIndices7.put(3, 0);
        colIndices7.put(4, 0);
        colIndices7.put(5, 1);
        colIndices7.put(6, 1);
        colIndices7.put(7, 0);
        colIndices7.put(8, 1);
        colIndices7.put(9, 1);
        colIndices7.put(10, 0);
        colIndices7.put(11, 0);
        colIndices7.put(12, 0);
        colIndices7.put(13, 3);
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
        colIndices8.put(7, 0);
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
        colIndices9.put(1, 2);
        colIndices9.put(2, 0);
        colIndices9.put(3, 0);
        colIndices9.put(4, 1);
        colIndices9.put(5, 2);
        colIndices9.put(6, 1);
        colIndices9.put(7, 0);
        colIndices9.put(8, 1);
        colIndices9.put(9, 2);
        colIndices9.put(10, 1);
        colIndices9.put(11, 0);
        colIndices9.put(12, 0);
        colIndices9.put(13, 2);
        colIndices9.put(14, 1);

        // create col map for row 10
        Map<Integer, Integer> colIndices10 = new HashMap<>();
        colIndices10.put(0, 1);
        colIndices10.put(1, 0);
        colIndices10.put(2, 1);
        colIndices10.put(3, 1);
        colIndices10.put(4, 1);
        colIndices10.put(5, 0);
        colIndices10.put(6, 0);
        colIndices10.put(7, 2);
        colIndices10.put(8, 0);
        colIndices10.put(9, 0);
        colIndices10.put(10, 1);
        colIndices10.put(11, 1);
        colIndices10.put(12, 1);
        colIndices10.put(13, 0);
        colIndices10.put(14, 1);

        // create col map for row 11
        Map<Integer, Integer> colIndices11 = new HashMap<>();
        colIndices11.put(0, 1);
        colIndices11.put(1, 4);
        colIndices11.put(2, 0);
        colIndices11.put(3, 0);
        colIndices11.put(4, 2);
        colIndices11.put(5, 0);
        colIndices11.put(6, 1);
        colIndices11.put(7, 2);
        colIndices11.put(8, 1);
        colIndices11.put(9, 0);
        colIndices11.put(10, 2);
        colIndices11.put(11, 0);
        colIndices11.put(12, 0);
        colIndices11.put(13, 3);
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

    // Level 5: get list of maps with data for each square
    private List<Map<Integer, Integer>> getSquareDataMapsLevel5() {
        // each map represents a row
        // each entry contains col# and integer used to assign interior (wall, monster, etc)


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
        colIndices1.put(4, 2);
        colIndices1.put(5, 0);
        colIndices1.put(6, 0);
        colIndices1.put(7, 2);
        colIndices1.put(8, 0);
        colIndices1.put(9, 0);
        colIndices1.put(10, 2);
        colIndices1.put(11, 2);
        colIndices1.put(12, 2);
        colIndices1.put(13, 3);
        colIndices1.put(14, 1);

        // create col map for row 2
        Map<Integer, Integer> colIndices2 = new HashMap<>();
        colIndices2.put(0, 1);
        colIndices2.put(1, 0);
        colIndices2.put(2, 1);
        colIndices2.put(3, 0);
        colIndices2.put(4, 1);
        colIndices2.put(5, 4);
        colIndices2.put(6, 1);
        colIndices2.put(7, 2);
        colIndices2.put(8, 1);
        colIndices2.put(9, 4);
        colIndices2.put(10, 1);
        colIndices2.put(11, 0);
        colIndices2.put(12, 1);
        colIndices2.put(13, 0);
        colIndices2.put(14, 1);

        // create col map for row 3
        Map<Integer, Integer> colIndices3 = new HashMap<>();
        colIndices3.put(0, 1);
        colIndices3.put(1, 0);
        colIndices3.put(2, 1);
        colIndices3.put(3, 0);
        colIndices3.put(4, 1);
        colIndices3.put(5, 0);
        colIndices3.put(6, 1);
        colIndices3.put(7, 2);
        colIndices3.put(8, 1);
        colIndices3.put(9, 0);
        colIndices3.put(10, 1);
        colIndices3.put(11, 0);
        colIndices3.put(12, 1);
        colIndices3.put(13, 0);
        colIndices3.put(14, 1);

        // create col map for row 4
        Map<Integer, Integer> colIndices4 = new HashMap<>();
        colIndices4.put(0, 1);
        colIndices4.put(1, 2);
        colIndices4.put(2, 0);
        colIndices4.put(3, 0);
        colIndices4.put(4, 2);
        colIndices4.put(5, 0);
        colIndices4.put(6, 0);
        colIndices4.put(7, 2);
        colIndices4.put(8, 0);
        colIndices4.put(9, 0);
        colIndices4.put(10, 2);
        colIndices4.put(11, 0);
        colIndices4.put(12, 0);
        colIndices4.put(13, 2);
        colIndices4.put(14, 1);

        // create col map for row 5
        Map<Integer, Integer> colIndices5 = new HashMap<>();
        colIndices5.put(0, 1);
        colIndices5.put(1, 2);
        colIndices5.put(2, 1);
        colIndices5.put(3, 1);
        colIndices5.put(4, 2);
        colIndices5.put(5, 1);
        colIndices5.put(6, 1);
        colIndices5.put(7, 0);
        colIndices5.put(8, 1);
        colIndices5.put(9, 1);
        colIndices5.put(10, 2);
        colIndices5.put(11, 1);
        colIndices5.put(12, 1);
        colIndices5.put(13, 2);
        colIndices5.put(14, 1);

        // create col map for row 6
        Map<Integer, Integer> colIndices6 = new HashMap<>();
        colIndices6.put(0, 1);
        colIndices6.put(1, 0);
        colIndices6.put(2, 2);
        colIndices6.put(3, 2);
        colIndices6.put(4, 2);
        colIndices6.put(5, 0);
        colIndices6.put(6, 0);
        colIndices6.put(7, 5);
        colIndices6.put(8, 0);
        colIndices6.put(9, 0);
        colIndices6.put(10, 2);
        colIndices6.put(11, 2);
        colIndices6.put(12, 2);
        colIndices6.put(13, 0);
        colIndices6.put(14, 1);

        // create col map for row 7
        Map<Integer, Integer> colIndices7 = new HashMap<>();
        colIndices7.put(0, 1);
        colIndices7.put(1, 0);
        colIndices7.put(2, 1);
        colIndices7.put(3, 0);
        colIndices7.put(4, 1);
        colIndices7.put(5, 2);
        colIndices7.put(6, 1);
        colIndices7.put(7, 1);
        colIndices7.put(8, 1);
        colIndices7.put(9, 2);
        colIndices7.put(10, 1);
        colIndices7.put(11, 0);
        colIndices7.put(12, 1);
        colIndices7.put(13, 0);
        colIndices7.put(14, 1);

        // create col map for row 8
        Map<Integer, Integer> colIndices8 = new HashMap<>();
        colIndices8.put(0, 1);
        colIndices8.put(1, 0);
        colIndices8.put(2, 1);
        colIndices8.put(3, 0);
        colIndices8.put(4, 1);
        colIndices8.put(5, 0);
        colIndices8.put(6, 1);
        colIndices8.put(7, 0);
        colIndices8.put(8, 1);
        colIndices8.put(9, 0);
        colIndices8.put(10, 1);
        colIndices8.put(11, 0);
        colIndices8.put(12, 1);
        colIndices8.put(13, 0);
        colIndices8.put(14, 1);

        // create col map for row 9
        Map<Integer, Integer> colIndices9 = new HashMap<>();
        colIndices9.put(0, 1);
        colIndices9.put(1, 2);
        colIndices9.put(2, 0);
        colIndices9.put(3, 3);
        colIndices9.put(4, 2);
        colIndices9.put(5, 0);
        colIndices9.put(6, 0);
        colIndices9.put(7, 4);
        colIndices9.put(8, 0);
        colIndices9.put(9, 0);
        colIndices9.put(10, 2);
        colIndices9.put(11, 3);
        colIndices9.put(12, 0);
        colIndices9.put(13, 2);
        colIndices9.put(14, 1);

        // create col map for row 10
        Map<Integer, Integer> colIndices10 = new HashMap<>();
        colIndices10.put(0, 1);
        colIndices10.put(1, 0);
        colIndices10.put(2, 1);
        colIndices10.put(3, 1);
        colIndices10.put(4, 2);
        colIndices10.put(5, 1);
        colIndices10.put(6, 1);
        colIndices10.put(7, 2);
        colIndices10.put(8, 1);
        colIndices10.put(9, 1);
        colIndices10.put(10, 2);
        colIndices10.put(11, 1);
        colIndices10.put(12, 1);
        colIndices10.put(13, 0);
        colIndices10.put(14, 1);

        // create col map for row 11
        Map<Integer, Integer> colIndices11 = new HashMap<>();
        colIndices11.put(0, 1);
        colIndices11.put(1, 3);
        colIndices11.put(2, 0);
        colIndices11.put(3, 0);
        colIndices11.put(4, 2);
        colIndices11.put(5, 2);
        colIndices11.put(6, 2);
        colIndices11.put(7, 2);
        colIndices11.put(8, 2);
        colIndices11.put(9, 2);
        colIndices11.put(10, 2);
        colIndices11.put(11, 0);
        colIndices11.put(12, 0);
        colIndices11.put(13, 3);
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

    // Level 6: get list of maps with data for each square
    private List<Map<Integer, Integer>> getSquareDataMapsLevel6() {
        // each map represents a row
        // each entry contains col# and integer used to assign interior (wall, monster, etc)


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
        colIndices1.put(4, 2);
        colIndices1.put(5, 0);
        colIndices1.put(6, 0);
        colIndices1.put(7, 2);
        colIndices1.put(8, 0);
        colIndices1.put(9, 0);
        colIndices1.put(10, 0);
        colIndices1.put(11, 2);
        colIndices1.put(12, 0);
        colIndices1.put(13, 0);
        colIndices1.put(14, 1);

        // create col map for row 2
        Map<Integer, Integer> colIndices2 = new HashMap<>();
        colIndices2.put(0, 1);
        colIndices2.put(1, 2);
        colIndices2.put(2, 1);
        colIndices2.put(3, 2);
        colIndices2.put(4, 1);
        colIndices2.put(5, 2);
        colIndices2.put(6, 1);
        colIndices2.put(7, 2);
        colIndices2.put(8, 1);
        colIndices2.put(9, 2);
        colIndices2.put(10, 1);
        colIndices2.put(11, 2);
        colIndices2.put(12, 1);
        colIndices2.put(13, 2);
        colIndices2.put(14, 1);

        // create col map for row 3
        Map<Integer, Integer> colIndices3 = new HashMap<>();
        colIndices3.put(0, 1);
        colIndices3.put(1, 3);
        colIndices3.put(2, 0);
        colIndices3.put(3, 2);
        colIndices3.put(4, 1);
        colIndices3.put(5, 2);
        colIndices3.put(6, 1);
        colIndices3.put(7, 0);
        colIndices3.put(8, 1);
        colIndices3.put(9, 2);
        colIndices3.put(10, 1);
        colIndices3.put(11, 2);
        colIndices3.put(12, 0);
        colIndices3.put(13, 3);
        colIndices3.put(14, 1);

        // create col map for row 4
        Map<Integer, Integer> colIndices4 = new HashMap<>();
        colIndices4.put(0, 1);
        colIndices4.put(1, 1);
        colIndices4.put(2, 1);
        colIndices4.put(3, 1);
        colIndices4.put(4, 1);
        colIndices4.put(5, 2);
        colIndices4.put(6, 1);
        colIndices4.put(7, 2);
        colIndices4.put(8, 1);
        colIndices4.put(9, 2);
        colIndices4.put(10, 1);
        colIndices4.put(11, 1);
        colIndices4.put(12, 1);
        colIndices4.put(13, 1);
        colIndices4.put(14, 1);

        // create col map for row 5
        Map<Integer, Integer> colIndices5 = new HashMap<>();
        colIndices5.put(0, 1);
        colIndices5.put(1, 0);
        colIndices5.put(2, 0);
        colIndices5.put(3, 0);
        colIndices5.put(4, 2);
        colIndices5.put(5, 0);
        colIndices5.put(6, 4);
        colIndices5.put(7, 2);
        colIndices5.put(8, 4);
        colIndices5.put(9, 0);
        colIndices5.put(10, 2);
        colIndices5.put(11, 0);
        colIndices5.put(12, 0);
        colIndices5.put(13, 0);
        colIndices5.put(14, 1);

        // create col map for row 6
        Map<Integer, Integer> colIndices6 = new HashMap<>();
        colIndices6.put(0, 1);
        colIndices6.put(1, 3);
        colIndices6.put(2, 1);
        colIndices6.put(3, 1);
        colIndices6.put(4, 1);
        colIndices6.put(5, 1);
        colIndices6.put(6, 1);
        colIndices6.put(7, 2);
        colIndices6.put(8, 1);
        colIndices6.put(9, 1);
        colIndices6.put(10, 1);
        colIndices6.put(11, 1);
        colIndices6.put(12, 1);
        colIndices6.put(13, 3);
        colIndices6.put(14, 1);

        // create col map for row 7
        Map<Integer, Integer> colIndices7 = new HashMap<>();
        colIndices7.put(0, 1);
        colIndices7.put(1, 0);
        colIndices7.put(2, 0);
        colIndices7.put(3, 0);
        colIndices7.put(4, 2);
        colIndices7.put(5, 5);
        colIndices7.put(6, 0);
        colIndices7.put(7, 2);
        colIndices7.put(8, 0);
        colIndices7.put(9, 5);
        colIndices7.put(10, 2);
        colIndices7.put(11, 0);
        colIndices7.put(12, 0);
        colIndices7.put(13, 0);
        colIndices7.put(14, 1);

        // create col map for row 8
        Map<Integer, Integer> colIndices8 = new HashMap<>();
        colIndices8.put(0, 1);
        colIndices8.put(1, 1);
        colIndices8.put(2, 1);
        colIndices8.put(3, 1);
        colIndices8.put(4, 1);
        colIndices8.put(5, 2);
        colIndices8.put(6, 1);
        colIndices8.put(7, 2);
        colIndices8.put(8, 1);
        colIndices8.put(9, 2);
        colIndices8.put(10, 1);
        colIndices8.put(11, 1);
        colIndices8.put(12, 1);
        colIndices8.put(13, 1);
        colIndices8.put(14, 1);

        // create col map for row 9
        Map<Integer, Integer> colIndices9 = new HashMap<>();
        colIndices9.put(0, 1);
        colIndices9.put(1, 2);
        colIndices9.put(2, 0);
        colIndices9.put(3, 2);
        colIndices9.put(4, 1);
        colIndices9.put(5, 2);
        colIndices9.put(6, 1);
        colIndices9.put(7, 0);
        colIndices9.put(8, 1);
        colIndices9.put(9, 2);
        colIndices9.put(10, 1);
        colIndices9.put(11, 2);
        colIndices9.put(12, 0);
        colIndices9.put(13, 2);
        colIndices9.put(14, 1);

        // create col map for row 10
        Map<Integer, Integer> colIndices10 = new HashMap<>();
        colIndices10.put(0, 1);
        colIndices10.put(1, 2);
        colIndices10.put(2, 1);
        colIndices10.put(3, 2);
        colIndices10.put(4, 1);
        colIndices10.put(5, 2);
        colIndices10.put(6, 1);
        colIndices10.put(7, 2);
        colIndices10.put(8, 1);
        colIndices10.put(9, 2);
        colIndices10.put(10, 1);
        colIndices10.put(11, 2);
        colIndices10.put(12, 1);
        colIndices10.put(13, 2);
        colIndices10.put(14, 1);

        // create col map for row 11
        Map<Integer, Integer> colIndices11 = new HashMap<>();
        colIndices11.put(0, 1);
        colIndices11.put(1, 3);
        colIndices11.put(2, 0);
        colIndices11.put(3, 2);
        colIndices11.put(4, 0);
        colIndices11.put(5, 4);
        colIndices11.put(6, 0);
        colIndices11.put(7, 2);
        colIndices11.put(8, 0);
        colIndices11.put(9, 4);
        colIndices11.put(10, 0);
        colIndices11.put(11, 2);
        colIndices11.put(12, 0);
        colIndices11.put(13, 3);
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

    // Level 7: get list of maps with data for each square
    private List<Map<Integer, Integer>> getSquareDataMapsLevel7() {
        // each map represents a row
        // each entry contains col# and integer used to assign interior (wall, monster, etc)


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
        colIndices1.put(4, 2);
        colIndices1.put(5, 2);
        colIndices1.put(6, 0);
        colIndices1.put(7, 0);
        colIndices1.put(8, 0);
        colIndices1.put(9, 2);
        colIndices1.put(10, 2);
        colIndices1.put(11, 0);
        colIndices1.put(12, 3);
        colIndices1.put(13, 0);
        colIndices1.put(14, 1);

        // create col map for row 2
        Map<Integer, Integer> colIndices2 = new HashMap<>();
        colIndices2.put(0, 1);
        colIndices2.put(1, 0);
        colIndices2.put(2, 1);
        colIndices2.put(3, 2);
        colIndices2.put(4, 1);
        colIndices2.put(5, 2);
        colIndices2.put(6, 1);
        colIndices2.put(7, 5);
        colIndices2.put(8, 1);
        colIndices2.put(9, 2);
        colIndices2.put(10, 1);
        colIndices2.put(11, 2);
        colIndices2.put(12, 1);
        colIndices2.put(13, 2);
        colIndices2.put(14, 1);

        // create col map for row 3
        Map<Integer, Integer> colIndices3 = new HashMap<>();
        colIndices3.put(0, 1);
        colIndices3.put(1, 2);
        colIndices3.put(2, 2);
        colIndices3.put(3, 0);
        colIndices3.put(4, 4);
        colIndices3.put(5, 0);
        colIndices3.put(6, 2);
        colIndices3.put(7, 2);
        colIndices3.put(8, 2);
        colIndices3.put(9, 0);
        colIndices3.put(10, 4);
        colIndices3.put(11, 0);
        colIndices3.put(12, 2);
        colIndices3.put(13, 2);
        colIndices3.put(14, 1);

        // create col map for row 4
        Map<Integer, Integer> colIndices4 = new HashMap<>();
        colIndices4.put(0, 1);
        colIndices4.put(1, 2);
        colIndices4.put(2, 1);
        colIndices4.put(3, 2);
        colIndices4.put(4, 1);
        colIndices4.put(5, 2);
        colIndices4.put(6, 1);
        colIndices4.put(7, 2);
        colIndices4.put(8, 1);
        colIndices4.put(9, 2);
        colIndices4.put(10, 1);
        colIndices4.put(11, 2);
        colIndices4.put(12, 1);
        colIndices4.put(13, 2);
        colIndices4.put(14, 1);

        // create col map for row 5
        Map<Integer, Integer> colIndices5 = new HashMap<>();
        colIndices5.put(0, 1);
        colIndices5.put(1, 2);
        colIndices5.put(2, 0);
        colIndices5.put(3, 2);
        colIndices5.put(4, 0);
        colIndices5.put(5, 2);
        colIndices5.put(6, 0);
        colIndices5.put(7, 2);
        colIndices5.put(8, 0);
        colIndices5.put(9, 2);
        colIndices5.put(10, 0);
        colIndices5.put(11, 2);
        colIndices5.put(12, 0);
        colIndices5.put(13, 2);
        colIndices5.put(14, 1);

        // create col map for row 6
        Map<Integer, Integer> colIndices6 = new HashMap<>();
        colIndices6.put(0, 1);
        colIndices6.put(1, 1);
        colIndices6.put(2, 3);
        colIndices6.put(3, 1);
        colIndices6.put(4, 4);
        colIndices6.put(5, 1);
        colIndices6.put(6, 3);
        colIndices6.put(7, 1);
        colIndices6.put(8, 3);
        colIndices6.put(9, 1);
        colIndices6.put(10, 4);
        colIndices6.put(11, 1);
        colIndices6.put(12, 3);
        colIndices6.put(13, 1);
        colIndices6.put(14, 1);

        // create col map for row 7
        Map<Integer, Integer> colIndices7 = new HashMap<>();
        colIndices7.put(0, 1);
        colIndices7.put(1, 2);
        colIndices7.put(2, 2);
        colIndices7.put(3, 0);
        colIndices7.put(4, 2);
        colIndices7.put(5, 0);
        colIndices7.put(6, 2);
        colIndices7.put(7, 0);
        colIndices7.put(8, 2);
        colIndices7.put(9, 0);
        colIndices7.put(10, 2);
        colIndices7.put(11, 0);
        colIndices7.put(12, 2);
        colIndices7.put(13, 2);
        colIndices7.put(14, 1);

        // create col map for row 8
        Map<Integer, Integer> colIndices8 = new HashMap<>();
        colIndices8.put(0, 1);
        colIndices8.put(1, 2);
        colIndices8.put(2, 1);
        colIndices8.put(3, 5);
        colIndices8.put(4, 1);
        colIndices8.put(5, 4);
        colIndices8.put(6, 1);
        colIndices8.put(7, 5);
        colIndices8.put(8, 1);
        colIndices8.put(9, 4);
        colIndices8.put(10, 1);
        colIndices8.put(11, 5);
        colIndices8.put(12, 1);
        colIndices8.put(13, 2);
        colIndices8.put(14, 1);

        // create col map for row 9
        Map<Integer, Integer> colIndices9 = new HashMap<>();
        colIndices9.put(0, 1);
        colIndices9.put(1, 2);
        colIndices9.put(2, 2);
        colIndices9.put(3, 0);
        colIndices9.put(4, 2);
        colIndices9.put(5, 2);
        colIndices9.put(6, 2);
        colIndices9.put(7, 0);
        colIndices9.put(8, 2);
        colIndices9.put(9, 2);
        colIndices9.put(10, 2);
        colIndices9.put(11, 0);
        colIndices9.put(12, 2);
        colIndices9.put(13, 2);
        colIndices9.put(14, 1);

        // create col map for row 10
        Map<Integer, Integer> colIndices10 = new HashMap<>();
        colIndices10.put(0, 1);
        colIndices10.put(1, 0);
        colIndices10.put(2, 1);
        colIndices10.put(3, 0);
        colIndices10.put(4, 1);
        colIndices10.put(5, 0);
        colIndices10.put(6, 1);
        colIndices10.put(7, 0);
        colIndices10.put(8, 1);
        colIndices10.put(9, 0);
        colIndices10.put(10, 1);
        colIndices10.put(11, 0);
        colIndices10.put(12, 1);
        colIndices10.put(13, 0);
        colIndices10.put(14, 1);

        // create col map for row 11
        Map<Integer, Integer> colIndices11 = new HashMap<>();
        colIndices11.put(0, 1);
        colIndices11.put(1, 4);
        colIndices11.put(2, 2);
        colIndices11.put(3, 0);
        colIndices11.put(4, 2);
        colIndices11.put(5, 3);
        colIndices11.put(6, 2);
        colIndices11.put(7, 0);
        colIndices11.put(8, 2);
        colIndices11.put(9, 3);
        colIndices11.put(10, 2);
        colIndices11.put(11, 0);
        colIndices11.put(12, 2);
        colIndices11.put(13, 4);
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
