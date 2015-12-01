package superBomberman.mvc.model;

import java.util.*;

import superBomberman.mvc.model.Wall.WallType;

/**
 * Created by RAM0N on 11/28/15.
 */
public class GameBoard {

    public static final int ROW_COUNT = 13;
    public static final int COL_COUNT = 15;
    public static final int LEFT_OUTER_WALL_COL = 0;
    public static final int RIGHT_OUTER_WALL_COL = 14;
    public static final int BOTTOM_OUTER_WALL_ROW = 0;
    public static final int TOP_OUTER_WALL_ROW = 12;

    //private Square[] mSquares;
    private int mLevel;
    private List<Square> mSquares;
    private List<PowerUp> mPowerUps;
    private List<Monster> mMonsters;
    private List<Wall> mBreakableWalls;

    public GameBoard(int level) {

        // assign level
        mLevel = level;

        // initialize square array
        mSquares = new ArrayList<>(ROW_COUNT * COL_COUNT);

        // initialize empty list for powerUps
        mPowerUps = new ArrayList<>();
        mMonsters = new ArrayList<>();
        mBreakableWalls = new ArrayList<>(ROW_COUNT * COL_COUNT);

        createSquares();
        createLevel1();
        createExit();

    }

    private void createSquares() {
        for (int iRow = 0; iRow < ROW_COUNT; iRow++) {
            for (int iCol = 0; iCol < COL_COUNT; iCol++) {
                mSquares.add(new Square(iRow, iCol));
            }
        }
    }
//
//    private void createExteriorWalls() {
//        for (Square square : mSquares) {
//            int iRow = square.getRow();
//            int iCol = square.getColumn();
//            if (iRow == BOTTOM_OUTER_WALL_ROW || iRow == TOP_OUTER_WALL_ROW
//                    || iCol == LEFT_OUTER_WALL_COL || iCol == RIGHT_OUTER_WALL_COL) {
//                Wall wall = new Wall(square, Wall.WallType.SOLID);
//                square.addWall();
//                square.setInside(wall);
//                CommandCenter.getInstance().getOpsList().enqueue(wall, CollisionOp.Operation.ADD);
//            }
//        }
//    }

//    private void createLevel1() {
//        if (mLevel == 1)
//            createWallsForLevel1();
//
//        // ADD CALLS & CODE FOR OTHER LEVELS
//
//    }


//    private void createWallsForLevel1() {
//        for (Square square : mSquares) {
//            int iRow = square.getRow();
//            int iCol = square.getColumn();
//
//
//            // inner solid walls
//            // add shuffle to solid walls
//            if ((iRow == 2 && (iCol == 2 || iCol == 4 || iCol == 5 || iCol == 6 || iCol == 8 || iCol == 10 || iCol == 12))
//                    || (iRow == 3 && (iCol == 1 || iCol == 9))
//                    || (iRow == 4 && (iCol == 2 || iCol == 4 || iCol == 6 || iCol == 8 || iCol == 10 || iCol == 12))
//                    || (iRow == 5 && (iCol == 3))
//                    || (iRow == 6 && (iCol == 2 || iCol == 4 || iCol == 6 || iCol == 8 || iCol == 9 || iCol == 10 || iCol == 12))
//                    || (iRow == 7 && (iCol == 4 || iCol == 10))
//                    || (iRow == 8 && (iCol == 2 || iCol == 4 || iCol == 6 || iCol == 8 || iCol == 9 || iCol == 10 || iCol == 12))
//                    || (iRow == 10 && (iCol == 2 || iCol == 4 || iCol == 6 || iCol == 8 || iCol == 10 || iCol == 12))) {
//
//                // create wall, add to square, add to OpsList
//                Wall wall = new Wall(square, Wall.WallType.SOLID);
//                square.addWall();
//                square.setInside(wall);
//                CommandCenter.getInstance().getOpsList().enqueue(wall, CollisionOp.Operation.ADD);
//            }
//
//            // inner breakable walls
//            // add shuffle to rows
//            if ((iRow == 1 && (iCol == 8 || iCol == 9))
//                    || (iRow == 2 && (iCol == 3 || iCol == 7 || iCol == 9))
//                    || (iRow == 3 && (iCol == 2 || iCol == 3 || iCol == 4 || iCol == 10))
//                    || (iRow == 4 && (iCol == 1))
//                    || (iRow == 5 && (iCol == 1 || iCol == 2 || iCol == 7 || iCol == 8 || iCol == 11 || iCol == 13))
//                    || (iRow == 6 && (iCol == 7 || iCol == 11 || iCol == 13))
//                    || (iRow == 7 && (iCol == 7 || iCol == 9))
//                    || (iRow == 8 && (iCol == 5 || iCol == 7))
//                    || (iRow == 9 && (iCol == 1 || iCol == 4 || iCol == 5 || iCol == 6 || iCol == 9 || iCol == 10 || iCol == 12))
//                    || (iRow == 10 && (iCol == 3))) {
//
//                // create wall, add to square, add to OpsList
//                Wall wall = new Wall(square, Wall.WallType.BREAKABLE);
//                mBreakableWalls.add(wall);
//                square.addWall();
//                square.setInside(wall);
//                CommandCenter.getInstance().getOpsList().enqueue(wall, CollisionOp.Operation.ADD);
//            }
////
////            // add exit
////            // shuffle among breakable walls
////            if ((iRow == 1 && iCol == 8)) {
////                square.addExit();
////            }
//
//        }
//
//    }
//
//    private void createMonsters() {
//        if (mLevel == 1)
//            createMonstersLevel1();
//
//        // ADD CALLS & CODE FOR OTHER LEVELS
//    }

//    private void createMonstersLevel1() {
//
//        int iMonsterCount = 3;
//
//        for (int i = 0; i < iMonsterCount; i++) {
//            List<Square> possibleMonsterSquares = getOpenSquares();
//            Collections.shuffle(possibleMonsterSquares);
//            Monster monster = new Monster(possibleMonsterSquares.get(i));
//            mMonsters.add(monster);
//            CommandCenter.getInstance().getOpsList().enqueue(monster, CollisionOp.Operation.ADD);
//            System.out.println("Monster added: " + possibleMonsterSquares.get(i));
//        }
//
////        for (Square square : mSquares) {
////            int iRow = square.getRow();
////            int iCol = square.getColumn();
////            if ((iRow == 8 && (iCol == 13 || iCol == 1)) || (iRow == 6 && (iCol == 5))) {
////                Monster monster = new Monster(square);
////                mMonsters.add(monster);
////                CommandCenter.getInstance().getOpsList().enqueue(monster, CollisionOp.Operation.ADD);
////            }
////        }
//    }

    private List<Square> getOpenSquares() {
        List<Square> openSquares = new ArrayList<>();
        for (Square square : mSquares) {
            if (!square.isWall())
                openSquares.add(square);
        }

        Iterator<Square> squareIterator = openSquares.iterator();
        while (squareIterator.hasNext()) {
            Square square = squareIterator.next();
            if (square.getColumn() < 5 && square.getRow() < 5)
                squareIterator.remove();
        }

        return openSquares;
    }


    private void createPowerUps(int powerUpBombCount, int powerUpBlastCount, int monsterSourceCount) {
        if ((powerUpBombCount + powerUpBlastCount - monsterSourceCount) > mBreakableWalls.size() || monsterSourceCount > mMonsters.size())
            throw new IllegalArgumentException("power up sources exceed available monsters & walls");

        for (int i = 0; i < powerUpBombCount; i++) {
            mPowerUps.add(new PowerUpBomb());
        }

        for (int i = 0; i < powerUpBlastCount; i++) {
            mPowerUps.add(new PowerUpBlast());
        }

        Collections.shuffle(mPowerUps);
        Collections.shuffle(mMonsters);
        Collections.shuffle(mBreakableWalls);

        int iSourceMonsterCount = monsterSourceCount;
        for (int i = 0; i < iSourceMonsterCount; i++) {
            mMonsters.get(i).setPowerUpInside(mPowerUps.get(0));
            mPowerUps.remove(0);
        }

        int iRemainingPowerUpCount = mPowerUps.size();
        for (int i = 0; i < iRemainingPowerUpCount; i++) {
            mBreakableWalls.get(i).setPowerUpInside(mPowerUps.get(0));
            mPowerUps.remove(0);
        }

    }

    private void createExit() {
        Collections.shuffle(mSquares);
        for (Square square : mSquares) {
            if (square.isWall() && !square.isSolidWall()) {
                if (!((Wall) square.getInside()).containsPowerUp()) {
                    square.addExit();
                    break;
                }
            }
        }
    }

    public Square getSquare(int row, int col) {
        for (Square square : mSquares)
            if (square.getRow() == row && square.getColumn() == col)
                return square;
        throw new IllegalArgumentException("Square not found");
    }

    public List<Square> getSquares() {
        return mSquares;
    }


    public void createLevel1() {

        int iInteriorFirstRow = 1;
        int iInteriorLastRow = ROW_COUNT - 1;
        int iInteriorRowCount = iInteriorLastRow - iInteriorFirstRow;

        // create list of row indices
        List<Integer> rowIndices = new ArrayList<>(ROW_COUNT);
        List<Integer> interiorRowIndices = new ArrayList<>(iInteriorRowCount);
        for (int iRowIndex = 0; iRowIndex < ROW_COUNT; iRowIndex++) {
            rowIndices.add(iRowIndex);
            if (iRowIndex >= iInteriorFirstRow && iRowIndex < iInteriorLastRow) {
                interiorRowIndices.add(iRowIndex);
                System.out.println("added interior row: " + iRowIndex);
            }
        }

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
        colIndices1.put(5, 0);
        colIndices1.put(6, 0);
        colIndices1.put(7, 2);
        colIndices1.put(8, 2);
        colIndices1.put(9, 0);
        colIndices1.put(10, 0);
        colIndices1.put(11, 0);
        colIndices1.put(12, 0);
        colIndices1.put(13, 0);
        colIndices1.put(14, 1);

        // create col map for row 2
        Map<Integer, Integer> colIndices2 = new HashMap<>();
        colIndices2.put(0, 1);
        colIndices2.put(1, 0);
        colIndices2.put(2, 1);
        colIndices2.put(3, 2);
        colIndices2.put(4, 1);
        colIndices2.put(5, 1);
        colIndices2.put(6, 1);
        colIndices2.put(7, 2);
        colIndices2.put(8, 1);
        colIndices2.put(9, 2);
        colIndices2.put(10, 1);
        colIndices2.put(11, 0);
        colIndices2.put(12, 1);
        colIndices2.put(13, 0);
        colIndices2.put(14, 1);

        // create col map for row 3
        Map<Integer, Integer> colIndices3 = new HashMap<>();
        colIndices3.put(0, 1);
        colIndices3.put(1, 1);
        colIndices3.put(2, 2);
        colIndices3.put(3, 2);
        colIndices3.put(4, 2);
        colIndices3.put(5, 0);
        colIndices3.put(6, 0);
        colIndices3.put(7, 1);
        colIndices3.put(8, 0);
        colIndices3.put(9, 1);
        colIndices3.put(10, 2);
        colIndices3.put(11, 0);
        colIndices3.put(12, 0);
        colIndices3.put(13, 0);
        colIndices3.put(14, 1);

        // create col map for row 4
        Map<Integer, Integer> colIndices4 = new HashMap<>();
        colIndices4.put(0, 1);
        colIndices4.put(1, 2);
        colIndices4.put(2, 1);
        colIndices4.put(3, 0);
        colIndices4.put(4, 1);
        colIndices4.put(5, 0);
        colIndices4.put(6, 1);
        colIndices4.put(7, 0);
        colIndices4.put(8, 2);
        colIndices4.put(9, 0);
        colIndices4.put(10, 1);
        colIndices4.put(11, 0);
        colIndices4.put(12, 1);
        colIndices4.put(13, 0);
        colIndices4.put(14, 1);

        // create col map for row 5
        Map<Integer, Integer> colIndices5 = new HashMap<>();
        colIndices5.put(0, 1);
        colIndices5.put(1, 2);
        colIndices5.put(2, 2);
        colIndices5.put(3, 1);
        colIndices5.put(4, 0);
        colIndices5.put(5, 2);
        colIndices5.put(6, 0);
        colIndices5.put(7, 0);
        colIndices5.put(8, 3);
        colIndices5.put(9, 0);
        colIndices5.put(10, 0);
        colIndices5.put(11, 2);
        colIndices5.put(12, 0);
        colIndices5.put(13, 2);
        colIndices5.put(14, 1);

        // create col map for row 6
        Map<Integer, Integer> colIndices6 = new HashMap<>();
        colIndices6.put(0, 1);
        colIndices6.put(1, 0);
        colIndices6.put(2, 1);
        colIndices6.put(3, 0);
        colIndices6.put(4, 1);
        colIndices6.put(5, 0);
        colIndices6.put(6, 1);
        colIndices6.put(7, 2);
        colIndices6.put(8, 1);
        colIndices6.put(9, 1);
        colIndices6.put(10, 1);
        colIndices6.put(11, 2);
        colIndices6.put(12, 1);
        colIndices6.put(13, 2);
        colIndices6.put(14, 1);

        // create col map for row 7
        Map<Integer, Integer> colIndices7 = new HashMap<>();
        colIndices7.put(0, 1);
        colIndices7.put(1, 3);
        colIndices7.put(2, 0);
        colIndices7.put(3, 0);
        colIndices7.put(4, 1);
        colIndices7.put(5, 0);
        colIndices7.put(6, 0);
        colIndices7.put(7, 2);
        colIndices7.put(8, 0);
        colIndices7.put(9, 0);
        colIndices7.put(10, 1);
        colIndices7.put(11, 0);
        colIndices7.put(12, 0);
        colIndices7.put(13, 0);
        colIndices7.put(14, 1);

        // create col map for row 8
        Map<Integer, Integer> colIndices8 = new HashMap<>();
        colIndices8.put(0, 1);
        colIndices8.put(1, 0);
        colIndices8.put(2, 1);
        colIndices8.put(3, 0);
        colIndices8.put(4, 1);
        colIndices8.put(5, 2);
        colIndices8.put(6, 1);
        colIndices8.put(7, 2);
        colIndices8.put(8, 1);
        colIndices8.put(9, 1);
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
        colIndices9.put(3, 0);
        colIndices9.put(4, 2);
        colIndices9.put(5, 2);
        colIndices9.put(6, 2);
        colIndices9.put(7, 0);
        colIndices9.put(8, 0);
        colIndices9.put(9, 2);
        colIndices9.put(10, 2);
        colIndices9.put(11, 0);
        colIndices9.put(12, 2);
        colIndices9.put(13, 0);
        colIndices9.put(14, 1);

        // create col map for row 10
        Map<Integer, Integer> colIndices10 = new HashMap<>();
        colIndices10.put(0, 1);
        colIndices10.put(1, 0);
        colIndices10.put(2, 1);
        colIndices10.put(3, 2);
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
        colIndices11.put(1, 0);
        colIndices11.put(2, 0);
        colIndices11.put(3, 0);
        colIndices11.put(4, 0);
        colIndices11.put(5, 0);
        colIndices11.put(6, 0);
        colIndices11.put(7, 0);
        colIndices11.put(8, 0);
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


        // loop through each row, then add wall based on entry in matching column hash map
        for (int i = 0; i < ROW_COUNT; i++) {
            System.out.println("creating row: " + i);
            for (Map.Entry<Integer, Integer> entry : colIndexMaps.get(i).entrySet()) {
                //Square square = new Square(rowIndices.get(i), entry.getKey());
                //mSquares.add(square);
                Square square = getSquare(rowIndices.get(i), entry.getKey());

                if (entry.getValue() == 1 || entry.getValue() == 2) {
                    WallType wallType = (entry.getValue() == 1) ? WallType.SOLID : WallType.BREAKABLE;
                    Wall wall = new Wall(square, wallType);
                    square.setInside(wall);
                    square.addWall();
                    if (wallType == WallType.BREAKABLE)
                        mBreakableWalls.add(wall);
                    CommandCenter.getInstance().getOpsList().enqueue(wall, CollisionOp.Operation.ADD);
                } else if (entry.getValue() == 3) {
                    Monster monster = new Monster(square);
                    mMonsters.add(monster);
                    CommandCenter.getInstance().getOpsList().enqueue(monster, CollisionOp.Operation.ADD);
                    System.out.println("Monster added: " + square);

                }
            }
        }

        int iPowerUpBombCount = 4;
        int iPowerUpBlastCount = 4;
        int iMonsterSourceCount = 2;
        createPowerUps(iPowerUpBombCount,iPowerUpBlastCount,iMonsterSourceCount);
    }


}
